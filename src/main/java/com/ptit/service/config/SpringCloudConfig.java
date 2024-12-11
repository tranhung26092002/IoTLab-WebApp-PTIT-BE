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
                .route("chat-service", r -> r.path("/chat/**")
                        .filters(f -> f.filter(filterFactory.apply(filterConfig)))
                        .uri("lb://CHAT-SERVICE"))

                .route("device-service", r -> r.path("/device/**")
                        .filters(f -> f.filter(filterFactory.apply(filterConfig)))
                        .uri("lb://DEVICE-SERVICE"))

                .route("practice-service", r -> r.path("/practice/**")
                        .filters(f -> f.filter(filterFactory.apply(filterConfig)))
                        .uri("lb://PRACTICE-SERVICE"))

                .route("product-service", r -> r.path("/product/**")
                        .filters(f -> f.filter(filterFactory.apply(filterConfig)))
                        .uri("lb://PRODUCT-SERVICE"))

                .route("report-service", r -> r.path("/report/**")
                        .filters(f -> f.filter(filterFactory.apply(filterConfig)))
                        .uri("lb://REPORT-SERVICE"))

                .route("task-service", r -> r.path("/task/**")
                        .filters(f -> f.filter(filterFactory.apply(filterConfig)))
                        .uri("lb://TASK-SERVICE"))

                .route("user-service", r -> r.path("/user/**")
                        .filters(f -> f.filter(filterFactory.apply(filterConfig)))
                        .uri("lb://USER-SERVICE"))

                .route("storage-service", r -> r.path("/storage/**")
                        .filters(f -> f.filter(filterFactory.apply(filterConfig)))
                        .uri("lb://STORAGE-SERVICE"))

                .build();
    }

}

