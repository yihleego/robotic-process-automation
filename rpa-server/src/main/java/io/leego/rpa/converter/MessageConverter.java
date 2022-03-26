package io.leego.rpa.converter;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * @author Leego Yih
 */
public class MessageConverter {
    private final MessageSource messageSource;

    public MessageConverter(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String convert(String message) {
        return convert(message, null, message);
    }

    public String convert(String message, Object[] args) {
        return convert(message, args, message);
    }

    public String convert(String message, Object[] args, String defaultMessage) {
        if (message != null && message.length() > 0) {
            return messageSource.getMessage(message, args, defaultMessage, LocaleContextHolder.getLocale());
        }
        return defaultMessage;
    }

}
