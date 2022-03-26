from apps.app import UiaApp, AppConfig


class DingTalk(UiaApp):
    def __init__(self, handle: int, process: int = None, status: int = None, user=None):
        super().__init__(handle, process, status, user)

    @staticmethod
    def config():
        return AppConfig(
            app_name="dingtalk",
            login_class_name="StandardFrame_Loging",
            main_class_name="StandardFrame_DingTalk",
            process_name="DingTalk.exe",
            mutex_names=[r"\{{239B7D43-86D5-4E5C-ADE6-CEC42155B475}}DingTalk", r"\{{239B7D43-86D5-4E5C-ADE6-CEC42155B475}}DingTalk_loginframe"],
            registry_path=r"SOFTWARE\WOW6432Node\Microsoft\Windows\CurrentVersion\Uninstall\钉钉",
            registry_key="UninstallString")

    def find_user(self):
        return

    def login(self, data=None):
        pass

    def logout(self, data=None):
        pass

    def send_private_messages(self, data):
        pass

    def send_group_messages(self, data):
        pass
