package com.ptit.service.exception;

import org.springframework.validation.FieldError;
import java.util.List;

public class ValidationException extends BaseException {
    private final List<FieldError> fieldErrors;

    public ValidationException(ErrorCode errorCode, List<FieldError> fieldErrors) {
        super(errorCode);
        this.fieldErrors = fieldErrors;
    }

    public ValidationException(ErrorCode errorCode, List<FieldError> fieldErrors, Object[] args) {
        super(errorCode, args);
        this.fieldErrors = fieldErrors;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }
} 