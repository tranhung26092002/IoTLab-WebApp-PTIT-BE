package com.ptit.service.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String accessToken;
    private Long userId;
}
