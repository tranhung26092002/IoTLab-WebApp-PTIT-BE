package com.ptit.service.service.impl;

import com.ptit.service.dto.EmailDTO;
import com.ptit.service.dto.ResetPasswordDTO;
import com.ptit.service.dto.SignInDTO;
import com.ptit.service.dto.SignUpDTO;
import com.ptit.service.entity.PasswordResetToken;
import com.ptit.service.entity.User;
import com.ptit.service.entity.enums.RoleType;
import com.ptit.service.entity.enums.StateUser;
import com.ptit.service.entity.enums.TokenType;
import com.ptit.service.exception.BaseException;
import com.ptit.service.exception.ErrorCode;
import com.ptit.service.repository.PasswordResetTokenRepository;
import com.ptit.service.repository.UserRepository;
import com.ptit.service.response.AuthResponse;
import com.ptit.service.response.MessageResponse;
import com.ptit.service.response.OTPResponse;
import com.ptit.service.security.JwtService;
import com.ptit.service.service.*;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final int LENGTH_OF_RANDOM_USER_NAME = 12;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final OTPService otpService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final AttendanceService attendanceService;
    private final UserLoginNotificationService userLoginNotificationService;

    @Override
    @Transactional
    public AuthResponse signUp(SignUpDTO signUpDto) {
        // boolean isVerifiedOtp = otpService.verifyOTP(signUpDto.getOtpCodeDto());
        // if (!isVerifiedOtp) {
        // throw new ExceptionOm(HttpStatus.BAD_REQUEST,
        // ErrorMessage.OTP_IS_INVALID.val());
        // }

        User user = userRepository.save(mapDtoToEntity(signUpDto));

        String accessToken = jwtService.generateToken(user, user.getId());
        String refreshToken = jwtService.generateRefreshToken(user);

        refreshTokenService.saveUserToken(user, refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(TokenType.BEARER)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse signIn(SignInDTO signInDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInDto.getUserName(),
                        signInDto.getPassword()));

        User user = userRepository.findByUserName(signInDto.getUserName())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        String accessToken = jwtService.generateToken(user, user.getId());
        String refreshToken = jwtService.generateRefreshToken(user);

        refreshTokenService.revokeAllUserToken(user);
        refreshTokenService.saveUserToken(user, refreshToken);

        // Kiểm tra và điểm danh
        boolean checkInSuccess = attendanceService.checkAndMarkAttendance(user);
        String message = checkInSuccess ? "Đăng nhập & điểm danh thành công!"
                : "Đăng nhập thành công, đã điểm danh trước đó.";

        // Gửi thông báo khi sinh viên đăng nhập
        if (user.getRoleType() == RoleType.STUDENT) {
            log.info("Gửi thông báo khi sinh viên đăng nhập: {}", user);
            userLoginNotificationService.sendUserLoginNotification(user);
        }

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(TokenType.BEARER)
                .message(message) // Gửi thông báo để hiển thị trên UI
                .build();
    }

    @Override
    @Transactional
    public OTPResponse forgotPassword(EmailDTO emailDto) {
        String email = emailDto.getEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new BaseException(ErrorCode.USER_NOT_FOUND));

        String otpCode = otpService.generateOTP();

        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(otpCode)
                .user(user)
                .build();

        passwordResetTokenRepository.markTokensAsUsedByUserId(user.getId());
        passwordResetTokenRepository.save(passwordResetToken);

        try {
            // Tạo context cho Thymeleaf template
            Context context = new Context();
            context.setVariable("otp", otpCode);

            // Gửi email
            emailService.sendEmail(email, "Your OTP Code", "otp-template", context);
        } catch (Exception e) {
            throw new BaseException(ErrorCode.OTP_NOT_SEND);
        }

        return OTPResponse.builder()
                .otpCode(otpCode)
                .build();
    }

    @Override
    @Transactional
    public MessageResponse resetPassword(ResetPasswordDTO resetPasswordDto) {
        String token = resetPasswordDto.getOtp();

        PasswordResetToken passwordResetToken = passwordResetTokenRepository
                .findByToken(token)
                .orElseThrow(
                        () -> new BaseException(ErrorCode.USER_PASSWORD_RESET_TOKEN_NOT_FOUND));

        log.info("{}", passwordResetToken.getToken());

        // check mat khau moi khong trung mat khau cu
        User user = userRepository.findByPasswordToken(token).orElseThrow(
                () -> new BaseException(ErrorCode.USER_NOT_FOUND));

        String currentPassword = user.getPassword();
        if (passwordEncoder.matches(resetPasswordDto.getNewPassword(), currentPassword)) {
            throw new BaseException(ErrorCode.USER_PASSWORD_SAME_AS_OLD);
        }

        user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
        userRepository.save(user);
        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);

        return MessageResponse.builder()
                .message("Reset password successfully!")
                .build();
    }

    public Object validToken(String token) {
        try {
            // Giải mã token để lấy claims
            Claims claims = jwtService.extractAllClaims(token, jwtService.getSigningKey(jwtService.getJwtKey()));

            // Kiểm tra xem token có hết hạn không
            if (jwtService.isTokenExpired(token, jwtService.getJwtKey())) {
                throw new IllegalArgumentException("Token đã hết hạn.");
            }

            // Tạo phản hồi khi token hợp lệ
            Map<String, Object> response = new HashMap<>();
            response.put("userId", claims.get("userId"));
            response.put("authorities", claims.get("authorities"));
            response.put("authenticate", true);

            return response;
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.error("Token không đúng định dạng: ", e);
            return Map.of("authenticate", false, "error", "Token không đúng định dạng");
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("Token đã hết hạn: ", e);
            return Map.of("authenticate", false, "error", "Token đã hết hạn");
        } catch (Exception ex) {
            log.error("Lỗi xác thực token: ", ex);
            return Map.of("authenticate", false, "error", "Token không hợp lệ");
        }
    }

    private String randomUserName() {
        Random random = new SecureRandom();
        StringBuilder userName = new StringBuilder(LENGTH_OF_RANDOM_USER_NAME);

        for (int i = 0; i < LENGTH_OF_RANDOM_USER_NAME; i++) {
            userName.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return userName.toString();
    }

    private User mapDtoToEntity(SignUpDTO request) {
        // check user name da ton tai chua
        if (userRepository.existsByUserName(request.getUserName())) {
            throw new BaseException(ErrorCode.USER_NAME_EXISTS);
        }
        String userName = request.getUserName();

        // encode password before save in database
        String password = request.getUserName();
        String encodedPassword = passwordEncoder.encode(password);

        // khi dang ky tai khoan mac dinh la customer
        return User.builder()
                .userName(userName)
                .fullName(request.getFullName())
                .classCode(request.getClassCode())
                .roleType(RoleType.STUDENT)
                .password(encodedPassword)
                .status(StateUser.ACTIVE)
                .deleted(false)
                .build();
    }
}
