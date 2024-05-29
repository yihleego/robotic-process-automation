package io.leego.rpa.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.leego.rpa.converter.LocalDateConverter;
import io.leego.rpa.converter.LocalDateTimeConverter;
import io.leego.rpa.converter.LocalTimeConverter;
import io.leego.rpa.converter.MessageConverter;
import io.leego.rpa.converter.SortConverter;
import io.leego.rpa.server.RpaServer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Leego Yih
 */
@Configuration
@EnableJpaAuditing
@EnableAspectJAutoProxy
@EnableConfigurationProperties(RpaProperties.class)
public class RpaConfiguration {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory, ObjectMapper objectMapper) {
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(objectMapper.copy().activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY));
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setHashValueSerializer(valueSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalDateTimeConverter localDateTimeConverter(RpaProperties rpaProperties) {
        return new LocalDateTimeConverter(rpaProperties.getConverter().getDateTimePattern());
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalDateConverter localDateConverter(RpaProperties rpaProperties) {
        return new LocalDateConverter(rpaProperties.getConverter().getDatePattern());
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalTimeConverter localTimeConverter(RpaProperties rpaProperties) {
        return new LocalTimeConverter(rpaProperties.getConverter().getTimePattern());
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageConverter messageConverter(MessageSource messageSource) {
        return new MessageConverter(messageSource);
    }

    @Bean
    @ConditionalOnMissingBean
    public SortConverter sortConverter() {
        return new SortConverter();
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper(RpaProperties rpaProperties) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(rpaProperties.getConverter().getDateTimePattern());
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(rpaProperties.getConverter().getDatePattern());
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(rpaProperties.getConverter().getTimePattern());
        return new ObjectMapper()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
                .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
                .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModules(
                        new ParameterNamesModule(),
                        new Jdk8Module(),
                        new JavaTimeModule()
                                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter))
                                .addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter))
                                .addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter))
                                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter))
                                .addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter))
                                .addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormatter)));
    }

    @Configuration
    public static class WebMvcConfiguration implements WebMvcConfigurer {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOriginPatterns("*")
                    .allowCredentials(true)
                    .allowedMethods("*");
        }
    }

    @Configuration
    @ConditionalOnBean(RpaServer.class)
    public static class RpaServerConfiguration implements InitializingBean, DisposableBean {
        private final RpaServer rpaServer;

        public RpaServerConfiguration(RpaServer rpaServer) {
            this.rpaServer = rpaServer;
        }

        @Override
        public void afterPropertiesSet() {
            rpaServer.startup();
        }

        @Override
        public void destroy() {
            rpaServer.shutdown();
        }
    }
}