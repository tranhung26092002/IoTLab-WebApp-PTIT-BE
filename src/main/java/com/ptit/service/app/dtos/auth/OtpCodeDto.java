package com.ptit.service.app.dtos.auth;

import com.ptit.service.domain.annotations.ValidOtp;
import com.ptit.service.domain.annotations.ValidPhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpCodeDto {
    @ValidPhoneNumber
    private String phoneNumber;

    @ValidOtp
    private String otpCode;
}
