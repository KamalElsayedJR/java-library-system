package com.flairs.libraryapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI libraryApiInfo() {
        return new OpenAPI().info(new Info()
                .title("Library API")
                .version("v3")
                .description("Session 11 Lab: the Library API on PostgreSQL with JPA and Flyway"));
    }
}
