import logging
import unittest

import win32gui

from apps.app import MessageType
from apps.wecom import WeCom

APP = WeCom


class WecomTestSuite(unittest.TestCase):

    def get_client(self) -> APP:
        elements = APP.find_elements()
        first = elements[0]
        return APP(first.handle, first.process_id, 0)

    def test_launch(self):
        APP.launch(1)

    def test_reset_title(self):
        client = self.get_client()
        win32gui.SetWindowText(client.handle, '')

    def test_init(self):
        client = self.get_client()
        client.init()
        logging.debug(client.user)
        assert client.user is not None

    def test_find_user(self):
        client = self.get_client()
        user = client.find_user()
        logging.debug(user)
        assert user is not None

    def test_login(self):
        client = self.get_client()
        qrcode = client.login(None)
        logging.debug(qrcode)

    def test_logout(self):
        client = self.get_client()
        client.logout(None)

    def test_send_private_messages(self):
        client = self.get_client()
        client.send_private_messages({
            "target": "friend_name",
            "messages": [
                {"type": MessageType.TEXT, "content": "Hey, dude."},
                {"type": MessageType.IMAGE, "content": "https://www.xxxx.com/image.png"},
                {"type": MessageType.VIDEO, "content": "https://www.xxxx.com/video.mp4"},
                {"type": MessageType.FILE, "content": "https://www.xxxx.com/file.zip"},
            ]
        })

    def test_send_group_messages(self):
        client = self.get_client()
        client.send_group_messages({
            "target": "group_name",
            "messages": [
                {"type": MessageType.TEXT, "content": "Hey, guys."},
                {"type": MessageType.IMAGE, "content": "https://www.xxxx.com/image.png"},
                {"type": MessageType.VIDEO, "content": "https://www.xxxx.com/video.mp4"},
                {"type": MessageType.FILE, "content": "https://www.xxxx.com/file.zip"},
                {"type": MessageType.MENTION, "content": "member_name"},
            ]
        })
