package io.leego.rpa.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Leego Yih
 */
public class LocalDateConverter implements Converter<String, LocalDate> {
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private final DateTimeFormatter dateTimeFormatter;

    public LocalDateConverter() {
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
    }

    public LocalDateConverter(String pattern) {
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
    }

    public LocalDateConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public LocalDate convert(String source) {
        if (source.isEmpty()) {
            return null;
        }
        return LocalDate.parse(source, dateTimeFormatter);
    }

}
