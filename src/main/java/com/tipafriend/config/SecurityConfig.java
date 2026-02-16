package com.tipafriend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        try {
            http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for API
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/health", "/actuator/**").permitAll() // Allow health checks
                    .anyRequest().authenticated() // Require auth for everything else
                );

            return http.build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure security", e);
        }
    }
}


