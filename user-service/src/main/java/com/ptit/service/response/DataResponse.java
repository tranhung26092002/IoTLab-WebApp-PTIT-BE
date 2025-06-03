package com.ptit.service.response;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class DataResponse<T> {
    private LocalDateTime timestamp;
    private int status;
    private String statusCode;
    private String message;
    private T data;

    public DataResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public DataResponse(int status, String statusCode, String message, T data) {
        this();
        this.message = message;
        this.status = status;
        this.statusCode = statusCode;
        this.data = data;
    }

    public static <T> DataResponse<T> success(T data) {
        return new DataResponse<>(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                "message",
                data
        );
    }

    public static <T> DataResponse<T> success(String message, T data) {
        return new DataResponse<>(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                message,
                data
        );
    }

    public static <T> DataResponse<T> success(String message) {
        return new DataResponse<>(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                message,
                null
        );
    }

    public static <T> DataResponse<T> created(T data) {
        return new DataResponse<>(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                "Created successfully",
                data
        );
    }

    public static <T> DataResponse<T> error(HttpStatus status, String message) {
        return new DataResponse<>(
                status.value(),
                status.name(),
                message,
                null
        );
    }
}
