package com.bustracking.shared.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.infrastructure.service.RefreshTokenService;
import com.bustracking.shared.infrastructure.service.RefreshTokenService.RefreshTokenResult;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private StringRedisTemplate redisTemplateMock;

    @Mock
    private ValueOperations<String, String> valueOperationsMock;

    private RefreshTokenService refreshTokenService;

    // Test common data
    private static final UUID USER_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");
    private static final String ROLE = "DRIVER";
    private static final String RAW_TOKEN = "raw_refresh_token";

    @BeforeEach
    void setUp() {
        // ValueOperations is the object that manages key-value operations in Reddis
        when(redisTemplateMock.opsForValue()).thenReturn(valueOperationsMock);

        // 7 days TTL, same as original config (604800000 ms)
        refreshTokenService = new RefreshTokenService(redisTemplateMock, 604800000L);
    }

    // =========================================================
    // Happy Path — Token saved correctly
    // =========================================================

    @Test
    void shouldSaveRefreshTokenSuccessfully_WhenAllInputsAreValid() {
        // Act
        refreshTokenService.saveRefreshToken(USER_ID, ROLE, RAW_TOKEN);

        // Assert: we verify Redis was called with corrects parameters
        verify(valueOperationsMock, times(1)).set(
                startsWith("rt:"), // Does key start with prefix rt?
                contains("\"ACTIVE\""), // Does JSON contains ACTIVE state
                eq(Duration.ofMillis(604800000L)) // Valid TTL
        );
    }

    @Test
    void shouldSaveTokenDataWithCorrectJsonStructure() {
        // Act
        refreshTokenService.saveRefreshToken(USER_ID, ROLE, RAW_TOKEN);

        // Assert: verificar que el JSON contiene los campos esperados
        verify(valueOperationsMock).set(
                startsWith("rt:"),
                argThat(json -> json.contains("\"userId\":\"650e8400-e29b-41d4-a716-446655440001\"") &&
                        json.contains("\"role\":\"DRIVER\"") &&
                        json.contains("\"status\":\"ACTIVE\"") &&
                        json.contains("\"issuedAt\":\"")),
                any());
    }

    // =========================================================
    // generateRawToken() — Not tested directly
    // =========================================================

    /**
     * generateRawToken() is a private helper that only uses SecureRandom
     * and Base64. It contains no business logic worth testing directly,
     * its correctness is already verified indirectly through any test
     * that calls saveRefreshToken or validateAndRotateRefreshToken.
     */

    // =========================================================
    // hashToken() — Not tested directly
    // =========================================================

    /**
     * hashToken() is a private helper that only applies SHA-256 and
     * Base64 encoding. It contains no business logic worth testing
     * directly, its correctness is already verified indirectly
     * through any test that calls saveRefreshToken or
     * validateAndRotateRefreshToken.
     */

    // =========================================================
    // Happy Path — Valid token, rotation succeeds
    // =========================================================

    /**
     * Verifies that when a valid, active refresh token is presented,
     * the old token is marked as USED, a new token is saved, and
     * the returned RefreshTokenResult contains the correct userId,
     * role, and a new raw token.
     */
    @Test
    void shouldRotateTokenAndReturnNewToken_WhenTokenIsValid() {
        // Arrange: simulate Redis returning an ACTIVE token
        String activeJson = """
                {"userId":"%s","role":"%s","issuedAt":"2025-01-01T00:00:00Z","status":"ACTIVE"}
                """.formatted(USER_ID, ROLE);

        when(valueOperationsMock.get(anyString())).thenReturn(activeJson);

        // Act
        RefreshTokenResult result = refreshTokenService.validateAndRotateRefreshToken(RAW_TOKEN);

        // Assert
        assertEquals(USER_ID, result.busId());
        assertEquals(ROLE, result.role());
        assertNotNull(result.newRawToken());
        assertNotEquals(RAW_TOKEN, result.newRawToken()); // must be a new token

        // Assert: Redis interactions
        verify(valueOperationsMock, times(1)).get(anyString()); // 1 GET
        verify(valueOperationsMock, times(1)).set(anyString(), contains("\"USED\""), any()); // mark old as USED
        verify(valueOperationsMock, times(1)).set(anyString(), contains("\"ACTIVE\""), any()); // save new token
    }

    // =========================================================
    // Token not found — Redis returns null
    // =========================================================

    /**
     * Verifies that when Redis does not contain the token (get returns null),
     * a BusinessRuleException with UNAUTHORIZED is thrown and no set operation
     * is performed.
     */
    @Test
    void shouldThrowBusinessRuleException_WhenTokenNotFound() {
        // Arrange: Redis returns null → token doesn't exist
        when(valueOperationsMock.get(anyString())).thenReturn(null);

        // Act & Assert
        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> refreshTokenService.validateAndRotateRefreshToken(RAW_TOKEN));

        assertEquals(ErrorCode.UNAUTHORIZED, exception.getErrorCode());

        // Verify Redis was only queried, never updated
        verify(valueOperationsMock, times(1)).get(anyString());
        verify(valueOperationsMock, never()).set(anyString(), anyString(), any());
    }

    // =========================================================
    // Token already used — status is not ACTIVE
    // =========================================================

    /**
     * Verifies that when the token exists but its status is not ACTIVE
     * (i.e., it was already used), a BusinessRuleException with UNAUTHORIZED
     * is thrown and no set operation is performed.
     */
    @Test
    void shouldThrowBusinessRuleException_WhenTokenAlreadyUsed() {
        // Arrange: Redis returns a token whose status is USED
        String usedJson = """
                {"userId":"%s","role":"%s","issuedAt":"2025-01-01T00:00:00Z","status":"USED"}
                """.formatted(USER_ID, ROLE);

        when(valueOperationsMock.get(anyString())).thenReturn(usedJson);

        // Act & Assert
        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> refreshTokenService.validateAndRotateRefreshToken(RAW_TOKEN));

        assertEquals(ErrorCode.UNAUTHORIZED, exception.getErrorCode());

        // Verify Redis was only queried, never updated
        verify(valueOperationsMock, times(1)).get(anyString());
        verify(valueOperationsMock, never()).set(anyString(), anyString(), any());
    }

    // =========================================================
    // Happy Path — Revoke Refresh Token
    // =========================================================

    @Test
    public void shouldRevokeRefreshToken_WhenTokenExists() {

        // Arrange
        String activeToken = """
                {"userId":"%s","role":"%s","issuedAt":"2025-01-01T00:00:00Z","status":"ACTIVE"}
                """.formatted(USER_ID, ROLE);
        when(valueOperationsMock.get(anyString())).thenReturn(activeToken);

        // Act
        refreshTokenService.revokeRefreshToken(RAW_TOKEN);

        // Assert
        verify(valueOperationsMock).set(anyString(), contains("\"REVOKED\""), any());
    }

    // =========================================================
    // Refresh Token Does Not Exist or Already Revoked
    // =========================================================

    @Test
    public void shouldNotRevokeRefreshToken_WhenTokenDoesNotExist() {
        // Arrange
        when(valueOperationsMock.get(anyString())).thenReturn(null);

        // Act
        refreshTokenService.revokeRefreshToken(RAW_TOKEN);

        // Assert
        verify(valueOperationsMock, never()).set(anyString(), anyString(), any());
    }

}
