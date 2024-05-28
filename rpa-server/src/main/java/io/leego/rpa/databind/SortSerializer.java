package io.leego.rpa.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.leego.rpa.util.Sort;

import java.io.IOException;

/**
 * @author Leego Yih
 */
public class SortSerializer extends JsonSerializer<Sort> {
    @Override
    public void serialize(Sort value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (value != null) {
            jsonGenerator.writeString(Sort.format(value));
        }
    }
}
