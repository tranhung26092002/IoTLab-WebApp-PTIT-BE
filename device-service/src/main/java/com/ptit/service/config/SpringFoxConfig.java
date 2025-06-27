package com.ptit.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableSwagger2
@EnableWebMvc
public class SpringFoxConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build()
//                .securityContexts(Arrays.asList(securityContext()))
//                .securitySchemes(Arrays.asList(apiKey()))
                .tags(
                        new Tag("Device Management", "APIs quản lý thiết bị IoT và thông tin thiết bị"),
                        new Tag("IoT Device", "APIs quản lý thiết bị IoT thông minh và kích hoạt"),
                        new Tag("Borrow Record", "APIs quản lý mượn trả thiết bị"),
                        new Tag("IoT Realtime", "APIs dữ liệu thời gian thực từ thiết bị IoT")
                );
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Device Service API")
                .description("API quản lý thiết bị IoT, mượn trả và dữ liệu thời gian thực cho hệ thống IoT Lab PTIT")
                .version("1.0.0")
                .contact(new Contact("PTIT IoT Lab", "https://openlab.com.vn", "tranvanhung26092002@gmail.com"))
                .license("MIT License")
                .licenseUrl("https://opensource.org/licenses/MIT")
                .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
    }
}
