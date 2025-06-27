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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(tags = "Authentication")
public class AuthController extends BaseController {
    private final AuthService authService;
    private final OTPService otpService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/check-email")
    @ApiOperation("Gửi OTP qua email")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Gửi OTP thành công"),
        @ApiResponse(code = 400, message = "Email không hợp lệ"),
        @ApiResponse(code = 500, message = "Lỗi server")
    })
    public ResponseEntity<DataResponse<OTPResponse>> sendOtp(
            @ApiParam(value = "Thông tin email", required = true) 
            @Valid @RequestBody EmailDTO request) {
        OTPResponse response = otpService.sendOTP(request);
        return success(response);
    }

    @PostMapping("/sign-up")
    @ApiOperation("Đăng ký tài khoản mới")
    @ApiResponses({
        @ApiResponse(code = 201, message = "Đăng ký thành công"),
        @ApiResponse(code = 400, message = "Dữ liệu không hợp lệ"),
        @ApiResponse(code = 409, message = "Tài khoản đã tồn tại"),
        @ApiResponse(code = 500, message = "Lỗi server")
    })
    public ResponseEntity<DataResponse<AuthResponse>> signUp(
            @ApiParam(value = "Thông tin đăng ký", required = true) 
            @Valid @RequestBody SignUpDTO request) {
        AuthResponse response = authService.signUp(request);
        return created(response);
    }

    @PostMapping("/sign-in")
    @ApiOperation("Đăng nhập")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Đăng nhập thành công"),
        @ApiResponse(code = 400, message = "Thông tin đăng nhập không đúng"),
        @ApiResponse(code = 401, message = "Tài khoản bị khóa"),
        @ApiResponse(code = 500, message = "Lỗi server")
    })
    public ResponseEntity<DataResponse<AuthResponse>> signIn(
            @ApiParam(value = "Thông tin đăng nhập", required = true) 
            @Valid @RequestBody SignInDTO request) {
        AuthResponse response = authService.signIn(request);
        return success(response);
    }

    @PostMapping("/refresh-token")
    @ApiOperation("Làm mới token")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Làm mới token thành công"),
        @ApiResponse(code = 401, message = "Token không hợp lệ"),
        @ApiResponse(code = 500, message = "Lỗi server")
    })
    public ResponseEntity<DataResponse<AuthResponse>> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        AuthResponse res = refreshTokenService.refreshToken(request, response);
        return success(res);
    }

    @PostMapping("/forgot-password")
    @ApiOperation("Quên mật khẩu - Gửi OTP")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Gửi OTP thành công"),
        @ApiResponse(code = 400, message = "Email không tồn tại"),
        @ApiResponse(code = 500, message = "Lỗi server")
    })
    public ResponseEntity<DataResponse<OTPResponse>> forgotPassword(
            @ApiParam(value = "Email cần reset mật khẩu", required = true) 
            @Valid @RequestBody EmailDTO request) {
        OTPResponse response = authService.forgotPassword(request);
        return success(response);
    }

    @PostMapping("/reset-password")
    @ApiOperation("Đặt lại mật khẩu")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Đặt lại mật khẩu thành công"),
        @ApiResponse(code = 400, message = "OTP không đúng hoặc hết hạn"),
        @ApiResponse(code = 500, message = "Lỗi server")
    })
    public ResponseEntity<DataResponse<MessageResponse>> resetPassword(
            @ApiParam(value = "Thông tin reset mật khẩu", required = true) 
            @Valid @RequestBody ResetPasswordDTO request) {
        MessageResponse response = authService.resetPassword(request);
        return success(response);
    }

    @RequestMapping("/valid-token")
    @ApiOperation("Kiểm tra tính hợp lệ của token")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Token hợp lệ"),
        @ApiResponse(code = 401, message = "Token không hợp lệ")
    })
    public ResponseEntity validToken(
            @ApiParam(value = "JWT Token", required = true) 
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
