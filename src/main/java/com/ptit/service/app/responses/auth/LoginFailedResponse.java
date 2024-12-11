package com.ptit.service.app.responses.auth;

import lombok.Data;

@Data
public class LoginFailedResponse {
    private String code;
    private String message;
}
