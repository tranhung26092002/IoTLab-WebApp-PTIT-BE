package com.ptit.service.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OTPResponse {
    private String otpCode;
}
