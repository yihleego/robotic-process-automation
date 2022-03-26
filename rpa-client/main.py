import logging
import signal
import sys
import time

from config import SERVER_HOST, SERVER_PORT, SERVER_PATH, SERVER_SSL, APP_SIZE, APP_PATHS
from scheduler import Scheduler

if __name__ == '__main__':
    signal.signal(signal.SIGINT, lambda *args: sys.exit())
    signal.signal(signal.SIGTERM, lambda *args: sys.exit())
    while True:
        scheduler = Scheduler(host=SERVER_HOST, port=SERVER_PORT, path=SERVER_PATH, ssl=SERVER_SSL, app_size=APP_SIZE, app_paths=APP_PATHS)
        scheduler.startup()
        logging.error('Unable to connect to the server host: %s, port: %d, path: %s, ssl: %s', SERVER_HOST, SERVER_PORT, SERVER_PATH, SERVER_SSL)
        scheduler.shutdown()
        time.sleep(5)
