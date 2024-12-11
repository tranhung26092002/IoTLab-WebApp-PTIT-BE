package com.ptit.service.domain.services;

import com.ptit.service.app.responses.auth.AuthResponse;
import com.ptit.service.domain.entities.RefreshToken;
import com.ptit.service.domain.entities.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public interface RefreshTokenService {
    // saveRefreshTokenUser
    void saveUserToken(User user, String token);

    // findRefreshToken
    RefreshToken findRefreshToken(String token);

    // revokeAllRefreshToken
    void revokeAllUserToken(User user);
    // RefreshToken

    AuthResponse refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException;

}
