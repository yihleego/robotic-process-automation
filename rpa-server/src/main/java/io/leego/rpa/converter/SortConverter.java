package io.leego.rpa.converter;

import io.leego.rpa.util.Sort;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Leego Yih
 */
public class SortConverter implements Converter<String, Sort> {

    @Override
    public Sort convert(String source) {
        return Sort.parse(source);
    }

}
