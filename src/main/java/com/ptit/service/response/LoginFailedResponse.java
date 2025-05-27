package com.ptit.service.response;

import lombok.Data;

@Data
public class LoginFailedResponse {
    private String code;
    private String message;
}
