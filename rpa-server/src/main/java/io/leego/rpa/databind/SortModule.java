package io.leego.rpa.databind;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.leego.rpa.util.Sort;

import java.io.Serial;

/**
 * @author Leego Yih
 */
public class SortModule extends SimpleModule {
    @Serial
    private static final long serialVersionUID = -265873645056841230L;

    public SortModule() {
        this.addSerializer(Sort.class, new SortSerializer());
        this.addDeserializer(Sort.class, new SortDeserializer());
    }

    @Override
    public String getModuleName() {
        return "SortModule";
    }
}
