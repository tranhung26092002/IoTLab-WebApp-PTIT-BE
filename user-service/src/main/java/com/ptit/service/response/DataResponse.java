package com.ptit.service.response;

import com.ptit.service.util.I18nUntil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
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
        this.status = status;
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public static <T> DataResponse<T> success(T data) {
        return new DataResponse<>(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                "message.success",
                data
        );
    }

    public static <T> DataResponse<T> success(String messageKey, T data) {
        return new DataResponse<>(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                messageKey,
                data
        );
    }

    public static <T> DataResponse<T> success(String messageKey, Object[] args, T data) {
        return new DataResponse<>(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                messageKey,
                data
        );
    }

    public static <T> DataResponse<T> success(String messageKey) {
        return new DataResponse<>(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                messageKey,
                null
        );
    }

    public static <T> DataResponse<T> created(T data) {
        return new DataResponse<>(
                HttpStatus.CREATED.value(),
                HttpStatus.CREATED.name(),
                "message.success",
                data
        );
    }

    public static <T> DataResponse<T> created(String messageKey, T data) {
        return new DataResponse<>(
                HttpStatus.CREATED.value(),
                HttpStatus.CREATED.name(),
                messageKey,
                data
        );
    }

    public static <T> DataResponse<T> noContent() {
        return new DataResponse<>(
                HttpStatus.NO_CONTENT.value(),
                HttpStatus.NO_CONTENT.name(),
                "message.success",
                null
        );
    }

    public static <T> DataResponse<T> error(HttpStatus status, String messageKey) {
        return new DataResponse<>(
                status.value(),
                status.name(),
                messageKey,
                null
        );
    }

    public static <T> DataResponse<T> error(HttpStatus status, String messageKey, Object[] args) {
        return new DataResponse<>(
                status.value(),
                status.name(),
                messageKey,
                null
        );
    }

    public static <T> DataResponse<T> badRequest(String messageKey) {
        return error(HttpStatus.BAD_REQUEST, messageKey);
    }

    public static <T> DataResponse<T> badRequest(String messageKey, Object[] args) {
        return error(HttpStatus.BAD_REQUEST, messageKey, args);
    }

    public static <T> DataResponse<T> unauthorized(String messageKey) {
        return error(HttpStatus.UNAUTHORIZED, messageKey);
    }

    public static <T> DataResponse<T> forbidden(String messageKey) {
        return error(HttpStatus.FORBIDDEN, messageKey);
    }

    public static <T> DataResponse<T> notFound(String messageKey) {
        return error(HttpStatus.NOT_FOUND, messageKey);
    }

    public static <T> DataResponse<T> conflict(String messageKey) {
        return error(HttpStatus.CONFLICT, messageKey);
    }

    public static <T> DataResponse<T> internalServerError(String messageKey) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, messageKey);
    }

    // Pagination support methods
    public static <T> DataResponse<PaginationData<T>> successWithPagination(PaginationData<T> paginationData) {
        return new DataResponse<>(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                "message.success",
                paginationData
        );
    }

    public static <T> DataResponse<PaginationData<T>> successWithPagination(String messageKey, PaginationData<T> paginationData) {
        return new DataResponse<>(
                HttpStatus.OK.value(),
                HttpStatus.OK.name(),
                messageKey,
                paginationData
        );
    }
}
