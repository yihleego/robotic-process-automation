package io.leego.rpa.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Leego Yih
 */
@Getter
@AllArgsConstructor
public enum UserStatus {
    DISABLED(0, "user.status.disabled"),
    ENABLED(1, "user.status.enabled");

    private final int code;
    private final String name;
}
