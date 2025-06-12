package com.ptit.service.exception;

import com.ptit.service.response.ErrorResponse;
import com.ptit.service.util.I18nUntil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

import javax.persistence.NoResultException;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Log4j2
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final I18nUntil i18nUntil;

    private static ErrorResponse getErrorResponse(I18nUntil i18nUntil, BaseException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(e.getErrorCode().getHttpStatus().value());
        errorResponse.setStatusCode(e.getErrorCode().name());

        if (e.getArgs() != null) {
            errorResponse.setMessage(i18nUntil.getMessage(e.getErrorCode().getMessageKey(), e.getArgs()));
        } else {
            errorResponse.setMessage(i18nUntil.getMessage(e.getErrorCode().getMessageKey()));
        }

        return errorResponse;
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<ErrorResponse> handleNoResultException(NoResultException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(ErrorCode.NOT_FOUND.getHttpStatus().value());
        errorResponse.setStatusCode(ErrorCode.NOT_FOUND.name());
        errorResponse.setMessage(i18nUntil.getMessage(ErrorCode.NOT_FOUND.getMessageKey()));
        return ResponseEntity.status(ErrorCode.NOT_FOUND.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        ErrorResponse errorResponse = getErrorResponse(i18nUntil, e);
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e) {
        List<String> errors = e.getFieldErrors().stream()
                .map(fieldError -> i18nUntil.getValidationMessage(fieldError))
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(e.getErrorCode().getHttpStatus().value());
        errorResponse.setStatusCode(e.getErrorCode().name());
        errorResponse.setMessage(String.join(", ", errors));

        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> i18nUntil.getValidationMessage(fieldError))
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(ErrorCode.VALIDATION_ERROR.getHttpStatus().value());
        errorResponse.setStatusCode(ErrorCode.VALIDATION_ERROR.name());
        errorResponse.setMessage(String.join(", ", errors));

        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> i18nUntil.getValidationMessage(fieldError))
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(ErrorCode.VALIDATION_ERROR.getHttpStatus().value());
        errorResponse.setStatusCode(ErrorCode.VALIDATION_ERROR.name());
        errorResponse.setMessage(String.join(", ", errors));

        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(ErrorCode.VALIDATION_ERROR.getHttpStatus().value());
        errorResponse.setStatusCode(ErrorCode.VALIDATION_ERROR.name());
        errorResponse.setMessage(String.join(", ", errors));

        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(ErrorCode.INVALID_REQUEST.getHttpStatus().value());
        errorResponse.setStatusCode(ErrorCode.INVALID_REQUEST.name());
        errorResponse.setMessage(i18nUntil.getMessage(ErrorCode.INVALID_REQUEST.getMessageKey()));
        return ResponseEntity.status(ErrorCode.INVALID_REQUEST.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(ErrorCode.INVALID_REQUEST.getHttpStatus().value());
        errorResponse.setStatusCode(ErrorCode.INVALID_REQUEST.name());
        errorResponse.setMessage(i18nUntil.getMessage(ErrorCode.INVALID_REQUEST.getMessageKey()));
        return ResponseEntity.status(ErrorCode.INVALID_REQUEST.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(ErrorCode.INVALID_REQUEST.getHttpStatus().value());
        errorResponse.setStatusCode(ErrorCode.INVALID_REQUEST.name());
        errorResponse.setMessage(i18nUntil.getMessage(ErrorCode.INVALID_REQUEST.getMessageKey()));
        return ResponseEntity.status(ErrorCode.INVALID_REQUEST.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(ErrorCode.INVALID_REQUEST.getHttpStatus().value());
        errorResponse.setStatusCode(ErrorCode.INVALID_REQUEST.name());
        errorResponse.setMessage(i18nUntil.getMessage(ErrorCode.INVALID_REQUEST.getMessageKey()));
        return ResponseEntity.status(ErrorCode.INVALID_REQUEST.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(ErrorCode.INVALID_REQUEST.getHttpStatus().value());
        errorResponse.setStatusCode(ErrorCode.INVALID_REQUEST.name());
        errorResponse.setMessage(i18nUntil.getMessage(ErrorCode.INVALID_REQUEST.getMessageKey()));
        return ResponseEntity.status(ErrorCode.INVALID_REQUEST.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(ErrorCode.UNAUTHORIZED.getHttpStatus().value());
        errorResponse.setStatusCode(ErrorCode.UNAUTHORIZED.name());
        errorResponse.setMessage(i18nUntil.getMessage(ErrorCode.UNAUTHORIZED.getMessageKey()));
        return ResponseEntity.status(ErrorCode.UNAUTHORIZED.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(ErrorCode.UNAUTHORIZED.getHttpStatus().value());
        errorResponse.setStatusCode(ErrorCode.UNAUTHORIZED.name());
        errorResponse.setMessage(i18nUntil.getMessage(ErrorCode.UNAUTHORIZED.getMessageKey()));
        return ResponseEntity.status(ErrorCode.UNAUTHORIZED.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(ErrorCode.FORBIDDEN.getHttpStatus().value());
        errorResponse.setStatusCode(ErrorCode.FORBIDDEN.name());
        errorResponse.setMessage(i18nUntil.getMessage(ErrorCode.FORBIDDEN.getMessageKey()));
        return ResponseEntity.status(ErrorCode.FORBIDDEN.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception e) {
        log.error("Unexpected error occurred: ", e);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(ErrorCode.UNKNOWN_ERROR.getHttpStatus().value());
        errorResponse.setStatusCode(ErrorCode.UNKNOWN_ERROR.name());
        errorResponse.setMessage(i18nUntil.getMessage(ErrorCode.UNKNOWN_ERROR.getMessageKey()));
        return ResponseEntity.status(ErrorCode.UNKNOWN_ERROR.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(ErrorCode.UNKNOWN_ERROR.getHttpStatus().value());
        errorResponse.setStatusCode(ErrorCode.UNKNOWN_ERROR.name());
        errorResponse.setMessage(i18nUntil.getMessage(ErrorCode.UNKNOWN_ERROR.getMessageKey()));
        return ResponseEntity.status(ErrorCode.UNKNOWN_ERROR.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse errorResponse = getErrorResponse(i18nUntil, ex);
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(errorResponse);
    }
}
