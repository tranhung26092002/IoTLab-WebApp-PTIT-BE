package com.ptit.service.domain.exceptions;

import com.ommanisoft.common.exceptions.BaseErrorMessage;

public enum ErrorMessage implements BaseErrorMessage {
  SUCCESS("Thành công"),
  OTP_IS_INVALID("Mã OTP không hợp lệ"),
  USER_NOT_FOUND("Người dùng không tồn tại"),
  PASSWORD_RESET_TOKEN_NOT_FOUND("Password reset token không tồn tại"),
  CURRENT_PASSWORD_SAME_NEW_PASSWORD("Mật khẩu mới không được trùng với mật khẩu cũ"),
  PHONE_NUMBER_ALREADY_EXISTS("Số điện thoại đã tồn tại"),
  REFRESH_TOKEN_NOT_FOUND("Refresh token không tồn tại"),
  INVALID_REFRESH_TOKEN("Refresh token không hợp lệ"),
  CURRENT_PASSWORD_INCORRECT("Mật khẩu hiện tại không chính xác"),
  PROVINCE_NOT_FOUND("Tỉnh không tồn tại"),
  DISTRICT_NOT_FOUND("Quận không tồn tại"),
  ADDRESS_NOT_MATCH_WARD("Địa chỉ không khớp với phường/xã"),
  WARD_NOT_FOUND("Phường/xã không tồn tại"),
  UNAUTHORIZED_USER_ACCESS("Truy cập không được ủy quyền"),
  USER_UNAUTHENTICATED("Người dùng chưa xác thực"),
  PHONE_NUMBER_EXISTED("Số điện thoại đã tồn tại");

  public String val;

  private ErrorMessage(String label) {
    val = label;
  }

  @Override
  public String val() {
    return val;
  }
}
