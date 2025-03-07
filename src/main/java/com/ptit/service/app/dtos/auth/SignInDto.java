package com.ptit.service.app.dtos.auth;

import com.ptit.service.domain.annotations.ValidPhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignInDto implements Serializable {
    @NotBlank(message = "User Name is required!")
    private String userName;

    @NotBlank(message = "Password is required!")
    private String password;
}
