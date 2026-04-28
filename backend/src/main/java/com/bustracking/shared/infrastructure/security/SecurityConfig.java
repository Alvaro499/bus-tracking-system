package com.bustracking.shared.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

/**
 * Security Configuration — TEMPORARY
 *
 * All endpoints are public for now. Authentication (JWT) will be added in HU-09.
 * When implemented, replace permitAll() with role-based rules per endpoint.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /*
     * Temporary CORS configuration for development.
     * Update allowed origins when deploying to production.
     */

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(CsrfConfigurer::disable)  // Disable CSRF for API endpoints
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()  // Allow all requests without authentication
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Development origins
        config.setAllowedOrigins(List.of(
            "http://localhost:5173",  // passengers-app (Vite)
            "http://localhost:3000"   // management-app (Next.js) — future
        ));
        
        // Production origins — uncomment and update when deploying
        // config.setAllowedOrigins(List.of(
        //     "https://passenger-app.vercel.app",
        //     "https://management-app.vercel.app"
        // ));
        
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
