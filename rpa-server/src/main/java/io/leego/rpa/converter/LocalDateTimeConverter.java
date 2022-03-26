package io.leego.rpa.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Leego Yih
 */
public class LocalDateTimeConverter implements Converter<String, LocalDateTime> {
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private final DateTimeFormatter dateTimeFormatter;

    public LocalDateTimeConverter() {
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    }

    public LocalDateTimeConverter(String pattern) {
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
    }

    public LocalDateTimeConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public LocalDateTime convert(String source) {
        if (source.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(source, dateTimeFormatter);
    }

}
