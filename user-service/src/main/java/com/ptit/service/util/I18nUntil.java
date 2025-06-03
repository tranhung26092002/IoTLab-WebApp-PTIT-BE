package com.ptit.service.util;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import java.util.Arrays;

@Component
public class I18nUntil {
    public final MessageSource messageSource;

    public final String FIELD_PREFIX = "field.";
    public final String PARAMETER_PREFIX = "parameter.";

    I18nUntil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String messageKey) {
        return messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());
    }

    public String getMessage(String messageKey, Object... args) {
        Object[] resolvedArgs = Arrays.stream(args)
                .map(this::resolveParam)
                .toArray();
        return messageSource.getMessage(messageKey, resolvedArgs, messageKey, LocaleContextHolder.getLocale());
    }

    public String getMessage(MessageSourceResolvable resolvable) {
        return messageSource.getMessage(resolvable, LocaleContextHolder.getLocale());
    }

    public String getValidationMessage(FieldError fieldError) {
        String fieldName = fieldError.getField();
        String fieldLabel = getMessage(FIELD_PREFIX + fieldName, fieldName);

        String messageCode = fieldError.getDefaultMessage();
        Object[] args = fieldError.getArguments();

        if (args != null && args.length > 0) {
            Object[] resolveArgs = new Object[args.length];

            resolveArgs[0] = fieldName;

            System.arraycopy(args, 0, resolveArgs, 1, args.length - 1);
            return getMessage(messageCode, resolveArgs);
        }

        return getMessage(messageCode);
    }

    public String resolveParam(Object param) {
        if (param instanceof String str && str.startsWith(FIELD_PREFIX)) {
            return messageSource.getMessage(str, null, LocaleContextHolder.getLocale());
        }
        return String.valueOf(param);
    }
}
