package io.leego.rpa.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.Instant;

/**
 * @author Leego Yih
 */
public class InstantConverter implements Converter<String, Instant> {
    @Override
    public Instant convert(String source) {
        int len = source.length();
        if (len == 0) {
            return null;
        }
        if (source.charAt(len - 1) == 'Z') {
            // 2007-12-03T10:15:30.00Z
            return Instant.parse(source);
        } else {
            // 1196676930000
            return Instant.ofEpochMilli(Long.parseLong(source));
        }
    }
}