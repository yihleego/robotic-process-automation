# 客户端部署

请确保使用的操作系统为 Windows 7 及以上和 Python 的版本为3.7.0及以上。

## 安装

```bash
git clone https://github.com/yihleego/robotic-process-automation.git
cd robotic-process-automation/rpa-client
pip install -r requirements.txt
```

## 配置

客户端配置文件位于 [rpa-client/config.yml](../rpa-client/config.yml)，开发者可以根据实际场景修改配置。

| Property            | Description | Default                 |
|:--------------------|:------------|:------------------------|
| server.host         | 服务器主机       | `localhost`             |
| server.port         | 服务器端口       | `10000`                 |
| server.path         | 服务器路径       | `/rpa`                  |
| server.ssl          | 是否启用SSL     | `False`                 |
| app.size            | 程序最大可运行数量   | `32`                    |
| app.path.`<appid>`  | 自定义程序路径     | *从注册表中获取*               |
| airtest.cvstrategy  | 图像识别算法      | `[ tpl,sift,brisk ]`    |
| airtest.timeout     | 图像识别算法      | `20`秒                   |
| airtest.timeout-tmp | 图像识别算法      | `3`秒                    |
| logging.level       | 日志级别        | `DEBUG`                 |
| logging.format      | 日志格式        |                         |
| logging.filename    | 日志文件名       | `./logs/rpa-client.log` |

## 启动

运行 [rpa-client/main.py](../rpa-client/main.py) 即可。
