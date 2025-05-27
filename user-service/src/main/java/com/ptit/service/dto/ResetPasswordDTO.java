package com.ptit.service.dto;

import com.ptit.service.annotation.ValidOtp;
import com.ptit.service.annotation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetPasswordDTO {

    @ValidOtp
    private String otp;

    @ValidPassword
    private String newPassword;

    @ValidPassword
    private String confirmNewPassword;

    @AssertTrue(message = "New password and confirm new password do not match!")
    public boolean isPasswordMatches(){
        return newPassword.equals(confirmNewPassword);
    }
}
