package com.ptit.service.app.responses.auth;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OTPResponse {
    private String otpCode;
}
