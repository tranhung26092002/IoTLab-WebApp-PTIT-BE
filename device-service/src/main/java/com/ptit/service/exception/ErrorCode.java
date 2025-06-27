package com.ptit.service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Common errors
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "error.invalid_request");

    private final HttpStatus httpStatus;
    private final String messageKey;

    ErrorCode(HttpStatus httpStatus, String messageKey) {
        this.httpStatus = httpStatus;
        this.messageKey = messageKey;
    }
} 