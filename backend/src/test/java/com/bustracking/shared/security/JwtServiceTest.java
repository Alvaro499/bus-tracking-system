package com.bustracking.shared.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import com.bustracking.shared.domain.RoleAuth;
import com.bustracking.shared.infrastructure.config.JwtProperties;
import com.bustracking.shared.infrastructure.service.JwtService;

/**
 * Unit test for {@link JwtService}.
 *
 * Scope: verifies the real cryptographic logic of token generation and
 * validation (signing, parsing, expiration, signature verification).
 *
 * No mocks are used here. JwtService has no external dependencies to isolate
 * from — its own logic IS what this test verifies, so real instances with
 * real (test-only) secret keys are used throughout.
 *
 * If you need to change how tokens are signed, parsed, or validated,
 * this is the class that should catch a regression.
 *
 * Does NOT test: how the token is extracted from a request, how it's used
 * to build a Spring Authentication object, or authorization rules.
 * See {@link JwtAuthenticationFilterTest} and SecurityConfigTest for those.
 */

public class JwtServiceTest {

    private JwtService jwtService;

    private static final UUID BUS_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");

    @BeforeEach
    void setUp() {

        // We fill JwtProperties instance with mock data similar from our
        // application-test.properties
        JwtProperties testProperties = new JwtProperties(
                "test-secret-key-must-be-at-least-32-bytes-long!!",
                3600000,
                3000000);
        jwtService = new JwtService(testProperties);
    }

    @Test
    public void shouldGenerateValidAccessToken() {

        String accessToken = jwtService.generateAccessToken(BUS_ID, RoleAuth.DRIVER);
        assertEquals(BUS_ID, jwtService.extractBusId(accessToken));

    }

    @Test
    public void shouldExtractCorrectRoleClaim() {
        String accessToken = jwtService.generateAccessToken(BUS_ID, RoleAuth.DRIVER);

        String role = jwtService.extractClaim(accessToken, claims -> claims.get("role", String.class));
        assertEquals("DRIVER", role);
    }

    @Test
    public void shouldReturnValid_WhenTokenIsFreshlyGenerated() {
        String token = jwtService.generateAccessToken(BUS_ID, RoleAuth.DRIVER);

        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    public void shouldReturnInvalid_WhenTokenIsExpired() {
        JwtProperties expiredProperties = new JwtProperties("test-secret-key-must-be-at-least-32-bytes-long!!", -1000L,-1000L);
        JwtService expiredService = new JwtService(expiredProperties);
        String expiredToken = expiredService.generateAccessToken(BUS_ID, RoleAuth.DRIVER);

        assertFalse(expiredService.isTokenValid(expiredToken));
    }

    @Test
    public void shouldReturnInvalid_WhenTokenIsMalformed() {
        assertFalse(jwtService.isTokenValid("esto.no.es.un.jwt"));
    }

    @Test
    public void shouldReturnInvalid_WhenSignatureDoesNotMatch() {
        JwtProperties otherSecretProperties = new JwtProperties("otra-clave-completamente-diferente-32bytes!", 3600000L,
                3600000L);
        JwtService otherService = new JwtService(otherSecretProperties);
        String tokenSignedWithOtherKey = otherService.generateAccessToken(BUS_ID, RoleAuth.DRIVER);

        assertFalse(jwtService.isTokenValid(tokenSignedWithOtherKey));
    }
}
