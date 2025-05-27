package com.ptit.service.dto;

import com.ptit.service.annotation.ValidOtp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpCodeDTO {
    @Email
    private String email;

    @ValidOtp
    private String otpCode;
}
