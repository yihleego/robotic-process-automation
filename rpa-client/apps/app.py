import json
import logging
import os
import signal
import time
import winreg
from abc import ABC
from datetime import datetime
from urllib.parse import quote
from urllib.request import urlopen

import psutil
import pyperclip
import win32con
import win32gui
import win32process
from PIL import ImageGrab
from pywinauto.application import Application
from pywinauto.controls.hwndwrapper import HwndWrapper
from pywinauto.findwindows import find_elements

import airtest.core.api
import airtest.core.win
from airtest.core.cv import Template
from handler import handler


class ClientStatus:
    OFFLINE = 0
    ONLINE = 1
    WAITING = 2
    ERROR = 3
    SUSPENDED = 4


class TaskStatus:
    CREATED = 0
    RUNNING = 1
    DELETED = 2
    CANCELLED = 3
    FINISHED = 10
    FAILED = 11
    TIMEOUT = 12
    OFFLINE = 13
    TERMINATED = 14
    UNSUPPORTED = 15


class MessageType:
    TEXT = 'text'
    IMAGE = 'image'
    VIDEO = 'video'
    FILE = 'file'
    MENTION = 'mention'


class AppConfig:
    def __init__(self, app_name, login_class_name, main_class_name, process_name, mutex_names, registry_path, registry_key):
        self.app_name = app_name
        self.login_class_name = login_class_name
        self.main_class_name = main_class_name
        self.process_name = process_name
        self.mutex_names = mutex_names
        self.registry_path = registry_path
        self.registry_key = registry_key

    def __str__(self) -> str:
        return f"app_name: {self.app_name}, " \
               f"login_class_name: {self.login_class_name}, " \
               f"main_class_name: {self.main_class_name}, " \
               f"process_name: {self.process_name}, " \
               f"mutex_names: {self.mutex_names}, " \
               f"registry_path: {self.registry_path}, " \
               f"registry_key: {self.registry_key}"


class AppUser:
    def __init__(self, account=None, nickname=None, realname=None, company=None, phone=None, avatar=None):
        self.account = account
        self.nickname = nickname
        self.realname = realname
        self.company = company
        self.phone = phone
        self.avatar = avatar

    def __str__(self) -> str:
        return f"account: {self.account}, " \
               f"nickname: {self.nickname}, " \
               f"realname: {self.realname}, " \
               f"company: {self.company}, " \
               f"phone: {self.phone}, " \
               f"avatar: {self.avatar}"


class App(ABC):
    def __init__(self, handle: int, process: int = None, status: int = None, user: AppUser = None):
        self.handle = handle
        self.process = process
        self.status = status
        self.user = user
        self.root_path = os.path.dirname(__file__)
        self.assets_path = os.path.abspath(os.path.join(self.root_path, '../assets/'))
        self.temps_path = os.path.abspath(os.path.join(self.root_path, '../temps/'))
        self.started_time = self.now()
        self.online_time = None

    @staticmethod
    def config() -> AppConfig:
        raise NotImplementedError

    @classmethod
    def app_id(cls):
        return cls.config().app_name

    @classmethod
    def is_login_window(cls, name: str):
        return cls.config().login_class_name == name

    @classmethod
    def is_main_window(cls, name: str):
        return cls.config().main_class_name == name

    @classmethod
    def find_elements(cls, process=None):
        class_name_re = f'^{cls.config().login_class_name}$|^{cls.config().main_class_name}$'
        return find_elements(class_name_re=class_name_re, process=process)

    @classmethod
    def launch(cls, number: int = 1, default_path=None):
        config = cls.config()
        app_path = None
        if default_path and os.path.exists(default_path):
            # Use custom path
            app_path = default_path
        elif config.registry_path and config.registry_key:
            # Find path from registry
            sub_key = winreg.OpenKeyEx(winreg.HKEY_LOCAL_MACHINE, config.registry_path)
            values = winreg.QueryValueEx(sub_key, config.registry_key)
            if values and values[0]:
                tmp = values[0].strip()
                if tmp.startswith('\"') or tmp.startswith('\''):
                    tmp = tmp[1:]
                if tmp.endswith('\"') or tmp.endswith('\''):
                    tmp = tmp[:-1]
                app_path = tmp
        if not app_path:
            raise Exception(f"Cannot find '{cls.app_id()}' path")

        # Close mutexes before launching
        processes = []
        for i in range(number):
            if config.process_name and config.mutex_names:
                process_ids = []
                for proc in psutil.process_iter(attrs=['name']):
                    if proc.info['name'] == config.process_name:
                        process_ids.append(proc.pid)
                if process_ids:
                    handles = handler.find_handles(process_ids=process_ids, handle_names=config.mutex_names)
                    handler.close_handles(handles)
            p = win32process.CreateProcess(app_path, '', None, None, 0, 0, None, None, win32process.STARTUPINFO())
            if p and len(p) == 4:
                processes.append(p[2])
        return processes

    def find_user(self) -> AppUser:
        raise NotImplementedError

    def execute(self, task_type, task_data=None):
        func = getattr(self, task_type)
        if not func:
            raise NotImplementedError
        return func(task_data)

    def init(self):
        def load_cache():
            title = win32gui.GetWindowText(self.handle)
            if not title or not title.startswith(self.app_id() + ' '):
                return False
            cache = json.loads(title[len(self.app_id()) + 1:])
            user = AppUser()
            for attr in user.__dict__:
                user.__dict__[attr] = cache.get(attr)
            self.user = user
            self.started_time = cache.get('st')
            self.online_time = cache.get('ot')
            return user

        def set_cache():
            u = {k: v for k, v in self.user.__dict__.items() if v is not None}
            if self.started_time:
                u['st'] = self.started_time
            if self.online_time:
                u['ot'] = self.online_time
            title = json.dumps(u, separators=(',', ':'))
            win32gui.SetWindowText(self.handle, self.app_id() + ' ' + title)

        try:
            if load_cache():
                return True
            user = self.find_user()
            if user:
                self.user = user
                self.online_time = self.now()
                set_cache()
                return True
            return False
        except:
            logging.exception('Failed to find user info')
        return False

    def now(self):
        return datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    def __str__(self) -> str:
        return f"handle: {self.handle}, " \
               f"process: {self.process}, " \
               f"status: {self.status}, " \
               f"user: {str(self.user)}, " \
               f"started_time: {self.started_time}, " \
               f"online_time: {self.online_time}"


class Capable:
    def close_handle(self, handle):
        if not handle:
            return
        hwnd = HwndWrapper(handle)
        hwnd.close()

    def kill(self, process):
        if not process:
            return
        os.kill(process, signal.SIGABRT)

    def snapshot(self, handle):
        win32gui.SendMessage(handle, win32con.WM_SYSCOMMAND, win32con.SC_RESTORE, 0)
        win32gui.SetForegroundWindow(handle)
        return ImageGrab.grab((win32gui.GetWindowRect(handle)))

    def download(self, urls, dir_path):
        if not urls or not dir_path:
            return {}
        try:
            os.makedirs(dir_path, exist_ok=True)
            paths = {}
            for url in urls:
                path = "{}\\{}".format(dir_path, os.path.basename(url))
                if os.path.exists(path):
                    logging.debug('The file already exists, url: %s, path: %s', url, path)
                    paths[url] = path
                else:
                    logging.debug('Downloading the file, url: %s, path: %s', url, path)
                    response = urlopen(quote(url, safe='/:?='))
                    if response.getcode() == 200:
                        with open(path, "wb") as f:
                            f.write(response.read())
                            paths[url] = f.name
                            f.close()
                    else:
                        logging.error('Failed to download the file, url: %s', url)
            return paths
        except:
            logging.exception('Failed to download files')
            raise Exception("Failed to download files, urls: " + str(urls))

    def copy(self, v):
        pyperclip.copy(v)

    def paste(self):
        return pyperclip.paste()

    def wait(self, seconds=0):
        if seconds > 0:
            time.sleep(seconds)


class UiaApp(App, Capable):
    def __init__(self, handle: int, process: int = None, status: int = None, user=None):
        super().__init__(handle, process, status, user)

    def connect(self, handle=None, foreground=True, backend='uia'):
        if not handle:
            handle = self.handle
        if foreground:
            window = HwndWrapper(handle)
            window.set_focus()
            window.move_window(0, 0)
        app = Application(backend=backend)
        app.connect(handle=handle)
        return app


class AirApp(App, Capable):
    def __init__(self, handle: int, process: int = None, status: int = None, user=None):
        super().__init__(handle, process, status, user)

    def connect(self, handle=None, foreground=True):
        if not handle:
            handle = self.handle
        device = airtest.core.api.connect_device("Windows:///" + str(handle))
        if foreground:
            device.set_foreground()
            device.move((0, 0))
        return device

    def exists(self, v):
        if isinstance(v, Template):
            return airtest.core.api.exists(v)
        elif isinstance(v, str):
            return airtest.core.api.exists(Template(os.path.join(self.assets_path, self.app_id(), v)))
        elif isinstance(v, tuple):
            return v
        elif isinstance(v, list):
            for i in v:
                r = self.exists(i)
                if r:
                    return r
        else:
            return None

    def click(self, v, wait_time=0):
        ok = self.exists(v)
        if ok:
            airtest.core.api.touch(ok)
            self.wait(wait_time)
            return ok
        else:
            return False

    def double_click(self, v, wait_time=0):
        ok = self.exists(v)
        if ok:
            airtest.core.api.double_click(ok)
            self.wait(wait_time)
            return ok
        else:
            return False

    def key(self, key, wait_time=0):
        airtest.core.api.keyevent(key)
        self.wait(wait_time)
