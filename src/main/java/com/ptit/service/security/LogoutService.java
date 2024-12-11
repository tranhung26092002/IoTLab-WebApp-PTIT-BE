package com.ptit.service.security;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.ptit.service.domain.entities.RefreshToken;
import com.ptit.service.domain.exceptions.ErrorMessage;
import com.ptit.service.domain.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {

    private final RefreshTokenRepository refreshTokenRepository;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwtRefreshToken;

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            return;
        }
        jwtRefreshToken = authHeader.substring(7);


        RefreshToken refreshToken = refreshTokenRepository.findByToken(jwtRefreshToken).orElseThrow(
                () -> new ExceptionOm(HttpStatus.NOT_FOUND, ErrorMessage.REFRESH_TOKEN_NOT_FOUND.val())
        );

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
}
