package com.flairs.libraryapi.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;     // you need this for TODO 1
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] PUBLIC = {
        "/api/auth/login", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/error"
    };

    @Bean
    SecurityFilterChain api(HttpSecurity http,
                            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC).permitAll()
                // GET on the catalogue is for anyone logged in
                .requestMatchers(HttpMethod.GET, "/api/books/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/authors/**").authenticated()
                // every other method on those paths only for the librarian
                .requestMatchers("/api/books/**", "/api/authors/**").hasRole("LIBRARIAN")
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint((req, res, ex) -> resolver.resolveException(req, res, null, ex))
                .accessDeniedHandler((req, res, ex) -> resolver.resolveException(req, res, null, ex)))
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) -> resolver.resolveException(req, res, null, ex))
                .accessDeniedHandler((req, res, ex) -> resolver.resolveException(req, res, null, ex)));
        return http.build();
    }

    // "roles": ["LIBRARIAN"] in the token becomes the authority ROLE_LIBRARIAN
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("roles");
        authorities.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
