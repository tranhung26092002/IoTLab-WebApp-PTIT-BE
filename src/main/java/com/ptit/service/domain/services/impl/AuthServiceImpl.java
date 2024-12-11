package com.ptit.service.domain.services.impl;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.ptit.service.domain.enums.RoleType;
import com.ptit.service.domain.enums.StateUser;
import com.ptit.service.security.JwtService;
import com.ptit.service.app.dtos.auth.PhoneNumberDto;
import com.ptit.service.app.dtos.auth.ResetPasswordDto;
import com.ptit.service.app.dtos.auth.SignInDto;
import com.ptit.service.app.dtos.auth.SignUpDto;
import com.ptit.service.app.responses.MessageResponse;
import com.ptit.service.app.responses.auth.AuthResponse;
import com.ptit.service.app.responses.auth.OTPResponse;
import com.ptit.service.domain.entities.PasswordResetToken;
import com.ptit.service.domain.entities.User;
import com.ptit.service.domain.enums.TokenType;
import com.ptit.service.domain.exceptions.ErrorMessage;
import com.ptit.service.domain.repositories.PasswordResetTokenRepository;
import com.ptit.service.domain.repositories.UserRepository;
import com.ptit.service.domain.services.AuthService;
import com.ptit.service.domain.services.OTPService;
import com.ptit.service.domain.services.RefreshTokenService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final OTPService otpService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private static final int LENGTH_OF_RANDOM_USER_NAME = 12;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private ModelMapper mapper;

    @Override
    @Transactional
    public AuthResponse signUp(SignUpDto signUpDto) {
        boolean isVerifiedOtp = otpService.verifyOTP(signUpDto.getOtpCodeDto());
        if (!isVerifiedOtp) {
            throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessage.OTP_IS_INVALID.val());
        }

        User user = userRepository.save(mapDtoToEntity(signUpDto));

        log.info("{}",user);

        String accessToken = jwtService.generateToken(user, user.getId());
        String refreshToken = jwtService.generateRefreshToken(user);

        refreshTokenService.saveUserToken(user,refreshToken);


        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(TokenType.BEARER)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse signIn(SignInDto signInDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInDto.getPhoneNumber(),
                        signInDto.getPassword()
                )
        );

        User user = userRepository.findByPhoneNumber(signInDto.getPhoneNumber())
                .orElseThrow(() -> new ExceptionOm(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND.val()));

        String accessToken = jwtService.generateToken(user, user.getId());
        String refreshToken = jwtService.generateRefreshToken(user);

        refreshTokenService.revokeAllUserToken(user);
        refreshTokenService.saveUserToken(user, refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(TokenType.BEARER)
                .build();
    }

    @Override
    @Transactional
    public OTPResponse forgotPassword(PhoneNumberDto phoneNumberDto) {
        String phoneNumber = phoneNumberDto.getPhoneNumber();

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(
                        () -> new ExceptionOm(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND)
                );

        String otpCode = otpService.generateOTP();

        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(otpCode)
                .user(user)
                .build();

        passwordResetTokenRepository.markTokensAsUsedByUserId(user.getId());
        passwordResetTokenRepository.save(passwordResetToken);

        return OTPResponse.builder()
                .otpCode(otpCode)
                .build();
    }

    @Override
    @Transactional
    public MessageResponse resetPassword(ResetPasswordDto resetPasswordDto) {
        String token = resetPasswordDto.getOtp();

        PasswordResetToken passwordResetToken = passwordResetTokenRepository
                .findByToken(token)
                .orElseThrow(
                        () -> new ExceptionOm(HttpStatus.NOT_FOUND, ErrorMessage.PASSWORD_RESET_TOKEN_NOT_FOUND.val())
                );

        log.info("{}", passwordResetToken.getToken());

        // check mat khau moi khong trung mat khau cu
        User user = userRepository.findByPasswordToken(token).orElseThrow(
                () -> new ExceptionOm(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND.val())
        );

        String currentPassword = user.getPassword();
        if(passwordEncoder.matches(resetPasswordDto.getNewPassword(), currentPassword)){
            throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessage.CURRENT_PASSWORD_SAME_NEW_PASSWORD);
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

    private String randomUserName(){
        Random random = new SecureRandom();
        StringBuilder userName = new StringBuilder(LENGTH_OF_RANDOM_USER_NAME);

        for (int i = 0; i < LENGTH_OF_RANDOM_USER_NAME; i++) {
            userName.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return userName.toString();
    }

    private User mapDtoToEntity(SignUpDto request){
        String phoneNumber = request.getOtpCodeDto().getPhoneNumber();

        String password = request.getPassword();

        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            request.setUsername(randomUserName());
        }
        String userName = request.getUsername();

        // encode password before save in database
        String encodedPassword = passwordEncoder.encode(password);

        // khi dang ky tai khoan mac dinh la customer
        return User.builder()
                .userName(userName)
                .phoneNumber(phoneNumber)
                .roleType(RoleType.STUDENT)
                .password(encodedPassword)
                .deleted(false)
                .status(StateUser.ACTIVE)
                .build();
    }
}
