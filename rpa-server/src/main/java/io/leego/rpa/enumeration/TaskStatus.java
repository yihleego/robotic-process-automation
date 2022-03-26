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
public enum TaskStatus {
    CREATED(0, "task.status.created"),
    RUNNING(1, "task.status.running"),
    DELETED(2, "task.status.deleted"),
    CANCELLED(3, "task.status.cancelled"),
    FINISHED(10, "task.status.finished"),
    FAILED(11, "task.status.failed"),
    TIMEOUT(12, "task.status.timeout"),
    OFFLINE(13, "task.status.offline"),
    TERMINATED(14, "task.status.terminated"),
    UNSUPPORTED(15, "task.status.unsupported"),
    ;

    private final int code;
    private final String name;

    private static final Map<Integer, TaskStatus> map = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(TaskStatus::getCode, Function.identity()));

    public static TaskStatus get(Integer code) {
        return map.get(code);
    }
}
