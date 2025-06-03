package com.ptit.service.exception;

import com.ptit.service.dto.ErrorResponseDTO;
import com.ptit.service.response.ErrorResponse;
import com.ptit.service.util.I18nUntil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.NoResultException;

@RestControllerAdvice
@Log4j2
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final I18nUntil i18nUntil;

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoResultException(NoResultException e) {
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDTO);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        ErrorResponse errorResponse = getErrorResponse(i18nUntil, e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    private static ErrorResponse getErrorResponse(I18nUntil i18nUntil, BaseException e) {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setStatus(e.getStatus());
        errorResponse.setStatusCode(e.getStatusCode());

        if (e.getArgs() != null) {
            errorResponse.setMessage(i18nUntil.getMessage(e.getMessage(), e.getArgs()));
        } else {
            errorResponse.setMessage(i18nUntil.getMessage(e.getMessage()));
        }

        return errorResponse;
    }
}
