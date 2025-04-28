package com.bookmng.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${springdoc.title}")
    private String API_TITLE;

    @Value("${springdoc.description}")
    private String API_DECRIPTION;

    @Value("${springdoc.version}")
    private String API_VERSION;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    /**
     * swagger 정보
     * @return
     */
    private Info apiInfo() {
        return new Info()
                .title(API_TITLE) // API의 제목
                .version(API_VERSION) // API의 버전
                .description(API_DECRIPTION); // API에 대한 설명
    }
}