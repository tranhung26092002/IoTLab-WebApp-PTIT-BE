package com.ptit.service.domain.services;

import com.ptit.service.app.dtos.auth.PhoneNumberDto;
import com.ptit.service.app.dtos.auth.ResetPasswordDto;
import com.ptit.service.app.dtos.auth.SignInDto;
import com.ptit.service.app.dtos.auth.SignUpDto;
import com.ptit.service.app.responses.MessageResponse;
import com.ptit.service.app.responses.auth.AuthResponse;
import com.ptit.service.app.responses.auth.OTPResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    AuthResponse signUp(SignUpDto signUpDto);
    AuthResponse signIn(SignInDto signInDto);
    OTPResponse forgotPassword(PhoneNumberDto phoneNumberDto);
    MessageResponse resetPassword(ResetPasswordDto resetPasswordDto);

    Object validToken(String token);
}
