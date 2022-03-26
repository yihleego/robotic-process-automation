# 服务端部署

请确保使用的 Java 的版本为17及以上。

## 安装

```bash
git clone https://github.com/yihleego/robotic-process-automation.git
cd robotic-process-automation/rpa-server
mvn clean install
```

## 配置

### 必填配置

服务端依赖于`MySQL`和`Redis`，请在 [application.properties](../rpa-server/src/main/resources/application.properties) 文件中修改相应配置。

```properties
# datasource
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://<localhost>:<port>/rpa
spring.datasource.username=<username>
spring.datasource.password=<password>
# redis
spring.redis.host=<localhost>
spring.redis.port=<port>
spring.redis.password=<password>
spring.redis.database=<database>
```

### 可选配置

| Property                        | Description         | Default                          |
|:--------------------------------|:--------------------|:---------------------------------|
| rpa.websocket.port              | `WebSocket`服务端口     | `10000`                          |
| rpa.websocket.path              | `WebSocket`服务路径     | `/rpa`                           |
| rpa.websocket.idle-timeout      | `WebSocket`服务空闲超市时间 | `5m`                             |
| rpa.converter.date-time-pattern | 日期时间格式              | `yyyy-MM-dd HH:mm:ss`            |
| rpa.converter.date-pattern      | 日期格式                | `yyyy-MM-dd`                     |
| rpa.converter.time-pattern      | 时间格式                | `HH:mm:ss`                       |
| rpa.client.cache-key            | 客户端缓存KEY前缀          | rpa:client:`<appid>`:`<account>` |
| rpa.client.cache-timeout        | 客户端缓存超时时间           | `5m`                             |

## 准备工作

启动服务前，请先在`MySQL`实例中执行以下脚本

- [rpa_ddl.sql](../rpa-server/sql/rpa_ddl.sql)
- [rpa_dml.sql](../rpa-server/sql/rpa_dml.sql)

## 启动

运行 [RpaApplication.java](../rpa-server/src/main/java/io/leego/rpa/RpaApplication.java) 即可。
