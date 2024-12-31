package com.ptit.service.app.dtos.auth;

import com.ptit.service.domain.annotations.ValidOtp;
import com.ptit.service.domain.annotations.ValidPhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpCodeDto {
    @Email
    private String email;

    @ValidOtp
    private String otpCode;
}
