package com.ptit.service.app.dtos.auth;

import com.ptit.service.domain.annotations.ValidPhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignInDto {
    @ValidPhoneNumber
    private String phoneNumber;

    @NotBlank(message = "Password is required!")
    private String password;
}
