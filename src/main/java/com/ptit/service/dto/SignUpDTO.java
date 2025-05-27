package com.ptit.service.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpDTO implements Serializable {
//    @NotNull(message = "OtpCodeDto is required!")
//    private OtpCodeDto otpCodeDto;

    private String userName;

    private String fullName;

    private String classCode;

//    @ValidPassword
//    private String password;
//
//    @ValidPassword
//    private String confirmPassword;
//
//    @AssertTrue(message = "Password and confirm password do not match!")
//    public boolean isPasswordMatches() {
//        return password != null && password.equals(confirmPassword);
//    }
}
