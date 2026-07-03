package com.bustracking.shared.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for JWT settings from application.properties.
 * This class main objective is to provide a centralized configuration for JWT-related properties, such as the secret key and token expiration times.
 * JwtProperties
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        long accessTokenExpiration,
        long refreshTokenExpiration
) {}

// As a record, JwtProperties is immutable