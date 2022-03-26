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
public enum ClientStatus {
    OFFLINE(0, "client.status.offline"),
    ONLINE(1, "client.status.online"),
    WAITING(2, "client.status.waiting"),
    ERROR(3, "client.status.error"),
    SUSPENDED(4, "client.status.suspended"),
    ;

    private final int code;
    private final String name;

    private static final Map<Integer, ClientStatus> map = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(ClientStatus::getCode, Function.identity()));

    public static ClientStatus get(Integer code) {
        return map.get(code);
    }
}
