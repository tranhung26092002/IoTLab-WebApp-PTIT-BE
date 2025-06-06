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
                .route("chat-service", r -> r.path("/api/v1/chat/**")
                        .filters(f -> f.rewritePath("/api/v1/chat/(?<segment>.*)", "/chat/${segment}").filter(filterFactory.apply(filterConfig)))
                        .uri("lb://CHAT-SERVICE"))

                .route("device-service", r -> r.path("/api/v1/device/**")
                        .filters(f -> f.rewritePath("/api/v1/device/(?<segment>.*)", "/device/${segment}").filter(filterFactory.apply(filterConfig)))
                        .uri("lb://DEVICE-SERVICE"))

                .route("practice-service", r -> r.path("/api/v1/practice/**")
                        .filters(f -> f.rewritePath("/api/v1/practice/(?<segment>.*)", "/practice/${segment}").filter(filterFactory.apply(filterConfig)))
                        .uri("lb://PRACTICE-SERVICE"))

                .route("notification-service", r -> r.path("/api/v1/notification/**")
                        .filters(f -> f.rewritePath("/api/v1/notification/(?<segment>.*)", "/notification/${segment}").filter(filterFactory.apply(filterConfig)))
                        .uri("lb://NOTIFICATION-SERVICE"))

                .route("report-service", r -> r.path("/api/v1/report/**")
                        .filters(f -> f.rewritePath("/api/v1/report/(?<segment>.*)", "/report/${segment}").filter(filterFactory.apply(filterConfig)))
                        .uri("lb://REPORT-SERVICE"))

                .route("task-service", r -> r.path("/api/v1/task/**")
                        .filters(f -> f.rewritePath("/api/v1/task/(?<segment>.*)", "/task/${segment}").filter(filterFactory.apply(filterConfig)))
                        .uri("lb://TASK-SERVICE"))

                .route("user-service", r -> r.path("/api/v1/user/**")
                        .filters(f -> f.rewritePath("/api/v1/user/(?<segment>.*)", "/user/${segment}").filter(filterFactory.apply(filterConfig)))
                        .uri("lb://USER-SERVICE"))

                .route("storage-service", r -> r.path("/api/v1/storage/**")
                        .filters(f -> f.rewritePath("/api/v1/storage/(?<segment>.*)", "/storage/${segment}").filter(filterFactory.apply(filterConfig)))
                        .uri("lb://STORAGE-SERVICE"))

                .route("mqtt-service", r -> r.path("/api/v1/mqtt/**")
                        .filters(f -> f.rewritePath("/api/v1/mqtt/(?<segment>.*)", "/mqtt/${segment}"))
                        .uri("lb://MQTT-SERVICE"))

                .build();
    }

}

