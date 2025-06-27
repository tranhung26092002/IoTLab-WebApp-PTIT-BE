package com.ptit.service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Common errors
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "error.unknown"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "error.invalid.request"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "error.unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "error.forbidden"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "error.not.found"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "error.validation"),

    // User errors
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "error.user.not.found"),
    USER_IS_NOT_STUDENT(HttpStatus.BAD_REQUEST, "error.user.not.student"),
    USER_INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "error.user.invalid.password"),
    USER_EMAIL_EXISTS(HttpStatus.CONFLICT, "error.user.email.exists"),
    USER_EMAIL_INVALID(HttpStatus.BAD_REQUEST, "error.user.email.invalid"),
    USER_PHONE_EXISTS(HttpStatus.CONFLICT, "error.user.phone.exists"),
    USER_PHONE_INVALID(HttpStatus.BAD_REQUEST, "error.user.phone.invalid"),
    USER_PHONE_NOT_EXISTS(HttpStatus.NOT_FOUND, "error.user.phone.not.exists"),
    USER_PASSWORD_RULE_INVALID(HttpStatus.BAD_REQUEST, "error.user.password.rule.invalid"),
    USER_PASSWORD_OLD_INVALID(HttpStatus.BAD_REQUEST, "error.user.password.old.invalid"),
    USER_PASSWORD_RESET_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "error.user.password.reset.token.not.found"),
    USER_PASSWORD_SAME_AS_OLD(HttpStatus.BAD_REQUEST, "error.user.password.same.as.old"),
    USER_PASSWORD_CURRENT_INCORRECT(HttpStatus.BAD_REQUEST, "error.user.password.current.incorrect"),
    USER_NAME_EXISTS(HttpStatus.CONFLICT, "error.user.name.exists"),
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "error.user.unauthorized"),
    USER_UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "error.user.unauthenticated"),

    // OTP errors
    OTP_EXPIRE(HttpStatus.BAD_REQUEST, "error.otp.expire"),
    OTP_NOT_SEND(HttpStatus.BAD_REQUEST, "error.otp.not.send"),
    OTP_INVALID(HttpStatus.BAD_REQUEST, "error.otp.invalid"),
    OTP_TOO_MANY_MINUTE(HttpStatus.TOO_MANY_REQUESTS, "error.otp.too.many.minute"),
    OTP_TOO_MANY_DAY(HttpStatus.TOO_MANY_REQUESTS, "error.otp.too.many.day"),

    // Cart errors
    CART_MAX_QUANTITY(HttpStatus.BAD_REQUEST, "error.cart.max.quantity"),
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "error.cart.not.found"),

    // Address errors
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "error.address.not.found"),
    ADDRESS_PROVINCE_NOT_FOUND(HttpStatus.NOT_FOUND, "error.address.province.not.found"),
    ADDRESS_DISTRICT_NOT_FOUND(HttpStatus.NOT_FOUND, "error.address.district.not.found"),
    ADDRESS_WARD_NOT_FOUND(HttpStatus.NOT_FOUND, "error.address.ward.not.found"),
    ADDRESS_NOT_MATCH_WARD(HttpStatus.BAD_REQUEST, "error.address.not.match.ward"),

    // Product errors
    PRODUCT_BUY_YOUR_STORE(HttpStatus.BAD_REQUEST, "error.product.buy.your.store"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "error.product.not.found"),

    // Token errors
    TOKEN_REFRESH_NOT_FOUND(HttpStatus.NOT_FOUND, "error.token.refresh.not.found"),
    TOKEN_REFRESH_INVALID(HttpStatus.BAD_REQUEST, "error.token.refresh.invalid"),

    UPLOAD_FILE_ERROR(HttpStatus.BAD_REQUEST, "error.upload.file.error"),
    UPLOAD_FILE_INVALID(HttpStatus.BAD_REQUEST, "error.upload.file.invalid"),
    FILE_EXCEL_INVALID_TYPE(HttpStatus.BAD_REQUEST, "error.file.excel.invalid.type"),
    FILE_EXCEL_NOT_FOUND(HttpStatus.NOT_FOUND, "error.file.excel.not.found"),
    FILE_EXEL_ERROR(HttpStatus.BAD_REQUEST, "error.file.exel.error"),
    ;

    private final HttpStatus httpStatus;
    private final String messageKey;

    ErrorCode(HttpStatus httpStatus, String messageKey) {
        this.httpStatus = httpStatus;
        this.messageKey = messageKey;
    }
} 