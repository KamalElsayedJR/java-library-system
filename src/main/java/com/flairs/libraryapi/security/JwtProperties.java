package com.flairs.libraryapi.security;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

// Bound from app.jwt.* in application.yml. The secret comes from the JWT_SECRET environment variable.
@ConfigurationProperties("app.jwt")
public record JwtProperties(String secret, String issuer, Duration accessTokenTtl) {

    public JwtProperties {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("app.jwt.secret (JWT_SECRET) must be at least 32 bytes for HS256");
        }
    }
}
