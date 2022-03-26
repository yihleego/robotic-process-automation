package io.leego.rpa.util;

/**
 * @author Leego Yih
 */
public enum Direction {
    ASC, DESC;

    /**
     * Returns whether the direction is ascending.
     */
    public boolean isAscending() {
        return this.equals(ASC);
    }

    /**
     * Returns whether the direction is descending.
     */
    public boolean isDescending() {
        return this.equals(DESC);
    }

    /**
     * Returns the {@link Direction} enum for the given {@link String} value.
     *
     * @throws IllegalArgumentException in case the given value cannot be parsed into an enum value.
     */
    public static Direction fromString(String value) {
        if (value != null && !value.isBlank()) {
            String s = value.toUpperCase();
            if (ASC.name().equals(s)) {
                return ASC;
            } else if (DESC.name().equals(s)) {
                return DESC;
            }
        }
        throw new IllegalArgumentException("Invalid direction '%s'".formatted(value));
    }
}
