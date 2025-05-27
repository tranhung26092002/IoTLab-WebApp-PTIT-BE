package com.ptit.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignInDTO implements Serializable {
    @NotBlank(message = "User Name is required!")
    private String userName;

    @NotBlank(message = "Password is required!")
    private String password;
}
