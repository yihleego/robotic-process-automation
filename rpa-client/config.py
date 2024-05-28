import logging
import os
import sys

import yaml

from airtest.core.settings import Settings

# Load config
config = yaml.load(open(os.path.join(os.path.dirname(__file__), "config.yml"), 'r', encoding='utf-8'), Loader=yaml.SafeLoader)

# Application
SERVER_HOST = config.get("server").get("host", "localhost")
SERVER_PORT = config.get("server").get("port", 18888)
SERVER_PATH = config.get("server").get("path", "/rpa")
SERVER_SSL = config.get("server").get("ssl", False)
APP_SIZE = config.get("app").get("size")
APP_PATHS = {k: v for k, v in config.get("app").get("path").items()}

# Logging
LOGGING_LEVEL = config.get("logging").get("level", "INFO")
LOGGING_FORMAT = config.get("logging").get("format")
LOGGING_FILENAME = config.get("logging").get("filename")
LOGGING_FORMATTER = logging.Formatter(LOGGING_FORMAT)
STREAM_HANDLER = logging.StreamHandler(sys.stdout)
STREAM_HANDLER.setFormatter(LOGGING_FORMATTER)
logger = logging.getLogger()
logger.setLevel(LOGGING_LEVEL.upper())
logger.addHandler(STREAM_HANDLER)
if LOGGING_FILENAME:
    os.makedirs(os.path.dirname(LOGGING_FILENAME), exist_ok=True)
    FILE_HANDLER = logging.FileHandler(LOGGING_FILENAME, encoding="utf-8")
    FILE_HANDLER.setFormatter(LOGGING_FORMATTER)
    logger.addHandler(FILE_HANDLER)

# Airtest
AIRTEST_CVSTRATEGY = config.get("airtest").get("cvstrategy")
AIRTEST_TIMEOUT = config.get("airtest").get("timeout")
AIRTEST_TIMEOUT_TMP = config.get("airtest").get("timeout-tmp")
if AIRTEST_CVSTRATEGY:
    Settings.CVSTRATEGY = AIRTEST_CVSTRATEGY
if AIRTEST_TIMEOUT:
    Settings.FIND_TIMEOUT = AIRTEST_TIMEOUT
if AIRTEST_TIMEOUT_TMP:
    Settings.FIND_TIMEOUT_TMP = AIRTEST_TIMEOUT_TMP
