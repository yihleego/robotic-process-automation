package io.leego.rpa;

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
        Environment env = ctx.getBean(Environment.class);
        Integer port = env.getProperty("local.server.port", Integer.class, 8080);
        logger.info("Console URL: {}", String.format("http://localhost:%s/index.html", port));
    }

}
