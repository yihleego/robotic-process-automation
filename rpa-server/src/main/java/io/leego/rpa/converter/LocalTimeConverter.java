package io.leego.rpa.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Leego Yih
 */
public class LocalTimeConverter implements Converter<String, LocalTime> {
    private static final String TIME_PATTERN = "HH:mm:ss";
    private final DateTimeFormatter dateTimeFormatter;

    public LocalTimeConverter() {
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(TIME_PATTERN);
    }

    public LocalTimeConverter(String pattern) {
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
    }

    public LocalTimeConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public LocalTime convert(String source) {
        if (source.isEmpty()) {
            return null;
        }
        return LocalTime.parse(source, dateTimeFormatter);
    }

}
