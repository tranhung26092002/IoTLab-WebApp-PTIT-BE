package com.ptit.service.domain.services.impl;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.ptit.service.app.responses.auth.AuthResponse;
import com.ptit.service.domain.entities.RefreshToken;
import com.ptit.service.domain.entities.User;
import com.ptit.service.domain.enums.TokenType;
import com.ptit.service.domain.exceptions.ErrorMessage;
import com.ptit.service.domain.repositories.RefreshTokenRepository;
import com.ptit.service.domain.repositories.UserRepository;
import com.ptit.service.domain.services.RefreshTokenService;
import com.ptit.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
                        () -> new ExceptionOm(HttpStatus.NOT_FOUND, ErrorMessage.REFRESH_TOKEN_NOT_FOUND)
                );
    }

    @Override
    @Transactional
    public void revokeAllUserToken(User user) {
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAllValidTokenByUserId(user.getId());
        if(refreshTokens.isEmpty()){
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

        if(authHeader == null || !authHeader.startsWith("Bearer")){
            throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_REFRESH_TOKEN);
        }

        refreshToken = authHeader.substring(7);
        phoneNumber = jwtService.extractUserName(refreshToken, refreshTokenKey);

        if (phoneNumber == null){
            throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_REFRESH_TOKEN);
        }

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() ->
                        new ExceptionOm(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND));

        // kiem tra xem trong database co ton tai refreshToken tuong ung hay khong
        RefreshToken existRefreshToken = refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(() ->
                        new ExceptionOm(HttpStatus.NOT_FOUND, ErrorMessage.REFRESH_TOKEN_NOT_FOUND));

        if (!jwtService.isTokenValid(existRefreshToken.getToken(), user, refreshTokenKey)){
            throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_REFRESH_TOKEN);
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
