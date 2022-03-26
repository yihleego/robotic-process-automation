package io.leego.rpa.util;

/**
 * @author Leego Yih
 */
public interface Error {

    /** Returns error code. */
    Integer getCode();

    /** Returns error message. */
    String getMessage();

}