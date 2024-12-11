package com.ptit.service.security;

import com.ptit.service.domain.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@Getter
@Slf4j
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.key}")
    private String jwtKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${refresh-token.key}")
    private String jwtRefreshKey;

    @Value(("${refresh-token.expiration}"))
    private long jwtRefreshExpiration;

    public Long extractUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey(jwtKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.get("userId").toString());
    }

    public List<GrantedAuthority> extractAuthorities(String token, String key) {
        // Trích xuất danh sách authorities từ JWT
        List<String> authorities = extractClaim(token,
                claims -> claims.get("authorities") != null
                        ? (List<String>) claims.get("authorities")
                        : Collections.emptyList(),
                getSigningKey(key));

        return authorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority))
                .collect(Collectors.toList());
    }

    public String extractUserName(String token, String key) {
        return extractClaim(token, Claims::getSubject, getSigningKey(key));
    }

    public String generateToken(UserDetails userDetails, Long userId) {
        return generateToken(new HashMap<>(), userDetails, userId);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, Long userId) {
        extraClaims.put("userId", userId);
        return buildToken(extraClaims, userDetails, jwtExpiration, jwtKey);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, jwtRefreshExpiration, jwtRefreshKey);
    }

    public boolean isTokenValid(String token, UserDetails userDetails, String key) {
        final String userName = extractUserName(token, key);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token, key);
    }


    private String buildTokenWithExpiry(Map<String, Object> extraClaims, UserDetails userDetails, Date expiration, String key) {
        // Thêm vai trò vào claims
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // Chuyển đổi thành danh sách vai trò (role)
                .collect(Collectors.toList());

        extraClaims.put("authorities", roles); // Đưa vào claims

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiration)
                .signWith(getSigningKey(key), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateNewRefreshTokenWithOldExpiryTime(String oldRefreshToken, UserDetails userDetails) {
        Date oldExpiryTime = extractExpiration(oldRefreshToken, jwtRefreshKey);
        return buildTokenWithExpiry(new HashMap<>(), userDetails, oldExpiryTime, jwtRefreshKey);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration, String key) {
        // Thêm vai trò vào claims
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // Chuyển đổi thành danh sách vai trò (role)
                .collect(Collectors.toList());

        extraClaims.put("authorities", roles); // Đưa vào claims

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(key), SignatureAlgorithm.HS256)
                .compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver, Key key) {
        final Claims claims = extractAllClaims(token, key);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenExpired(String token, String key) {
        return extractExpiration(token, key).before(new Date());
    }

    private Date extractExpiration(String token, String key) {
        return extractClaim(token, Claims::getExpiration, getSigningKey(key));
    }

    public Claims extractAllClaims(String token, Key key) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Key getSigningKey(String key) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}

