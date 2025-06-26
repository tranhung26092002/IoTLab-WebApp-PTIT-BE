package com.ptit.service.controller;

import com.ptit.service.entity.User;
import com.ptit.service.entity.enums.Provider;
import com.ptit.service.entity.enums.RoleType;
import com.ptit.service.entity.enums.StateUser;
import com.ptit.service.entity.enums.TokenType;
import com.ptit.service.repository.UserRepository;
import com.ptit.service.response.AuthResponse;
import com.ptit.service.response.DataResponse;
import com.ptit.service.security.JwtService;
import com.ptit.service.service.AttendanceService;
import com.ptit.service.service.RefreshTokenService;
import com.ptit.service.service.UserLoginNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/auth/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller extends BaseController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final AttendanceService attendanceService;
    private final UserLoginNotificationService userLoginNotificationService;

    @PostMapping("/google")
    @Transactional
    public ResponseEntity<DataResponse<AuthResponse>> handleGoogleSignIn(@RequestParam String googleToken) {
        try {
            // Giải mã token Google để lấy thông tin người dùng
            Map<String, Object> claims = jwtService.validateGoogleToken(googleToken);

            String email = (String) claims.get("email");
            String name = (String) claims.get("name");
            String picture = (String) claims.get("picture");
            String oauth2Id = (String) claims.get("sub");

            // Kiểm tra xem tài khoản Google đã tồn tại chưa (kiểm tra cả oauth2Id và email)
            Optional<User> existingUser = userRepository.findByOauth2Id(oauth2Id);
            if (!existingUser.isPresent()) {
                existingUser = userRepository.findByUserName(email);
            }

            User user;
            if (existingUser.isPresent()) {
                // Nếu tài khoản đã tồn tại, lấy thông tin user hiện tại
                user = existingUser.get();
                // Nếu user chưa có oauth2Id, cập nhật thêm
                if (user.getOauth2Id() == null) {
                    user.setOauth2Id(oauth2Id);
                    user.setAuthProvider(Provider.GOOGLE);
                    userRepository.save(user);
                }
            } else {
                // Nếu tài khoản chưa tồn tại, tạo tài khoản mới
                user = User.builder()
                        .oauth2Id(oauth2Id)
                        .email(email)
                        .fullName(name)
                        .avatarUrl(picture)
                        .avatarSource(Provider.GOOGLE)
                        .authProvider(Provider.GOOGLE)
                        .userName(email) // Sử dụng email làm username
                        .status(StateUser.ACTIVE)
                        .roleType(RoleType.STUDENT) // Mặc định là STUDENT
                        .build();
                userRepository.save(user);
            }

            // Tạo JWT token
            String accessToken = jwtService.generateToken(user, user.getId());
            String refreshToken = jwtService.generateRefreshToken(user);

            // Xử lý refresh token
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

            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType(TokenType.BEARER)
                    .message(message)
                    .build();

            return success(authResponse);

        } catch (Exception e) {
            log.error("Lỗi khi xử lý đăng nhập Google: ", e);
            AuthResponse errorResponse = AuthResponse.builder()
                    .message("Invalid Google token: " + e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(DataResponse.badRequest("Invalid Google token: " + e.getMessage()));
        }
    }

    // Giữ lại endpoint cũ cho OAuth2 callback
    @GetMapping("/success")
    @Transactional
    public RedirectView oauth2Success(Authentication authentication) {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");
        String oauth2Id = (String) attributes.get("sub");

        // Kiểm tra xem tài khoản Google đã tồn tại chưa (kiểm tra cả oauth2Id và email)
        Optional<User> existingUser = userRepository.findByOauth2Id(oauth2Id);
        if (!existingUser.isPresent()) {
            existingUser = userRepository.findByEmail(email);
        }

        User user;
        if (existingUser.isPresent()) {
            // Nếu tài khoản đã tồn tại, lấy thông tin user hiện tại
            user = existingUser.get();
            // Nếu user chưa có oauth2Id, cập nhật thêm
            if (user.getOauth2Id() == null) {
                user.setOauth2Id(oauth2Id);
                user.setAuthProvider(Provider.GOOGLE);
                userRepository.save(user);
            }
        } else {
            // Nếu tài khoản chưa tồn tại, tạo tài khoản mới
            user = User.builder()
                    .oauth2Id(oauth2Id)
                    .email(email)
                    .fullName(name)
                    .avatarUrl(picture)
                    .avatarSource(Provider.GOOGLE)
                    .authProvider(Provider.GOOGLE)
                    .userName(email)
                    .status(StateUser.ACTIVE)
                    .roleType(RoleType.STUDENT)
                    .build();
            userRepository.save(user);
        }

        // Tạo JWT token
        String accessToken = jwtService.generateToken(user, user.getId());
        String refreshToken = jwtService.generateRefreshToken(user);

        // Xử lý refresh token
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

        String frontendUrl = "http://localhost:4000/oauth-callback";
        String redirectUrl = frontendUrl + "?accessToken=" + accessToken
                + "&refreshToken=" + refreshToken
                + "&tokenType=BEARER"
                + "&message=" + message;

        return new RedirectView(redirectUrl);
    }
}