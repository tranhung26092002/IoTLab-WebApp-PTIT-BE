package com.ptit.service.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class TokenDecodeGatewayFilterFactory
        extends AbstractGatewayFilterFactory<TokenDecodeGatewayFilterFactory.Config> {

    private static final Logger logger = LoggerFactory.getLogger(TokenDecodeGatewayFilterFactory.class);

    @Value("${gateway.valid-token-url}")
    private String VALID_TOKEN_URL;

    public TokenDecodeGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Lấy token từ header
            String token = exchange.getRequest().getHeaders().getFirst("Authorization");
            String path = exchange.getRequest().getPath().value();

            logger.debug("Processing request for path: {}", path);

            // Bỏ qua authentication cho một số endpoint công khai
            if (isPublicEndpoint(path)) {
                logger.debug("Skipping authentication for public endpoint: {}", path);
                return chain.filter(exchange);
            }

            if (token != null && !token.equals("Bear null") && !token.equals("Bearer null")) {
                logger.debug("Validating token for path: {}", path);
                Object claim = decodeToken(token);

                // Kiểm tra claim null hoặc không phải là Map
                if (claim instanceof Map) {
                    Map<String, Object> claimMap = (Map<String, Object>) claim;
                    if (Boolean.FALSE.equals(claimMap.get("authenticate"))) {
                        logger.warn("Token validation failed for path: {}", path);
                        // Nếu không authenticate, trả về lỗi 401 Unauthorized
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }

                    // Lấy thông tin userId và authorities từ claim
                    Object userId = claimMap.get("userId");
                    Object authorities = claimMap.get("authorities");

                    logger.debug("Token validated successfully for user: {}", userId);

                    // Tạo một phiên bản mới của yêu cầu với các header được cập nhật
                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                            .header("userId", userId != null ? userId.toString() : "unknown")
                            .header("role", authorities != null ? authorities.toString() : "N/A")
                            .header("authenticate", token)
                            .build();

                    // Ghi đè yêu cầu cũ bằng yêu cầu mới đã được cập nhật
                    exchange = exchange.mutate().request(modifiedRequest).build();
                } else {
                    logger.warn("Invalid token response format for path: {}", path);
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
            } else {
                logger.warn("No valid token provided for protected endpoint: {}", path);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);
        };
    }

    private boolean isPublicEndpoint(String path) {
        // Danh sách các endpoint công khai không cần authentication
        return path.contains("/auth/sign-in") ||
                path.contains("/auth/sign-up") ||
                path.contains("/auth/check-email") ||
                path.contains("/auth/forgot-password") ||
                path.contains("/auth/reset-password") ||
                path.contains("/swagger-ui") ||
                path.contains("/v2/api-docs") ||
                path.contains("/v3/api-docs") ||
                path.contains("/actuator") ||
                path.contains("/health") ||
                path.contains("/info");
    }

    public static class Config {
        // Cấu hình nếu cần
    }

    // Giải mã token và kiểm tra tính hợp lệ của token
    public Object decodeToken(String token) {
        try {
            logger.debug("Attempting to validate token at URL: {}", VALID_TOKEN_URL);

            // Tạo RestTemplate để gửi yêu cầu HTTP
            RestTemplate restTemplate = new RestTemplate();

            // Tạo HttpHeaders và đặt token vào header
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Gửi yêu cầu HTTP GET đến URL VALID_TOKEN_URL
            ResponseEntity<Object> response = restTemplate.exchange(VALID_TOKEN_URL, HttpMethod.GET, entity,
                    Object.class);

            // Kiểm tra phản hồi từ máy chủ
            if (response.getStatusCode() == HttpStatus.OK) {
                logger.debug("Token validation successful");
                return response.getBody();
            } else {
                logger.warn("Token validation failed with status: {}", response.getStatusCode());
                return createErrorResponse(false, "Token validation failed");
            }
        } catch (HttpClientErrorException e) {
            logger.error("HTTP error during token validation: {} - {}", e.getStatusCode(), e.getMessage());
            return createErrorResponse(false, "HTTP error: " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            logger.error("Connection error during token validation: {}", e.getMessage());
            return createErrorResponse(false, "Service unavailable");
        } catch (Exception e) {
            logger.error("Unexpected error during token validation: {}", e.getMessage(), e);
            return createErrorResponse(false, "Internal error");
        }
    }

    private Map<String, Object> createErrorResponse(boolean authenticate, String error) {
        Map<String, Object> response = new HashMap<>();
        response.put("authenticate", authenticate);
        response.put("error", error);
        return response;
    }
}
