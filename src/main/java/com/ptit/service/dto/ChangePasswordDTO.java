package com.ptit.service.dto;

import com.ptit.service.annotation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePasswordDTO {

    @NotBlank(message = "You must enter your current password!")
    private String currentPassword;

    @ValidPassword
    private String newPassword;


    @ValidPassword
    private String confirmNewPassword;


    @AssertTrue(message = "New password and confirm new password do not match!")
    public boolean isPasswordMatches(){
        return newPassword.equals(confirmNewPassword);
    }


    @AssertTrue(message = "New password must be different from the current password!")
    public boolean isNewPasswordNotEqualOldPassword(){
        return !newPassword.equals(currentPassword);
    }
}
