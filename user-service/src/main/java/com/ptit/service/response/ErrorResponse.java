package com.ptit.service.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String message;
    private int status;
    private String statusCode;

    public ErrorResponse(){
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String message, int status, String statusCode) {
        this.message = message;
        this.status = status;
        this.statusCode = statusCode;
    }
}
