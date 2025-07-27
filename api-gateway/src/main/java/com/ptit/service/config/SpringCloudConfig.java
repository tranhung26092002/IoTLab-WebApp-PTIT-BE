package com.ptit.service.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringCloudConfig {

        @Bean
        public RouteLocator gatewayRoutes(RouteLocatorBuilder builder, TokenDecodeGatewayFilterFactory filterFactory) {
                TokenDecodeGatewayFilterFactory.Config filterConfig = new TokenDecodeGatewayFilterFactory.Config();

                return builder.routes()
                                .route("user-service", r -> r.path("/api/v1/user/**")
                                                .filters(f -> f.rewritePath("/api/v1/user/(?<segment>.*)",
                                                                "/user/${segment}")
                                                                .filter(filterFactory.apply(filterConfig)))
                                                .uri("lb://USER-SERVICE"))

                                .route("device-service", r -> r.path("/api/v1/device/**")
                                                .filters(f -> f.rewritePath("/api/v1/device/(?<segment>.*)",
                                                                "/device/${segment}")
                                                                .filter(filterFactory.apply(filterConfig)))
                                                .uri("lb://DEVICE-SERVICE"))

                                .route("practice-service", r -> r.path("/api/v1/practice/**")
                                                .filters(f -> f.rewritePath("/api/v1/practice/(?<segment>.*)",
                                                                "/practice/${segment}")
                                                                .filter(filterFactory.apply(filterConfig)))
                                                .uri("lb://PRACTICE-SERVICE"))

                                .route("storage-service", r -> r.path("/api/v1/storage/**")
                                                .filters(f -> f.rewritePath("/api/v1/storage/(?<segment>.*)",
                                                                "/storage/${segment}")
                                                                .filter(filterFactory.apply(filterConfig)))
                                                .uri("lb://STORAGE-SERVICE"))

                                .build();
        }

}
