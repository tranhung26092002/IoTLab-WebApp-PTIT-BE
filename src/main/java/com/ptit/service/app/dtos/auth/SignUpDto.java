package com.ptit.service.app.dtos.auth;

import com.ptit.service.domain.annotations.ValidPassword;
import lombok.*;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpDto {
    @NotNull(message = "OtpCodeDto is required!")
    private OtpCodeDto otpCodeDto;

    private String username;

    @ValidPassword
    private String password;

    @ValidPassword
    private String confirmPassword;

    @AssertTrue(message = "Password and confirm password do not match!")
    public boolean isPasswordMatches() {
        return password != null && password.equals(confirmPassword);
    }
}
