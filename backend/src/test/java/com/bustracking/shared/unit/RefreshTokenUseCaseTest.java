package com.bustracking.shared.unit;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bustracking.shared.application.RefreshTokenUseCase;
import com.bustracking.shared.application.dto.TokensDTO;
import com.bustracking.shared.domain.RoleAuth;
import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.infrastructure.service.JwtService;
import com.bustracking.shared.infrastructure.service.RefreshTokenService;
import com.bustracking.shared.infrastructure.service.RefreshTokenService.RefreshTokenResult;


@ExtendWith(MockitoExtension.class)
public class RefreshTokenUseCaseTest {

    // Mocks
    @Mock
    private RefreshTokenService refreshTokenServiceMock;

    @Mock
    private JwtService jwtServiceMock;

    // Real instances
    private RefreshTokenUseCase refreshTokenUseCase;

    // Test common data
    private static final UUID BUS_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");
    private static final String VALID_REFRESH_TOKEN = "old_refresh_token";
    private static final String NEW_RAW_TOKEN = "new_raw_token";
    private static final String NEW_ACCESS_TOKEN = "new_access_token";

    @BeforeEach
    void setUp() {
        refreshTokenUseCase = new RefreshTokenUseCase(refreshTokenServiceMock, jwtServiceMock);
    }

    // =========================================================
    // Happy Path Test - Valid Refresh Token
    // =========================================================
    @Test
    void shouldReturnNewTokens_WhenRefreshTokenIsValid() {
        // Arrange
        RefreshTokenResult refreshTokenResult = new RefreshTokenResult(BUS_ID, "DRIVER", NEW_RAW_TOKEN);
        when(refreshTokenServiceMock.validateAndRotateRefreshToken(VALID_REFRESH_TOKEN))
                .thenReturn(refreshTokenResult);
        when(jwtServiceMock.generateAccessToken(BUS_ID, RoleAuth.DRIVER))
                .thenReturn(NEW_ACCESS_TOKEN);

        // Act
        TokensDTO result = refreshTokenUseCase.execute(VALID_REFRESH_TOKEN);

        // Assert
        assertNotNull(result);
        assertEquals(NEW_ACCESS_TOKEN, result.accessToken());
        assertEquals(NEW_RAW_TOKEN, result.refreshToken());

        // Verify interactions
        verify(refreshTokenServiceMock).validateAndRotateRefreshToken(VALID_REFRESH_TOKEN);
        verify(jwtServiceMock).generateAccessToken(BUS_ID, RoleAuth.DRIVER);
    }

    // =========================================================
    // Propagate BusinessRuleException from RefreshTokenService
    // =========================================================
    @Test
    void shouldPropagateBusinessRuleException_WhenRefreshTokenIsInvalid() {
        // Arrange
        doThrow(new BusinessRuleException(
                ErrorCode.UNAUTHORIZED,
                "Refresh token invalid or expired"))
                .when(refreshTokenServiceMock).validateAndRotateRefreshToken(VALID_REFRESH_TOKEN);

        // Act & Assert
        assertThrows(
                BusinessRuleException.class,
                () -> refreshTokenUseCase.execute(VALID_REFRESH_TOKEN));

        // Verify that JwtService was never called
        verify(refreshTokenServiceMock).validateAndRotateRefreshToken(VALID_REFRESH_TOKEN);
        verifyNoInteractions(jwtServiceMock);
    }
}

// falta este, y
// auhtenticatebuse use case test
// y flow test
// y refresh token service
// no se necesita un repository para redis