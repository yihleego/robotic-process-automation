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
public enum TaskMessageType {
    TEXT(0, "task.message.type.text"),
    IMAGE(1, "task.message.type.image"),
    VIDEO(2, "task.message.type.video"),
    FILE(3, "task.message.type.file"),
    MENTION(4, "task.message.type.mention"),
    ;

    private final int code;
    private final String name;

    private static final Map<Integer, TaskMessageType> map = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(TaskMessageType::getCode, Function.identity()));

    public static TaskMessageType get(Integer code) {
        return map.get(code);
    }
}
