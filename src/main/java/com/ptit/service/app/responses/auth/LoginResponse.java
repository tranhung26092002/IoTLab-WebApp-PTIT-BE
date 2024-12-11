package com.ptit.service.app.responses.auth;

import lombok.Data;

@Data
public class LoginResponse {
    private String accessToken;
    private Long userId;
}
