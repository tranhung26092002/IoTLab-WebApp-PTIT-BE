package com.ptit.service.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class BaseException extends RuntimeException {
    Object[] args;

    public BaseException() {
        super();
    }

    public BaseException(String messageCode) {
        super(messageCode);
    }

    public BaseException(String messageCode, Throwable cause) {
        super(messageCode, cause);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(String messageCode, Object... arg) {
        super(messageCode);
        this.args = arg;
    }

    public int getStatus() {
        return 400;
    }

    public String getStatusCode() {
        return "BAD_REQUEST";
    }
}
