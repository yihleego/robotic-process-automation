package io.leego.rpa.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Leego Yih
 */
@Getter
@AllArgsConstructor
public enum AppStatus {
    DISABLED(0, "app.status.disabled"),
    ENABLED(1, "app.status.enabled");

    private final int code;
    private final String name;
}
