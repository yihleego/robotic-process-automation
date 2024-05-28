package io.leego.rpa.controller.advice;

import io.leego.rpa.converter.MessageConverter;
import io.leego.rpa.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Leego Yih
 */
@RestControllerAdvice
public class WebControllerAdvice {
    private static final Logger logger = LoggerFactory.getLogger(WebControllerAdvice.class);
    private final MessageConverter messageConverter;

    public WebControllerAdvice(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.error("", e);
        return Result.buildFailure(buildConstraintViolation(e.getBindingResult(), e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public Result<Void> handleBindException(BindException e) {
        logger.error("", e);
        return Result.buildFailure(buildConstraintViolation(e.getBindingResult(), e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public Result<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        logger.error("", e);
        return Result.buildFailure(messageConverter.convert(e.getMessage()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public Result<Void> handleException(Exception e) {
        logger.error("", e);
        return Result.buildFailure(0, messageConverter.convert(e.getMessage()));
    }

    private String buildConstraintViolation(BindingResult result, String defaultMessage) {
        if (result == null || result.getErrorCount() <= 0) {
            return defaultMessage;
        }
        String message = result.getAllErrors().get(0).getDefaultMessage();
        Object[] args = result.getFieldErrorCount() > 0 ? result.getFieldErrors().stream().map(FieldError::getRejectedValue).toArray() : null;
        return messageConverter.convert(message, args, message);
    }
}
