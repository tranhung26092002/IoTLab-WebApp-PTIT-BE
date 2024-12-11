package com.ptit.service.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class TokenDecodeGatewayFilterFactory extends AbstractGatewayFilterFactory<TokenDecodeGatewayFilterFactory.Config> {
//    private static final String VALID_TOKEN_URL = "lb://USER-SERVICE/user/auth/valid-token";

    private static final String VALID_TOKEN_URL = "http://localhost:8081/user/auth/valid-token";

    public TokenDecodeGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Lấy token từ header
            String token = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (token != null && !token.equals("Bear null") && !token.equals("Bearer null")) {
                Object claim = decodeToken(token);

                // Kiểm tra claim null hoặc không phải là Map
                if (claim instanceof Map) {
                    Map<String, Object> claimMap = (Map<String, Object>) claim;
                    if (Boolean.FALSE.equals(claimMap.get("authenticate"))) {
                        // Nếu không authenticate, trả về lỗi 401 Unauthorized
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }

                    // Lấy thông tin userId và authorities từ claim
                    Object userId = claimMap.get("userId");
                    Object authorities = claimMap.get("authorities");

                    // Tạo một phiên bản mới của yêu cầu với các header được cập nhật
                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                            .header("userId", userId.toString())
                            .header("role", authorities != null ? authorities.toString() : "N/A") // Giả sử authorities là một mảng
                            .header("authenticate", token)
                            .build();

                    // Ghi đè yêu cầu cũ bằng yêu cầu mới đã được cập nhật
                    exchange = exchange.mutate().request(modifiedRequest).build();
                }
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
        // Cấu hình nếu cần
    }

    // Giải mã token và kiểm tra tính hợp lệ của token
    public Object decodeToken(String token) {
        // Tạo RestTemplate để gửi yêu cầu HTTP
        RestTemplate restTemplate = new RestTemplate();

        // Tạo HttpHeaders và đặt token vào header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Gửi yêu cầu HTTP GET đến URL VALID_TOKEN_URL
        ResponseEntity<Object> response = restTemplate.exchange(VALID_TOKEN_URL, HttpMethod.GET, entity, Object.class);

        // Kiểm tra phản hồi từ máy chủ
        if (response.getStatusCode() == HttpStatus.OK) {
            // Nếu token hợp lệ, thêm các thông tin từ token vào header của yêu cầu
            return response.getBody();
        }  else {
            return "{\n" +
                    "    \"authenticate\": false\n" +
                    "}";
        }
    }
}
