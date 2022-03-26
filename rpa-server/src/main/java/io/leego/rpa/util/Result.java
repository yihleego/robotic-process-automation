package io.leego.rpa.util;

import java.io.Serial;
import java.io.Serializable;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Leego Yih
 */
public class Result<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -6923820166518806231L;
    private T data;
    private Boolean success;
    private String message;
    private Integer code;

    public Result() {
    }

    public Result(Boolean success) {
        this.success = success;
    }

    public Result(T data, Boolean success) {
        this.data = data;
        this.success = success;
    }

    public Result(T data, Boolean success, String message, Integer code) {
        this.data = data;
        this.success = success;
        this.message = message;
        this.code = code;
    }

    public static <T> Result<T> buildSuccess(Integer code, String message, T data) {
        return new Result<>(data, true, message, code);
    }

    public static <T> Result<T> buildSuccess(Integer code, T data) {
        return new Result<>(data, true, null, code);
    }

    public static <T> Result<T> buildSuccess(String message, T data) {
        return new Result<>(data, true, message, null);
    }

    public static <T> Result<T> buildSuccess(String message) {
        return new Result<>(null, true, message, null);
    }

    public static <T> Result<T> buildSuccess(T data) {
        return new Result<>(data, true, null, null);
    }

    public static <T> Result<T> buildSuccess() {
        return new Result<>(null, true, null, null);
    }

    public static <T> Result<T> buildFailure(Integer code, String message, T data) {
        return new Result<>(data, false, message, code);
    }

    public static <T> Result<T> buildFailure(Integer code, String message) {
        return new Result<>(null, false, message, code);
    }

    public static <T> Result<T> buildFailure(Integer code) {
        return new Result<>(null, false, null, code);
    }

    public static <T> Result<T> buildFailure(String message, T data) {
        return new Result<>(data, false, message, null);
    }

    public static <T> Result<T> buildFailure(String message) {
        return new Result<>(null, false, message, null);
    }

    public static <T> Result<T> buildFailure() {
        return new Result<>(false);
    }

    public static <T> Result<T> buildFailure(Error error) {
        return error != null
                ? new Result<T>(null, false, error.getMessage(), error.getCode())
                : new Result<T>(false);
    }

    public static <T> Result<T> buildFailure(Throwable cause) {
        return cause != null
                ? new Result<T>(null, false, cause.getMessage(), null)
                : new Result<T>(false);
    }

    public static boolean isSuccessful(Result<?> result) {
        return result != null && result.getSuccess() != null && result.getSuccess();
    }

    public static boolean isUnsuccessful(Result<?> result) {
        return !isSuccessful(result);
    }

    public static boolean isSuccessfulWithData(Result<?> result) {
        return isSuccessful(result) && result.getData() != null;
    }

    public static <T, E extends Throwable> T getDataOrThrow(Result<T> result, BiFunction<Integer, String, E> s) throws E {
        if (result.getSuccess()) {
            return result.getData();
        }
        throw s.apply(result.getCode(), result.getMessage());
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public <U> Result<U> map(Function<T, U> converter) {
        return new Result<>(converter.apply(data), success, message, code);
    }

    public <U> Result<U> mapIfPresent(Function<T, U> converter) {
        return new Result<>(data != null ? converter.apply(data) : null, success, message, code);
    }

    public <U> Result<U> toFailure() {
        return new Result<>(null, false, message, code);
    }

    @Override
    public String toString() {
        return "Result{data=" + (data != null ? data.toString() : "null") +
                ", success=" + success +
                ", message=\"" + message + '\"' +
                ", code=" + code +
                '}';
    }
}