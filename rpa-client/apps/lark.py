from apps.app import UiaApp, AppConfig


class Lark(UiaApp):
    def __init__(self, handle: int, process: int = None, status: int = None, user=None):
        super().__init__(handle, process, status, user)

    @staticmethod
    def config():
        return AppConfig(
            app_name="lark",
            login_class_name="",
            main_class_name="",
            process_name="",
            mutex_names=[""],
            registry_path="",
            registry_key="")

    def find_userinfo(self):
        return
