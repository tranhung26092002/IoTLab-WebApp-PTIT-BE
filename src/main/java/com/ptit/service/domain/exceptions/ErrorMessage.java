package com.ptit.service.domain.exceptions;

import com.ommanisoft.common.exceptions.BaseErrorMessage;

public enum ErrorMessage implements BaseErrorMessage {
  SUCCESS("Thành công");

  public String val;

  private ErrorMessage(String label) {
    val = label;
  }

  @Override
  public String val() {
    return val;
  }
}
