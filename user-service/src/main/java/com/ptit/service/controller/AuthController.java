package com.ptit.service.controller;

import com.ptit.service.dto.EmailDTO;
import com.ptit.service.dto.ResetPasswordDTO;
import com.ptit.service.dto.SignInDTO;
import com.ptit.service.dto.SignUpDTO;
import com.ptit.service.response.AuthResponse;
import com.ptit.service.response.DataResponse;
import com.ptit.service.response.MessageResponse;
import com.ptit.service.response.OTPResponse;
import com.ptit.service.service.AuthService;
import com.ptit.service.service.OTPService;
import com.ptit.service.service.RefreshTokenService;
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
public class AuthController extends BaseController {
    private final AuthService authService;
    private final OTPService otpService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/check-email")
    public ResponseEntity<DataResponse<OTPResponse>> sendOtp(@Valid @RequestBody EmailDTO request) {
        OTPResponse response = otpService.sendOTP(request);
        return success(response);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<DataResponse<AuthResponse>> signUp(@Valid @RequestBody SignUpDTO request) {
        AuthResponse response = authService.signUp(request);
        return created(response);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<DataResponse<AuthResponse>> signIn(@Valid @RequestBody SignInDTO request) {
        AuthResponse response = authService.signIn(request);
        return success(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<DataResponse<AuthResponse>> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        AuthResponse res = refreshTokenService.refreshToken(request, response);
        return success(res);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<DataResponse<OTPResponse>> forgotPassword(@Valid @RequestBody EmailDTO request) {
        OTPResponse response = authService.forgotPassword(request);
        return success(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<DataResponse<MessageResponse>> resetPassword(@Valid @RequestBody ResetPasswordDTO request) {
        MessageResponse response = authService.resetPassword(request);
        return success(response);
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
