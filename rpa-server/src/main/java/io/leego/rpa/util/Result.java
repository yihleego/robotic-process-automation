package io.leego.rpa.util;

import java.io.Serial;
import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Leego Yih
 */
public class Result<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -6923820166518806231L;
    private T data;
    private Integer code;
    private String message;
    private Boolean success;

    public Result() {
    }

    public Result(Boolean success) {
        this.success = success;
    }

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(Integer code, String message, T data, Boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
    }

    public static <T> Result<T> buildSuccess(Integer code, String message, T data) {
        return new Result<>(code, message, data, true);
    }

    public static <T> Result<T> buildSuccess(Integer code, T data) {
        return new Result<>(code, null, data, true);
    }

    public static <T> Result<T> buildSuccess(String message, T data) {
        return new Result<>(null, message, data, true);
    }

    public static <T> Result<T> buildSuccess(String message) {
        return new Result<>(null, message, null, true);
    }

    public static <T> Result<T> buildSuccess(T data) {
        return new Result<>(null, null, data, true);
    }

    public static <T> Result<T> buildSuccess() {
        return new Result<>(null, null, null, true);
    }

    public static <T> Result<T> buildFailure(Integer code, String message, T data) {
        return new Result<>(code, message, data, false);
    }

    public static <T> Result<T> buildFailure(Integer code, String message) {
        return new Result<>(code, message, null, false);
    }

    public static <T> Result<T> buildFailure(Integer code) {
        return new Result<>(code, null, null, false);
    }

    public static <T> Result<T> buildFailure(String message, T data) {
        return new Result<>(null, message, data, false);
    }

    public static <T> Result<T> buildFailure(String message) {
        return new Result<>(null, message, null, false);
    }

    public static <T> Result<T> buildFailure() {
        return new Result<>(false);
    }

    public static <T> Result<T> buildFailure(Error error) {
        return error != null
                ? new Result<>(error.getCode(), error.getMessage(), null, false)
                : new Result<>(false);
    }

    public static <T> Result<T> buildFailure(Throwable cause) {
        return cause != null
                ? new Result<>(null, cause.getMessage(), null, false)
                : new Result<>(false);
    }

    public static boolean isSuccessful(Result<?> r) {
        if (r == null) return false;
        if (r.success != null) return r.success;
        if (r.code != null) return r.code == 0;
        return true;
    }

    public static boolean isUnsuccessful(Result<?> r) {
        return !isSuccessful(r);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Result<T> success() {
        return new Result<>(code, message, null, true);
    }

    public Result<T> failure() {
        return new Result<>(code, message, null, false);
    }

    public <U> Result<U> map(Function<T, U> mapper) {
        return new Result<>(code, message, mapper.apply(data), success);
    }

    public <U> Result<U> map(Function<T, U> mapper, Predicate<T> filter) {
        return new Result<>(code, message, filter.test(data) ? mapper.apply(data) : null, success);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Result{data=").append(data);
        if (code != null) sb.append(", code=").append(code);
        if (message != null) sb.append(", message='").append(message).append("'");
        if (success != null) sb.append(", success=").append(success);
        return sb.append('}').toString();
    }
}