package com.ptit.service.service;

import com.ptit.service.dto.EmailDTO;
import com.ptit.service.dto.ResetPasswordDTO;
import com.ptit.service.dto.SignInDTO;
import com.ptit.service.dto.SignUpDTO;
import com.ptit.service.response.MessageResponse;
import com.ptit.service.response.AuthResponse;
import com.ptit.service.response.OTPResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    AuthResponse signUp(SignUpDTO signUpDto);
    AuthResponse signIn(SignInDTO signInDto);
    OTPResponse forgotPassword(EmailDTO emailDto);
    MessageResponse resetPassword(ResetPasswordDTO resetPasswordDto);

    Object validToken(String token);
}
