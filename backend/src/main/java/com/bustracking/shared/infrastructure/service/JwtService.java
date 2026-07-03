package com.bustracking.shared.infrastructure.service;

import com.bustracking.shared.infrastructure.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;

    public JwtService(JwtProperties properties) {
        
        this.secretKey = Keys.hmacShaKeyFor(
                properties.secret().getBytes(StandardCharsets.UTF_8)
        );
        this.accessTokenExpiration = properties.accessTokenExpiration();
    }

    public String generateAccessToken(UUID busId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(busId.toString())
                .claim("role", "DRIVER")
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public UUID extractBusId(String token) {
        return UUID.fromString(extractClaim(token, claim -> claim.getSubject()));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, claim -> claim.getExpiration());
    }

    /**
     * Extract a specific claim from the JWT token
     *
     * @param token          the JWT token to parse
     * @param claimsResolver a function to extract the desired claim from the claims
     * @param <T>            the type of the claim to extract
     * @return the extracted claim
     * 
     * @visualization:
     *      Claims claims = parseToken(token);
     *      String subject = claims.getSubject();
     *      return subject;
     */

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = parseToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Read and verify the JWT token
     *
     * @param token the JWT token to parse
     * @return the claims contained in the token (expiration, subject, etc.)
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}