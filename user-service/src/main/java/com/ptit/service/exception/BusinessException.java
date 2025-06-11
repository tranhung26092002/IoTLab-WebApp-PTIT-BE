package com.ptit.service.exception;

public class BusinessException extends BaseException {
    public BusinessException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BusinessException(ErrorCode errorCode, Object[] args) {
        super(errorCode, args);
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public BusinessException(ErrorCode errorCode, Object[] args, Throwable cause) {
        super(errorCode, args, cause);
    }
} 