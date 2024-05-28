package io.leego.rpa.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.leego.rpa.util.Sort;

import java.io.IOException;

/**
 * @author Leego Yih
 */
public class SortDeserializer extends JsonDeserializer<Sort> {
    @Override
    public Sort deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getValueAsString();
        return value == null || value.isBlank() ? null : Sort.parse(value);
    }
}
