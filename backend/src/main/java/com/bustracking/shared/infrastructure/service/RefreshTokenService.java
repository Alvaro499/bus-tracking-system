package com.bustracking.shared.infrastructure.service;

import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;


@Service
public class RefreshTokenService {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final Duration ttl;
    private static final String PREFIX = "rt:";

    public RefreshTokenService(
            StringRedisTemplate redis,
            @Value("${jwt.refresh-token-expiration}") long ttlMillis) {
        this.redis = redis;
        this.objectMapper = new ObjectMapper(); 
        this.ttl = Duration.ofMillis(ttlMillis);
    }

    /**
     * Saves a refresh token associated with a bus.
     *
     * @param busId    bus ID
     * @param rawToken unhashed token (to return to the client)
     * @return the same rawToken (for the cookie)
     */
    public void saveRefreshToken(UUID busId, String rawToken) {
        String hash = hashToken(rawToken);
        String key = PREFIX + hash;
        TokenData data = new TokenData(busId.toString(), Instant.now().toString(), "ACTIVE");
        try {
            String json = objectMapper.writeValueAsString(data);
            redis.opsForValue().set(key, json, ttl);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error during serialization token", e);
        }
    }

    /**
     * Validates a refresh token: if it exists and is active, marks it as USED
     * (rotation)
     * and generates a new one, saving it and returning the new raw token.
     *
     * @param rawToken token received in the cookie
     * @return new rawToken (rotated refresh token)
     * @throws BusinessRuleException if the token does not exist, is revoked, or was
     *                               already used
     */
    public String validateAndRotateRefreshToken(String rawToken) {
        String hash = hashToken(rawToken);
        String key = PREFIX + hash;
        String json = redis.opsForValue().get(key);
        if (json == null) {
            throw new BusinessRuleException(
                    ErrorCode.UNAUTHORIZED,
                    "Refresh token inválido o expirado");
        }
        TokenData data;
        try {
            data = objectMapper.readValue(json, TokenData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializando token", e);
        }
        if (!"ACTIVE".equals(data.status)) {
            // Reused token: revoke the entire family (security best practice)
            // For now we simply throw an exception, later we can implement family
            // revocation
            throw new BusinessRuleException(
                    ErrorCode.UNAUTHORIZED,
                    "Refresh token ya fue utilizado");
        }

        // Rotation: mark current as USED and generate a new one data.status = "USED";
        try {
            redis.opsForValue().set(key, objectMapper.writeValueAsString(data), ttl);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al actualizar token", e);
        }

        // Generate a new token (with the same busId) and save it
        UUID busId = UUID.fromString(data.busId);
        String newRawToken = generateRawToken();
        saveRefreshToken(busId, newRawToken);

        return newRawToken;
    }

    /**
     * Revokes a refresh token (marks as REVOKED).
     *
     * @param rawToken token to revoke
     */
    public void revokeRefreshToken(String rawToken) {
        String hash = hashToken(rawToken);
        String key = PREFIX + hash;
        String json = redis.opsForValue().get(key);
        if (json != null) {
            TokenData data;
            try {
                data = objectMapper.readValue(json, TokenData.class);
            } catch (JsonProcessingException e) {
                return;
            }
            data.status = "REVOKED";
            try {
                redis.opsForValue().set(key, objectMapper.writeValueAsString(data), ttl);
            } catch (JsonProcessingException ignored) {
            }
        }
        // If it does not exist, we do nothing (it already expired)
    }

    /**
     * Generates a random refresh token (32 bytes, base64url).
     */
    private String generateRawToken() {
        byte[] bytes = new byte[32];
        new java.security.SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Calculates the SHA-256 hash of a plain text token.
     */
    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    // Inner class to serialize/deserialize token data in Redis
    private static class TokenData {
        public String busId;
        public String issuedAt;
        public String status;

        public TokenData() {
        }

        public TokenData(String busId, String issuedAt, String status) {
            this.busId = busId;
            this.issuedAt = issuedAt;
            this.status = status;
        }
    }
}
