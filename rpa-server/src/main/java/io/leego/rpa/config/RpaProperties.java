package io.leego.rpa.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.time.Duration;

/**
 * @author Leego Yih
 */
@Data
@ConfigurationProperties("rpa")
public class RpaProperties {
    @NestedConfigurationProperty
    private Websocket websocket = new Websocket();
    @NestedConfigurationProperty
    private Converter converter = new Converter();
    @NestedConfigurationProperty
    private Client client = new Client();

    @Data
    public static class Websocket {
        private Integer port = 18888;
        private String path = "/rpa";
        private Duration idleTimeout = Duration.ofMinutes(5);
    }

    @Data
    public static class Converter {
        private String dateTimePattern = "yyyy-MM-dd HH:mm:ss";
        private String datePattern = "yyyy-MM-dd";
        private String timePattern = "HH:mm:ss";
    }

    @Data
    public static class Client {
        private String cacheKey = "rpa:client:";
        private Duration cacheTimeout = Duration.ofMinutes(5);
    }
}
