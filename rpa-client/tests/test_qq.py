import logging
import unittest

from apps.qq import QQ

APP = QQ


class WechatTestSuite(unittest.TestCase):

    def get_client(self) -> APP:
        elements = APP.find_elements()
        first = elements[0]
        return APP(first.handle, first.process_id, 0)

    def test_launch(self):
        APP.launch(1)

    def test_login(self):
        client = self.get_client()
        res = client.login({"username": "1", "password": "1"})
        logging.debug(res)
