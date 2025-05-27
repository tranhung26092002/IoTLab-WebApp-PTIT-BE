package com.ptit.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf().disable() // Tắt CSRF
                .cors().and() // Kích hoạt CORS
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll() // Tùy chỉnh quyền truy cập
                );

        return http.build();
    }
}

