package io.leego.rpa.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Leego Yih
 */
@Getter
@AllArgsConstructor
public enum MessageCode {
    UNKNOWN(-1),
    HEARTBEAT(0),
    CLIENT_SYNC(10),
    TASK_SYNC(11),
    TASK_REQUEST(12),
    TASK_RESPONSE(13),
    ;

    private final int code;

    private static final Map<Integer, MessageCode> map = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(MessageCode::getCode, Function.identity()));

    public static MessageCode get(Integer code) {
        return map.get(code);
    }

    public static MessageCode getOrDefault(Integer code, MessageCode defaultValue) {
        return map.getOrDefault(code, defaultValue);
    }
}
