package io.leego.rpa.enumeration;

import io.leego.rpa.constant.Messages;
import io.leego.rpa.util.Code;
import io.leego.rpa.util.Error;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Leego Yih
 */
@Getter
public enum ErrorCode implements Error, Code {
    /** Common */
    PAGE_INVALID(1, 0, Messages.PAGE_INVALID),
    SORT_INVALID(1, 1, Messages.SORT_INVALID),
    PARAM_INVALID(1, 2, Messages.PARAM_INVALID),
    /** App */
    APP_ABSENT(2, 0, Messages.APP_ABSENT),
    APP_PRESENT(2, 1, Messages.APP_PRESENT),
    APP_DISABLED(2, 2, Messages.APP_DISABLED),
    /** User */
    USER_ABSENT(3, 0, Messages.USER_ABSENT),
    USER_PRESENT(3, 1, Messages.USER_PRESENT),
    USER_DISABLED(3, 2, Messages.USER_DISABLED),
    /** Task */
    TASK_ABSENT(4, 0, Messages.TASK_ABSENT),
    TASK_PRESENT(4, 1, Messages.TASK_PRESENT),
    TASK_TYPE_INVALID(4, 2, Messages.TASK_TYPE_INVALID),
    ;

    private final Integer code;
    private final String message;
    private final int type = 0;
    private final int tag;
    private final int index;

    ErrorCode(int tag, int index, String message) {
        this.tag = tag;
        this.index = index;
        this.code = toCode();
        this.message = message;
    }

    private static final Map<Integer, ErrorCode> map = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(ErrorCode::getCode, Function.identity()));

    public static ErrorCode get(Integer code) {
        return map.get(code);
    }
}
