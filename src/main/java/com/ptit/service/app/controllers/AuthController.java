package com.ptit.service.app.controllers;

import com.ptit.service.app.dtos.auth.PhoneNumberDto;
import com.ptit.service.app.dtos.auth.ResetPasswordDto;
import com.ptit.service.app.dtos.auth.SignInDto;
import com.ptit.service.app.dtos.auth.SignUpDto;
import com.ptit.service.app.responses.MessageResponse;
import com.ptit.service.app.responses.auth.AuthResponse;
import com.ptit.service.app.responses.auth.OTPResponse;
import com.ptit.service.domain.services.AuthService;
import com.ptit.service.domain.services.OTPService;
import com.ptit.service.domain.services.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final OTPService otpService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/check-phone-number")
    public ResponseEntity<OTPResponse> sendOtp(@Valid @RequestBody PhoneNumberDto request){
        OTPResponse response = otpService.sendOTP(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponse> signUp(@Valid @RequestBody SignUpDto request){
        AuthResponse response = authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<AuthResponse> signIn(@Valid @RequestBody SignInDto request){
        AuthResponse response = authService.signIn(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        AuthResponse res = refreshTokenService.refreshToken(request, response);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<OTPResponse> forgotPassword(@Valid @RequestBody PhoneNumberDto request){
        OTPResponse response = authService.forgotPassword(request);
        return ResponseEntity.ok(response);
    }


    @RequestMapping("/valid-token")
    public ResponseEntity validToken(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String token) {

        // Kiểm tra token có tồn tại và đúng định dạng "Bearer <token>"
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("authenticate", false, "error", "Token không hợp lệ"));
        }

        // Loại bỏ tiền tố "Bearer " để lấy token
        String jwtToken = token.substring(7);

        return ResponseEntity.ok(authService.validToken(jwtToken));
    }
}
