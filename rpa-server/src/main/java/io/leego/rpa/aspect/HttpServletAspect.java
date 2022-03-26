package io.leego.rpa.aspect;

import io.leego.rpa.converter.MessageConverter;
import io.leego.rpa.util.Result;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author Leego Yih
 */
@Aspect
@Component
public class HttpServletAspect {
    private final MessageConverter messageConverter;

    public HttpServletAspect(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    @AfterReturning(pointcut = "@within(org.springframework.web.bind.annotation.RestController)", returning = "result")
    public Object responseAround(Result<?> result) {
        if (StringUtils.hasText(result.getMessage())) {
            result.setMessage(messageConverter.convert(result.getMessage()));
        }
        return result;
    }
}
