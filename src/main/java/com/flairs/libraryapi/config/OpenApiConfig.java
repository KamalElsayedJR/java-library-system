package com.flairs.libraryapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Adds the "Authorize" button to Swagger UI: paste the accessToken from /api/auth/login
@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI libraryApiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Library API")
                        .version("v4")
                        .description("Session 12 Lab: the Library API secured with Spring Security and JWT"))
                .components(new Components().addSecuritySchemes("bearer",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearer"));
    }
}
