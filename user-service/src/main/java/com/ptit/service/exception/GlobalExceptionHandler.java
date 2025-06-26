package com.ptit.service.exception;

import com.ptit.service.response.DataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import com.ptit.service.util.I18nUntil;

import javax.persistence.NoResultException;
import javax.validation.ConstraintViolationException;
import java.io.IOException;

@RestControllerAdvice
@Log4j2
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final I18nUntil i18nUntil;

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<DataResponse<Object>> handleNoResultException(NoResultException e) {
        String message = i18nUntil.getMessage("error.not.found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(DataResponse.error(HttpStatus.NOT_FOUND, message));
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<DataResponse<Object>> handleBaseException(BaseException e) {
        String message = i18nUntil.getMessage(e.getErrorCode().getMessageKey(), e.getArgs());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(DataResponse.error(e.getErrorCode().getHttpStatus(), message));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<DataResponse<Object>> handleValidationException(ValidationException e) {
        String message = i18nUntil.getMessage(e.getErrorCode().getMessageKey(), e.getArgs());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(DataResponse.error(e.getErrorCode().getHttpStatus(), message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DataResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String message = i18nUntil.getMessage("error.validation");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(DataResponse.error(HttpStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<DataResponse<Object>> handleBindException(BindException ex) {
        String message = i18nUntil.getMessage("error.validation");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(DataResponse.error(HttpStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<DataResponse<Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        String message = i18nUntil.getMessage("error.validation");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(DataResponse.error(HttpStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DataResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = i18nUntil.getMessage("error.invalid.request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(DataResponse.error(HttpStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<DataResponse<Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = i18nUntil.getMessage("error.invalid.request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(DataResponse.error(HttpStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<DataResponse<Object>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String message = i18nUntil.getMessage("error.invalid.request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(DataResponse.error(HttpStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<DataResponse<Object>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        String message = i18nUntil.getMessage("error.invalid.request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(DataResponse.error(HttpStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<DataResponse<Object>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        String message = i18nUntil.getMessage("error.invalid.request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(DataResponse.error(HttpStatus.BAD_REQUEST, message));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<DataResponse<Object>> handleAuthenticationException(AuthenticationException ex) {
        String message = i18nUntil.getMessage("error.unauthorized");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(DataResponse.error(HttpStatus.UNAUTHORIZED, message));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<DataResponse<Object>> handleBadCredentialsException(BadCredentialsException ex) {
        String message = i18nUntil.getMessage("error.unauthorized");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(DataResponse.error(HttpStatus.UNAUTHORIZED, message));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<DataResponse<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        String message = i18nUntil.getMessage("error.forbidden");
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(DataResponse.error(HttpStatus.FORBIDDEN, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DataResponse<Object>> handleGlobalException(Exception e) {
        String message = i18nUntil.getMessage("error.unknown");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(DataResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, message));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<DataResponse<Object>> handleIOException(IOException ex) {
        String message = i18nUntil.getMessage("error.unknown");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(DataResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, message));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<DataResponse<Object>> handleBusinessException(BusinessException ex) {
        String message = i18nUntil.getMessage("error.business.exception");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(DataResponse.error(HttpStatus.BAD_REQUEST, message));
    }
}
