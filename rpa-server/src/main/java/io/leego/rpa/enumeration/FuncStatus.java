package io.leego.rpa.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Leego Yih
 */
@Getter
@AllArgsConstructor
public enum FuncStatus {
    DISABLED(0, "func.status.disabled"),
    ENABLED(1, "func.status.enabled");

    private final int code;
    private final String name;
}
