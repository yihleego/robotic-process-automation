package io.leego.rpa.util;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Leego Yih
 */
public class Message<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -8800068684057892298L;
    private Integer code;
    private T data;

    public Message() {
    }

    public Message(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}