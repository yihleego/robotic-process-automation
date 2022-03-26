import logging
from datetime import datetime

from pywinauto.findwindows import find_elements, find_windows, find_window
from pyzbar import pyzbar

from apps.app import AirApp, AppConfig, AppUser, TaskMessageType


class WeCom(AirApp):
    def __init__(self, handle: int, process: int = None, status: int = None, user=None):
        super().__init__(handle, process, status, user)
        self.version = "3.1.11.3009"

    @staticmethod
    def config():
        return AppConfig(
            app_name="wecom",
            login_class_name="WeChatLogin",
            main_class_name="WeWorkWindow",
            process_name="WXWork.exe",
            mutex_names=[r"\Tencent.WeWork.ExclusiveObject", r"\Tencent.WeWork.ExclusiveObjectInstance1"],
            registry_path=r"SOFTWARE\WOW6432Node\Microsoft\Windows\CurrentVersion\Uninstall\企业微信",
            registry_key="DisplayIcon")

    def find_user(self):
        config_handle = None
        edit_handle = None
        try:
            self.connect(self.handle)
            # Click menu button
            self.click(['main_menu_btn.png', 'main_menu_btn_hover.png'], 1)
            menu_handle = find_window(class_name="DuiMenuWnd", process=self.process)
            if not menu_handle:
                return None
            self.connect(menu_handle, foreground=False)
            # Click config menu item
            self.click('menu_config_menu_item.png', 1)
            config_handle = find_window(class_name='ConfigWindow', process=self.process)
            if not config_handle:
                return None
            self.connect(config_handle)
            # Click userinfo button
            label_userinfo_pos = self.click(['config_userinfo_selected_btn.png', 'config_userinfo_btn.png'], 1)
            if not label_userinfo_pos:
                return None
            # Find company
            self.double_click((label_userinfo_pos[0] + 139, label_userinfo_pos[1] + 21), 0.5)
            self.copy("")
            self.key("^c")
            company = self.paste()
            if company is None:
                return None
            # Find nickname
            self.click('config_edit_btn.png', 1)
            edit_handle = find_window(class_name='ModifyUserInfoWindow', process=self.process)
            if not edit_handle:
                return None
            self.connect(edit_handle)
            label_name_pos = self.exists('config_name_label.png')
            if not label_name_pos:
                return None
            self.click((label_name_pos[0] + 100, label_name_pos[1] + 0), 0.5)
            self.copy("")
            self.key("^a^c")
            nickname = self.paste()
            if nickname is None:
                return None
            return AppUser(account=company + '_' + nickname, nickname=nickname, company=company)
        finally:
            self.close_handle(config_handle)
            self.close_handle(edit_handle)

    def login(self, data=None):
        logging.debug('Login, handle: %d', self.handle)
        self.connect(self.handle)
        self.click(['login_refresh_qrcode_btn.png', 'login_switch_account_btn.png', 'login_back_btn.png'], 1)
        snapshot = self.snapshot(self.handle)
        decoded = pyzbar.decode(snapshot)
        if decoded and decoded[0]:
            return {'qrcode': decoded[0].data.decode('utf-8')}
        raise Exception("Unable to get QR code")

    def logout(self, data=None):
        logging.debug('Logout, handle: %d', self.handle)
        self.close_handle(self.handle)

    def send_private_messages(self, data):
        logging.debug('Send private messages, handle: %d, data: %s', self.handle, data)
        # Validate data before sending messages
        target, messages, file_paths = self._check_messages(data)
        self.connect(self.handle)
        # Click contact button
        self.click(['main_nav_contact_btn.png', 'main_nav_contact_btn_hover.png', 'main_nav_contact_btn_focus.png'])
        # Search the user
        self.click('main_search_clear_btn.png', 0.5)
        self.click(['main_search_input1.png', 'main_search_input2.png', 'main_search_input3.png'], 0.5)
        self.copy(target)
        self.key("^v", 1)
        # Throw if the user cannot be found
        if self.exists('main_search_no_result.png'):
            raise Exception('User cannot be found')
        self.key('{ENTER}', 0.5)
        # Send messages
        self._send_messages(target, messages, file_paths)
        # Throw if there is any error popup windows
        if not self._check_error_popup():
            raise Exception("Failed to send messages")

    def send_group_messages(self, data):
        logging.debug('Send group messages, handle: %d, data: %s', self.handle, data)
        # Validate data before sending messages
        target, messages, file_paths = self._check_messages(data)
        self.connect(self.handle)
        # Close existing message manager
        message_manager_handles = find_windows(class_name='MsgManagerWindow')
        if message_manager_handles:
            for handle in message_manager_handles:
                self.close_handle(handle)
        # Open menu
        self.click(['main_menu_btn.png', 'main_menu_btn_hover.png'], 1)
        menu_handle = find_window(class_name="DuiMenuWnd")
        if not menu_handle:
            raise Exception("Failed to open menu")
        self.connect(menu_handle, foreground=False)
        # Open message manager
        self.click('menu_message_manager_menu_item.png', 1)
        message_manager_handle = find_window(class_name='MsgManagerWindow')
        if not message_manager_handle:
            raise Exception("Failed to open message manager")
        self.connect(message_manager_handle)
        # Click dropdown button and search group
        self.click('message_manager_dropdown_btn.png', 0.5)
        self.click('message_manager_group_name_input.png', 0.5)
        self.copy(target)
        self.key("^v", 0.5)
        self.key("{ENTER}", 0.5)
        # Throw if the group cannot be found
        if self.exists('message_manager_no_result.png'):
            raise Exception('Group cannot be found')
        # Back to the main window
        self.double_click('message_manager_group_icon.png', 1)
        self.connect(self.handle)
        self.wait(0.5)
        # Send messages
        self._send_messages(target, messages, file_paths)
        # Close message manager
        self.close_handle(message_manager_handle)
        # Throw if there is any error popup windows
        if not self._check_error_popup():
            raise Exception("Failed to send messages")

    def add_contacts(self, data):
        logging.debug('Add Contacts, handle: %d', self.handle)
        raise NotImplementedError

    def _check_messages(self, data):
        if not data or not data.get('target') or not data.get('messages'):
            raise Exception("Invalid data")
        target = data['target']
        messages = data['messages']
        for m in messages:
            if 'type' not in m or 'content' not in m:
                raise Exception("Invalid messages")
        dir_path = "{}\\{}\\{}".format(self.temps_path, self.app_id(), datetime.now().strftime('%Y%m%d'))
        file_types = [TaskMessageType.IMAGE, TaskMessageType.VIDEO, TaskMessageType.FILE]
        file_urls = [message['content'] for message in messages if message['type'] in file_types]
        file_paths = self.download(file_urls, dir_path)
        return target, messages, file_paths

    def _send_messages(self, target, messages, file_paths):
        self.click('main_message_input.png')
        self.key("^a{BACKSPACE}", 0.5)
        file_count = 0
        for message in messages:
            type = message.get('type')
            content = message.get('content')
            if file_count == 20:
                self.key("{ENTER}", 1)
                file_count = 0
            if type == TaskMessageType.TEXT:
                if len(content) <= 4000:
                    self.copy(content)
                    self.key("^v", 0.5)
                else:
                    texts = self._slice_text(message['text'], 4000)
                    for v in texts:
                        self.copy(v)
                        self.key("^v", 0.5)
                        self.key("{ENTER}", 1)
            elif type == TaskMessageType.IMAGE:
                self.click('main_message_image_btn.png', 1)
                self.copy(file_paths[content])
                self.key("^v", 0.5)
                self.key("{ENTER}", 0.5)
                file_count = file_count + 1
            elif type == TaskMessageType.VIDEO or type == TaskMessageType.FILE:
                self.click('main_message_file_btn.png', 1)
                self.copy(file_paths[content])
                self.key("^v", 0.5)
                self.key("{ENTER}", 0.5)
                file_count = file_count + 1
            elif type == TaskMessageType.MENTION:
                self.copy('@' + content)
                self.key("^v", 0.5)
                self.key("{ENTER}", 0.5)
        self.key("{ENTER}", 1)

    def _check_error_popup(self):
        message_box_elements = find_elements(class_name='WeWorkMessageBoxFrame')
        if message_box_elements:
            for element in message_box_elements:
                self.close_handle(element.handle)
            return False
        return True

    def _slice_text(self, obj, sec):
        return [obj[i:i + sec] for i in range(0, len(obj), sec)]
