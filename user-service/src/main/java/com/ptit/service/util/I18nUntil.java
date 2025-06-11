package com.ptit.service.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Component
public class I18nUntil {
    private final MessageSource messageSource;
    private final ConcurrentHashMap<String, String> messageCache = new ConcurrentHashMap<>();

    public static final String FIELD_PREFIX = "field.";
    public static final String PARAMETER_PREFIX = "parameter.";
    public static final String DEFAULT_LOCALE = "en";

    I18nUntil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String messageKey) {
        try {
            return getMessageFromCache(messageKey, null);
        } catch (NoSuchMessageException e) {
            log.warn("Message key not found: {}", messageKey);
            return messageKey;
        }
    }

    public String getMessage(String messageKey, Object... args) {
        try {
            if (args == null) {
                return getMessageFromCache(messageKey, null);
            }
            Object[] resolvedArgs = Arrays.stream(args)
                    .map(this::resolveParam)
                    .toArray();
            return messageSource.getMessage(messageKey, resolvedArgs, getLocale());
        } catch (NoSuchMessageException e) {
            log.warn("Message key not found: {} with args: {}", messageKey, Arrays.toString(args));
            return messageKey;
        }
    }

    public String getMessage(MessageSourceResolvable resolvable) {
        try {
            return messageSource.getMessage(resolvable, getLocale());
        } catch (NoSuchMessageException e) {
            log.warn("Message not found for resolvable: {}", resolvable);
            return resolvable.getDefaultMessage();
        }
    }

    public String getValidationMessage(FieldError fieldError) {
        try {
            String fieldName = fieldError.getField();
            String fieldLabel = getMessage(FIELD_PREFIX + fieldName, fieldName);

            String messageCode = fieldError.getDefaultMessage();
            Object[] args = fieldError.getArguments();

            if (args != null && args.length > 0) {
                Object[] resolveArgs = new Object[args.length];
                resolveArgs[0] = fieldLabel;
                System.arraycopy(args, 0, resolveArgs, 1, args.length - 1);
                return getMessage(messageCode, resolveArgs);
            }

            return getMessage(messageCode);
        } catch (Exception e) {
            log.error("Error resolving validation message for field: {}", fieldError.getField(), e);
            return fieldError.getDefaultMessage();
        }
    }

    public String resolveParam(Object param) {
        if (param == null) {
            return "";
        }
        if (param instanceof String str && str.startsWith(FIELD_PREFIX)) {
            try {
                return messageSource.getMessage(str, null, getLocale());
            } catch (NoSuchMessageException e) {
                log.warn("Parameter message not found: {}", str);
                return str;
            }
        }
        return String.valueOf(param);
    }

    private String getMessageFromCache(String messageKey, Object[] args) {
        String cacheKey = messageKey + (args != null ? Arrays.toString(args) : "");
        return messageCache.computeIfAbsent(cacheKey, k -> 
            messageSource.getMessage(messageKey, args, messageKey, getLocale())
        );
    }

    private Locale getLocale() {
        Locale locale = LocaleContextHolder.getLocale();
        return locale != null ? locale : new Locale(DEFAULT_LOCALE);
    }
}
