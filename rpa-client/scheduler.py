import json
import logging
import threading
import time
from queue import PriorityQueue
from typing import Type

from websocket import WebSocketApp

from apps.app import App, ClientStatus, TaskStatus
from apps.dingtalk import DingTalk
from apps.lark import Lark
from apps.qq import QQ
from apps.tim import TIM
from apps.wechat import WeChat
from apps.wecom import WeCom

APPS = [WeChat, WeCom, QQ, TIM, DingTalk, Lark]


class MessageCode:
    HEARTBEAT = 0
    CLIENT_SYNC = 10
    TASK_SYNC = 11
    TASK_REQUEST = 12
    TASK_RESPONSE = 13


class SchedulerStatus:
    INIT = 0
    RUNNING = 1
    STOPPED = 2


class Scheduler:
    """
    Task scheduler for RPA.
    """

    def __init__(self, host='localhost', port=18888, path='/rpa', ssl=False, url=None,
                 app_size: int = 1, app_paths: dict[str, str] = None):
        """
        Scheduler initialization

        Parameters
        ----------
        host: str
            RPA server host. Default is 'localhost'.
        port: int
            RPA server port. Default is '18888'.
        path: str
            RPA server path. Default is '/rpa'.
        url: str
            RPA server url.
        app_size: int
            the maximum number of clients to allow in the pool.
        app_paths: dict[str,str]
            the custom app paths. Or obtain the path from the registry if it doesn't exist.
        """
        self.host = host
        self.port = port
        self.path = path if path.startswith('/') else '/' + path
        self.ssl = ssl
        self.protocol = 'wss' if ssl else 'ws'
        self.url = url or f'{self.protocol}://{self.host}:{self.port}{self.path}'
        self.app_size = app_size or 1
        self.app_paths = app_paths or {}
        self.client_sync_interval = 3 * 60 * 1000
        self.client_sync_timestamp = 0
        self.task_wait_timestamp = 0
        self.task_wait_duration = 5 * 1000
        self.status = SchedulerStatus.INIT
        # Apps: app_id -> Type
        self.apps = {app.app_id(): app for app in APPS}
        # Clients: handle -> app instance
        self.clients: dict[int, App] = {}
        # Handles: app_id -> handles
        self.handles: dict[str, set] = {}
        # Work queue
        self.queue = PriorityQueue()
        # Main lock
        self.main_lock = threading.Lock()
        # WebSocket
        self.ws = WebSocketApp(
            url=self.url,
            on_open=self.on_ws_open,
            on_close=self.on_ws_close,
            on_error=self.on_ws_error,
            on_message=self.on_ws_message)

    def startup(self):
        """
        Run websocket and set the status to running.
        """
        self.status = SchedulerStatus.RUNNING
        self.ws.run_forever()

    def shutdown(self):
        """
        Close websocket and set the status to stopped.
        """
        self.status = SchedulerStatus.STOPPED
        self.ws.close()

    def on_ws_open(self, ws):
        """
        Callback object which is called at opening websocket.

        Parameters
        ----------
        ws: WebSocketApp
            the instance of WebSocketApp.
        """
        logging.info('Connected to server')
        threads = [
            threading.Thread(target=self.loop_check_clients, daemon=True),
            threading.Thread(target=self.loop_request_tasks, daemon=True),
            threading.Thread(target=self.loop_handle_tasks, daemon=True),
        ]
        for t in threads:
            t.start()

    def on_ws_close(self, ws, code, msg):
        """
        Callback object which is called when connection is closed.

        Parameters
        ----------
        ws: WebSocketApp
            the instance of WebSocketApp.
        code: int
            the close status code.
        msg: str
            the close message.
        """
        logging.info('Disconnect from server, code: %s, msg: %s', code, msg)
        self.status = SchedulerStatus.STOPPED

    def on_ws_error(self, ws, error):
        """
        Callback object which is called when we get error.

        Parameters
        ----------
        ws: WebSocketApp
            the instance of WebSocketApp.
        error: Exception
            the exception.
        """
        logging.error('An error has occurred: %s', str(error))

    def on_ws_message(self, ws, message):
        """
        Callback object which is called when received data.

        Parameters
        ----------
        ws: WebSocketApp
            the instance of WebSocketApp.
        message: str
            the message received from the server.
        """
        obj = json.loads(message)
        code = obj.get('code')
        data = obj.get('data')
        if code == MessageCode.TASK_RESPONSE:
            if data:
                self.queue.put_nowait((data['priority'], data))
        elif code == MessageCode.CLIENT_SYNC:
            self.sync_clients(force=True)
        elif code == MessageCode.HEARTBEAT:
            logging.debug('Heartbeat from server')
        else:
            logging.warning('Invalid message: %s', message)

    def lock(self):
        """
        Acquires the lock.

        If the lock is not available then the current thread becomes disabled for thread scheduling purposes
        and lies dormant until the lock has been acquired.
        """
        self.main_lock.acquire()

    def unlock(self):
        """
        Releases the lock.
        """
        self.main_lock.release()

    def loop_check_clients(self):
        """
        Check all clients in an infinite loop.
        """
        while self.status == SchedulerStatus.RUNNING:
            changed = False
            for app_id, app in self.apps.items():
                self.lock()
                try:
                    changed = changed or self.check_clients(app)
                except:
                    logging.exception('Failed to check %s clients', app_id)
                finally:
                    self.unlock()
            self.sync_clients(force=changed)
            time.sleep(1)

    def loop_request_tasks(self):
        """
        Request tasks from the server in an infinite loop.
        """
        while self.status == SchedulerStatus.RUNNING:
            self.wait_task()
            self.lock()
            try:
                self.send_task_request(self.clients.values())
                self.set_task_wait_time(self.task_wait_duration)
            except:
                logging.exception('Failed to execute tasks')
            finally:
                self.unlock()

    def loop_handle_tasks(self):
        """
        Execute tasks if there are tasks received from the server.
        """
        while self.status == SchedulerStatus.RUNNING:
            _, task = self.queue.get()
            self.lock()
            logging.debug('Prepare to execute the task: %s', task)
            task_id = task.get("taskId")
            app_id = task.get('appId')
            account = task.get('account')
            type = task.get('type')
            data = task.get('data')
            login = task.get('login')
            try:
                params = json.loads(data) if data else None
                status = TaskStatus.FINISHED
                result = None
                message = None
                client = self.find_online_client(app_id=app_id, account=account)
                if login:
                    if client is None:
                        offline_client = self.find_offline_client(app_id=app_id)
                        result = offline_client.execute(type, params)
                    else:
                        # Is this a success?
                        message = f'The client is already online'
                else:
                    if client is not None:
                        result = client.execute(type, params)
                    else:
                        status = TaskStatus.OFFLINE
                self.send_task_sync(task_id, status, result, message)
            except NotImplementedError as e:
                logging.exception('Illegal task type: %s', task)
                self.send_task_sync(task_id, TaskStatus.UNSUPPORTED, message=str(e))
            except Exception as e:
                logging.exception('Failed to execute tasks: %s', task)
                self.send_task_sync(task_id, TaskStatus.FAILED, message=str(e))
            finally:
                self.unlock()
                # Prepare to request new tasks if there is no task to execute
                if self.queue.qsize() == 0:
                    self.set_task_wait_time(-1)

    def sync_clients(self, force=False):
        """
        Synchronize all clients to server.

        Parameters
        ----------
        force: bool
            whether to force sync. Default is False.
        """
        try:
            timestamp = self.timestamp()
            if force or timestamp - self.client_sync_timestamp >= self.client_sync_interval:
                self.send_client_sync(self.clients.values())
                self.client_sync_timestamp = timestamp
        except:
            logging.exception('Failed to sync clients')

    def check_clients(self, app: Type[App]):
        """
        Check whether the clients of the specified type have changed.

        Parameters
        ----------
        app: Type[App]
            the specified app class.

        Returns
        -------
        changed: bool
            return True if any client has changed.
        """
        changed = False
        app_id = app.app_id()
        elements = app.find_elements()
        old_handles = self.handles.get(app_id, set())
        new_handles = set([e.handle for e in elements])
        if old_handles == new_handles:
            return changed
        # Replace handles
        self.handles[app_id] = new_handles
        old_diff = old_handles - new_handles
        new_diff = new_handles - old_handles
        # Remove invalid clients
        if len(old_diff) > 0:
            changed = True
            for handle in old_diff:
                client = self.clients.pop(handle)
                logging.debug('Removed a client: %s', str(client))
        # Add new clients
        if len(new_diff) > 0:
            changed = True
            m = {e.handle: e for e in elements}
            for handle in new_diff:
                e = m.get(handle)
                if app.is_login_window(e.class_name):
                    status = ClientStatus.OFFLINE
                elif app.is_main_window(e.class_name):
                    status = ClientStatus.ONLINE
                else:
                    status = ClientStatus.ERROR
                client = app(handle, e.process_id, status)
                if status == ClientStatus.ONLINE and not client.init():
                    client.status = ClientStatus.ERROR
                self.clients[handle] = client
                logging.debug('Added a client: %s', str(client))
        return changed

    def find_online_client(self, app_id, account) -> App:
        """
        Find the online client by the account.

        Parameters
        ----------
        app_id: str
            the specified client type.
        account: str
            the specified account.

        Returns
        -------
        client: App
            return the client, or None if it cannot be found.
        """
        return self.find_client(app_id=app_id, account=account, status=ClientStatus.ONLINE)

    def find_offline_client(self, app_id) -> App:
        """
        Find an offline client if it exists, otherwise start a new client.

        Parameters
        ----------
        app_id:str
            the specified client type.

        Returns
        -------
        client: App
            return an existing offline client, or a newly started client.

        Raises
        ---------
        e: Exception
            If clients failed to start, or the number of client starts has exceeded the limit.
        """
        # Return if there is an offline client
        offline_client = self.find_client(app_id=app_id, status=ClientStatus.OFFLINE)
        if offline_client is not None:
            return offline_client
        # Try to start a new client
        if len(self.clients) >= self.app_size:
            raise Exception('No clients available')
        app = self.find_app(app_id)
        path = self.app_paths.get(app_id)
        processes = app.launch(1, path)
        if not processes:
            raise Exception('No clients available')
        # Find the client that just started
        retry_count = 0
        while retry_count < 5:
            retry_count += 1
            if app.find_elements(process=processes[0]):
                break
            time.sleep(0.5)
        # Check and sync clients
        self.check_clients(app)
        # Return the new client
        offline_client = self.find_client(app_id=app_id, status=ClientStatus.OFFLINE)
        if offline_client is None:
            raise Exception('No clients available')
        offline_client.status = ClientStatus.WAITING
        return offline_client

    def find_client(self, app_id, account=None, status=None) -> App:
        for handle, client in self.clients.items():
            if client.app_id() != app_id:
                continue
            if account is not None:
                if client.user and client.user.account == account:
                    return client
            elif status is not None:
                if client.status == status:
                    return client

    def find_app(self, app_id) -> Type[App]:
        app = self.apps.get(app_id)
        if app:
            return app
        raise Exception(f'Illegal app id \'{app_id}\'')

    def wait_task(self):
        while self.task_wait_timestamp >= self.timestamp():
            time.sleep(0.1)

    def set_task_wait_time(self, milliseconds=0):
        self.task_wait_timestamp = self.timestamp() + milliseconds

    def send_client_sync(self, client_pool):
        clients = []
        for c in client_pool:
            if c.status == ClientStatus.ONLINE:
                clients.append({
                    "appId": c.app_id(),
                    "status": c.status,
                    "handle": c.handle,
                    "process": c.process,
                    "account": c.user.account,
                    "nickname": c.user.nickname,
                    "realname": c.user.realname,
                    "company": c.user.company,
                    "startedTime": c.started_time,
                    "onlineTime": c.online_time})
            else:
                clients.append({
                    "appId": c.app_id(),
                    "status": c.status,
                    "handle": c.handle,
                    "process": c.process,
                    "startedTime": c.started_time})
        data = {"clients": clients}
        return self.send_data(MessageCode.CLIENT_SYNC, data)

    def send_task_request(self, client_pool):
        clients = []
        total_client_size = max(self.app_size, len(client_pool))
        online_client_size = 0
        for c in client_pool:
            if c.status == ClientStatus.ONLINE:
                online_client_size += 1
                clients.append({
                    "appId": c.app_id(),
                    "status": c.status,
                    "handle": c.handle,
                    "process": c.process,
                    "account": c.user.account,
                    "nickname": c.user.nickname,
                    "realname": c.user.realname,
                    "company": c.user.company})
        data = {
            "clients": clients,
            "totalClientSize": total_client_size,
            "idleClientSize": total_client_size - online_client_size}
        return self.send_data(MessageCode.TASK_REQUEST, data)

    def send_task_sync(self, task_id, status, result=None, message=None):
        if result and isinstance(result, dict):
            result = json.dumps(result)
        data = {
            "taskId": task_id,
            "status": status,
            "result": result,
            "message": message}
        return self.send_data(MessageCode.TASK_SYNC, data)

    def send_data(self, code, data):
        try:
            self.ws.send(json.dumps({"code": code, "data": data}))
        except:
            logging.exception('Failed to send message')

    def timestamp(self):
        return int(time.time() * 1000)
