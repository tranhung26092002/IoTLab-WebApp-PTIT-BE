package com.ptit.service.exception;

import com.ptit.service.constant.ErrorMessageConstants;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Common errors
    UNKNOWN_ERROR(ErrorMessageConstants.UNKNOWN_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST(ErrorMessageConstants.INVALID_REQUEST, HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(ErrorMessageConstants.UNAUTHORIZED, HttpStatus.UNAUTHORIZED),
    FORBIDDEN(ErrorMessageConstants.FORBIDDEN, HttpStatus.FORBIDDEN),
    NOT_FOUND(ErrorMessageConstants.NOT_FOUND, HttpStatus.NOT_FOUND),
    VALIDATION_ERROR(ErrorMessageConstants.VALIDATION_ERROR, HttpStatus.BAD_REQUEST),

    // User errors
    USER_NOT_FOUND(ErrorMessageConstants.USER_NOT_FOUND, HttpStatus.NOT_FOUND),
    USER_IS_NOT_STUDENT(ErrorMessageConstants.USER_NOT_STUDENT, HttpStatus.BAD_REQUEST),
    USER_INVALID_PASSWORD(ErrorMessageConstants.USER_INVALID_PASSWORD, HttpStatus.BAD_REQUEST),
    USER_EMAIL_EXISTS(ErrorMessageConstants.USER_EMAIL_EXISTS, HttpStatus.CONFLICT),
    USER_EMAIL_INVALID(ErrorMessageConstants.USER_EMAIL_INVALID, HttpStatus.BAD_REQUEST),
    USER_PHONE_EXISTS(ErrorMessageConstants.USER_PHONE_EXISTS, HttpStatus.CONFLICT),
    USER_PHONE_INVALID(ErrorMessageConstants.USER_PHONE_INVALID, HttpStatus.BAD_REQUEST),
    USER_PHONE_NOT_EXISTS(ErrorMessageConstants.USER_PHONE_NOT_EXISTS, HttpStatus.NOT_FOUND),
    USER_PASSWORD_RULE_INVALID(ErrorMessageConstants.USER_PASSWORD_RULE_INVALID, HttpStatus.BAD_REQUEST),
    USER_PASSWORD_OLD_INVALID(ErrorMessageConstants.USER_PASSWORD_OLD_INVALID, HttpStatus.BAD_REQUEST),
    USER_PASSWORD_RESET_TOKEN_NOT_FOUND(ErrorMessageConstants.USER_PASSWORD_RESET_TOKEN_NOT_FOUND, HttpStatus.NOT_FOUND),
    USER_PASSWORD_SAME_AS_OLD(ErrorMessageConstants.USER_PASSWORD_SAME_AS_OLD, HttpStatus.BAD_REQUEST),
    USER_PASSWORD_CURRENT_INCORRECT(ErrorMessageConstants.USER_PASSWORD_CURRENT_INCORRECT, HttpStatus.BAD_REQUEST),
    USER_NAME_EXISTS(ErrorMessageConstants.USER_NAME_EXISTS, HttpStatus.CONFLICT),
    USER_UNAUTHORIZED(ErrorMessageConstants.USER_UNAUTHORIZED, HttpStatus.UNAUTHORIZED),
    USER_UNAUTHENTICATED(ErrorMessageConstants.USER_UNAUTHENTICATED, HttpStatus.UNAUTHORIZED),

    // OTP errors
    OTP_EXPIRE(ErrorMessageConstants.OTP_EXPIRE, HttpStatus.BAD_REQUEST),
    OTP_NOT_SEND(ErrorMessageConstants.OTP_NOT_SEND, HttpStatus.BAD_REQUEST),
    OTP_INVALID(ErrorMessageConstants.OTP_INVALID, HttpStatus.BAD_REQUEST),
    OTP_TOO_MANY_MINUTE(ErrorMessageConstants.OTP_TOO_MANY_MINUTE, HttpStatus.TOO_MANY_REQUESTS),
    OTP_TOO_MANY_DAY(ErrorMessageConstants.OTP_TOO_MANY_DAY, HttpStatus.TOO_MANY_REQUESTS),

    // Cart errors
    CART_MAX_QUANTITY(ErrorMessageConstants.CART_MAX_QUANTITY, HttpStatus.BAD_REQUEST),
    CART_NOT_FOUND(ErrorMessageConstants.CART_NOT_FOUND, HttpStatus.NOT_FOUND),

    // Address errors
    ADDRESS_NOT_FOUND(ErrorMessageConstants.ADDRESS_NOT_FOUND, HttpStatus.NOT_FOUND),
    ADDRESS_PROVINCE_NOT_FOUND(ErrorMessageConstants.ADDRESS_PROVINCE_NOT_FOUND, HttpStatus.NOT_FOUND),
    ADDRESS_DISTRICT_NOT_FOUND(ErrorMessageConstants.ADDRESS_DISTRICT_NOT_FOUND, HttpStatus.NOT_FOUND),
    ADDRESS_WARD_NOT_FOUND(ErrorMessageConstants.ADDRESS_WARD_NOT_FOUND, HttpStatus.NOT_FOUND),
    ADDRESS_NOT_MATCH_WARD(ErrorMessageConstants.ADDRESS_NOT_MATCH_WARD, HttpStatus.BAD_REQUEST),

    // Product errors
    PRODUCT_BUY_YOUR_STORE(ErrorMessageConstants.PRODUCT_BUY_YOUR_STORE, HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND(ErrorMessageConstants.PRODUCT_NOT_FOUND, HttpStatus.NOT_FOUND),

    // Token errors
    TOKEN_REFRESH_NOT_FOUND(ErrorMessageConstants.TOKEN_REFRESH_NOT_FOUND, HttpStatus.NOT_FOUND),
    TOKEN_REFRESH_INVALID(ErrorMessageConstants.TOKEN_REFRESH_INVALID, HttpStatus.BAD_REQUEST),

    UPLOAD_FILE_ERROR(ErrorMessageConstants.UPLOAD_FILE_ERROR, HttpStatus.BAD_REQUEST),
    UPLOAD_FILE_INVALID(ErrorMessageConstants.UPLOAD_FILE_INVALID, HttpStatus.BAD_REQUEST),
    FILE_EXCEL_INVALID_TYPE(ErrorMessageConstants.FILE_EXCEL_INVALID_TYPE, HttpStatus.BAD_REQUEST),
    FILE_EXCEL_NOT_FOUND(ErrorMessageConstants.FILE_EXCEL_NOT_FOUND, HttpStatus.NOT_FOUND),
    FILE_EXEL_ERROR(ErrorMessageConstants.FILE_EXEL_ERROR, HttpStatus.BAD_REQUEST),
    ;

    private final String messageKey;
    private final HttpStatus httpStatus;

    ErrorCode(String messageKey, HttpStatus httpStatus) {
        this.messageKey = messageKey;
        this.httpStatus = httpStatus;
    }
} 