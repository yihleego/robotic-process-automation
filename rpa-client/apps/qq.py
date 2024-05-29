import logging

from apps.app import UiaApp, AppConfig


class QQ(UiaApp):
    def __init__(self, handle: int, process: int = None, status: int = None, user=None):
        super().__init__(handle, process, status, user)
        self.version = "9.5.6"

    @staticmethod
    def config():
        return AppConfig(
            app_name="qq",
            login_class_name="TXGuiFoundation",
            main_class_name="",
            process_name="QQ.exe",
            mutex_names=[],
            registry_path="",
            registry_key="")

    def find_userinfo(self):
        return

    def login(self, data=None):
        logging.debug('Login, handle: %d', self.handle)
        wechat_app = self.connect(self.handle)
        wechat_window = wechat_app.window(class_name="TXGuiFoundation")
        try:
            username_combobox = wechat_window.child_window(control_type='ComboBox', title='QQ号码')
            username_edit = username_combobox.child_window(control_type='Edit')
            username_edit.click_input()
            username_edit.type_keys('^a')
            username_edit.type_keys(data['username'])
            password_pane = wechat_window.child_window(control_type='Pane', title='密码')
            password_edit = password_pane.child_window(control_type='Pane', found_index=1)
            password_edit.click_input()
            password_edit.type_keys('^a')
            password_edit.type_keys(data['username'])
            login_button = wechat_window.child_window(control_type='Button', title='登录')
            login_button.click_input()
        except:
            logging.debug("Switch button cannot be found, whatever")
