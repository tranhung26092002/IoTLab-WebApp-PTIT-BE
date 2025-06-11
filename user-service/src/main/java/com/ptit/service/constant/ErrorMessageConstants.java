package com.ptit.service.constant;

public final class ErrorMessageConstants {
    // Common errors
    public static final String UNKNOWN_ERROR = "error.unknown";
    public static final String INVALID_REQUEST = "error.invalid.request";
    public static final String UNAUTHORIZED = "error.unauthorized";
    public static final String FORBIDDEN = "error.forbidden";
    public static final String NOT_FOUND = "error.not.found";
    public static final String VALIDATION_ERROR = "error.validation";
    // User errors
    public static final String USER_NOT_FOUND = "error.user.not.found";
    public static final String USER_NOT_STUDENT = "error.user.not.student";
    public static final String USER_INVALID_PASSWORD = "error.user.invalid.password";
    public static final String USER_EMAIL_EXISTS = "error.user.email.exists";
    public static final String USER_EMAIL_INVALID = "error.user.email.invalid";
    public static final String USER_PHONE_EXISTS = "error.user.phone.exists";
    public static final String USER_PHONE_INVALID = "error.user.phone.invalid";
    public static final String USER_PHONE_NOT_EXISTS = "error.user.phone.not.exists";
    public static final String USER_PASSWORD_RULE_INVALID = "error.user.password.rule.invalid";
    public static final String USER_PASSWORD_OLD_INVALID = "error.user.password.old.invalid";
    public static final String USER_PASSWORD_RESET_TOKEN_NOT_FOUND = "error.user.password.reset.token.not.found";
    public static final String USER_PASSWORD_SAME_AS_OLD = "error.user.password.same.as.old";
    public static final String USER_PASSWORD_CURRENT_INCORRECT = "error.user.password.current.incorrect";
    public static final String USER_NAME_EXISTS = "error.user.name.exists";
    public static final String USER_UNAUTHORIZED = "error.user.unauthorized";
    public static final String USER_UNAUTHENTICATED = "error.user.unauthenticated";
    // OTP errors
    public static final String OTP_EXPIRE = "error.otp.expire";
    public static final String OTP_NOT_SEND = "error.otp.not.send";
    public static final String OTP_INVALID = "error.otp.invalid";
    public static final String OTP_TOO_MANY_MINUTE = "error.otp.too.many.minute";
    public static final String OTP_TOO_MANY_DAY = "error.otp.too.many.day";
    // Cart errors
    public static final String CART_MAX_QUANTITY = "error.cart.max.quantity";
    public static final String CART_NOT_FOUND = "error.cart.not.found";
    // Address errors
    public static final String ADDRESS_NOT_FOUND = "error.address.not.found";
    public static final String ADDRESS_PROVINCE_NOT_FOUND = "error.address.province.not.found";
    public static final String ADDRESS_DISTRICT_NOT_FOUND = "error.address.district.not.found";
    public static final String ADDRESS_WARD_NOT_FOUND = "error.address.ward.not.found";
    public static final String ADDRESS_NOT_MATCH_WARD = "error.address.not.match.ward";
    // Product errors
    public static final String PRODUCT_BUY_YOUR_STORE = "error.product.buy.your.store";
    public static final String PRODUCT_NOT_FOUND = "error.product.not.found";
    // Token errors
    public static final String TOKEN_REFRESH_NOT_FOUND = "error.token.refresh.not.found";
    public static final String TOKEN_REFRESH_INVALID = "error.token.refresh.invalid";
    // Success messages
    public static final String SUCCESS = "message.success";
    public static final String OTP_SENT = "message.otp.sent";
    // Validation messages
    public static final String FIELD_REQUIRED = "field.required";
    public static final String FIELD_INVALID = "field.invalid";
    public static final String FIELD_MIN = "field.min";
    public static final String FIELD_MAX = "field.max";
    public static final String FIELD_SIZE = "field.size";
    public static final String FIELD_EMAIL = "field.email";
    public static final String FIELD_PHONE = "field.phone";
    public static final String FIELD_PASSWORD = "field.password";
    public static final String FIELD_CONFIRM_PASSWORD = "field.confirmPassword";

    public static final String UPLOAD_FILE_ERROR = "error.upload.file.error";
    public static final String UPLOAD_FILE_INVALID = "error.upload.file.invalid";

    public static final String FILE_EXCEL_INVALID_TYPE = "error.file.excel.invalid.type";
    public static final String FILE_EXCEL_NOT_FOUND = "error.file.excel.not.found";
    public static final String FILE_EXEL_ERROR = "error.file.excel.error";
    
    private ErrorMessageConstants() {
        throw new IllegalStateException("Constant class");
    }
} 