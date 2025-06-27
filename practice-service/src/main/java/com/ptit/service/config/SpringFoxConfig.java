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
                        new Tag("Practice Management", "APIs quản lý bài thực hành, video, file và hướng dẫn"),
                        new Tag("Exam Management", "APIs quản lý bài thi và cấu hình thi"),
                        new Tag("Question Management", "APIs quản lý câu hỏi trắc nghiệm và tự luận"),
                        new Tag("Student Exam", "APIs quản lý bài thi của sinh viên, nộp bài và chấm điểm"),
                        new Tag("Student Progress", "APIs theo dõi tiến độ học tập của sinh viên"),
                        new Tag("Report Management", "APIs quản lý báo cáo thực hành và đánh giá")
                );
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Practice Service API")
                .description("API quản lý thực hành, bài thi, câu hỏi và tiến độ học tập cho hệ thống IoT Lab PTIT")
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
