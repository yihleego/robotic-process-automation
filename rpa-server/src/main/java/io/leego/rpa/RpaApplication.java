package io.leego.rpa;

import io.leego.rpa.config.RpaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * @author Leego Yih
 */
@SpringBootApplication
public class RpaApplication {
    private static final Logger logger = LoggerFactory.getLogger(RpaApplication.class);

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(RpaApplication.class, args);
        printInfo(ctx);
    }

    private static void printInfo(ApplicationContext ctx) {
        Environment env = ctx.getBean(Environment.class);
        RpaProperties conf = ctx.getBean(RpaProperties.class);
        logger.info("Console URL: {}", String.format("http://localhost:%s", env.getProperty("local.server.port", Integer.class)));
        logger.info("WebSocket: {}", String.format("ws://localhost:%s%s", conf.getWebsocket().getPort(), conf.getWebsocket().getPath()));
    }

}
