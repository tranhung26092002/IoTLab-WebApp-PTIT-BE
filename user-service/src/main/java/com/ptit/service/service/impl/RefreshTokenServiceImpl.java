package com.ptit.service.service.impl;

import com.ptit.service.entity.RefreshToken;
import com.ptit.service.entity.User;
import com.ptit.service.entity.enums.TokenType;
import com.ptit.service.exception.BaseException;
import com.ptit.service.exception.BusinessException;
import com.ptit.service.exception.ErrorCode;
import com.ptit.service.repository.RefreshTokenRepository;
import com.ptit.service.repository.UserRepository;
import com.ptit.service.response.AuthResponse;
import com.ptit.service.security.JwtService;
import com.ptit.service.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void saveUserToken(User user, String token) {
        RefreshToken refreshToken = RefreshToken.builder()
                .revoked(false)
                .token(token)
                .tokenType(TokenType.BEARER)
                .user(user)
                .build();
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken findRefreshToken(String token) {
        return refreshTokenRepository
                .findByToken(token)
                .orElseThrow(
                        () -> new BusinessException(ErrorCode.TOKEN_REFRESH_NOT_FOUND)
                );
    }

    @Override
    @Transactional
    public void revokeAllUserToken(User user) {
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAllValidTokenByUserId(user.getId());
        if (refreshTokens.isEmpty()) {
            return;
        }
        refreshTokens.forEach(token -> token.setRevoked(true));
        refreshTokenRepository.saveAll(refreshTokens);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader("Authorization");
        final String refreshToken;
        final String phoneNumber;
        final String refreshTokenKey = jwtService.getJwtRefreshKey();

        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            throw new BaseException(ErrorCode.TOKEN_REFRESH_INVALID);
        }

        refreshToken = authHeader.substring(7);
        phoneNumber = jwtService.extractUserName(refreshToken, refreshTokenKey);

        if (phoneNumber == null) {
            throw new BusinessException(ErrorCode.TOKEN_REFRESH_INVALID);
        }

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.USER_NOT_FOUND));

        // kiem tra xem trong database co ton tai refreshToken tuong ung hay khong
        RefreshToken existRefreshToken = refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.TOKEN_REFRESH_NOT_FOUND));

        if (!jwtService.isTokenValid(existRefreshToken.getToken(), user, refreshTokenKey)) {
            throw new BusinessException(ErrorCode.TOKEN_REFRESH_INVALID);
        }

        String newAccessToken = jwtService.generateToken(user, user.getId());
        String newRefreshToken = jwtService.generateNewRefreshTokenWithOldExpiryTime(existRefreshToken.getToken(), user);

        revokeAllUserToken(user);
        saveUserToken(user, newRefreshToken);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType(TokenType.BEARER)
                .build();
    }
}
