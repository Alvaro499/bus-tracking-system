package com.bustracking.tracking.unit.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bustracking.shared.application.dto.TokensDTO;
import com.bustracking.shared.domain.RoleAuth;
import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.infrastructure.service.JwtService;
import com.bustracking.shared.infrastructure.service.RefreshTokenService;
import com.bustracking.tracking.application.usecase.AuthenticateBusUseCase;
import com.bustracking.tracking.domain.model.BusCredential;
import com.bustracking.tracking.domain.repository.BusCredentialRepository;

@ExtendWith(MockitoExtension.class)
public class AuthenticateBusUseCaseTest {

        @Mock
        private BusCredentialRepository credentialRepositoryMock;

        @Mock
        private PasswordEncoder passwordEncoderMock;

        @Mock
        private JwtService jwtServiceMock;

        @Mock
        private RefreshTokenService refreshTokenServiceMock;

        private AuthenticateBusUseCase authenticateBusUseCase;

        // Test Common Data
        private final UUID VALID_BUS_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");
        private final String VALID_PASSWORD = "randomPassWord";
        private final String VALID_HASH = "hashed_password_here";

        private final BusCredential activeCredential = new BusCredential(
                        UUID.randomUUID(),
                        VALID_BUS_ID,
                        VALID_HASH);

        private BusCredential revokedCredential = new BusCredential(
                        UUID.randomUUID(),
                        VALID_BUS_ID,
                        VALID_HASH);

        @BeforeEach
        void setUp() {

                authenticateBusUseCase = new AuthenticateBusUseCase(
                                credentialRepositoryMock,
                                passwordEncoderMock,
                                jwtServiceMock,
                                refreshTokenServiceMock);

                revokedCredential.revokeCredentials();
        }

        // =========================================================
        // Happy Path — Valid Credentials
        // =========================================================

        @Test
        void shouldReturnTokensDTO_WhenCredentialsAreValid() {

                // Arrange
                when(credentialRepositoryMock.findByBusId(VALID_BUS_ID))
                                .thenReturn(Optional.of(activeCredential));
                when(passwordEncoderMock.matches(VALID_PASSWORD, VALID_HASH))
                                .thenReturn(true);
                when(jwtServiceMock.generateAccessToken(eq(VALID_BUS_ID), any()))
                                .thenReturn("fake_access_token");

                // Act
                TokensDTO result = authenticateBusUseCase.execute(VALID_BUS_ID, VALID_PASSWORD);

                // Asserts
                assertNotNull(result);
                assertEquals("fake_access_token", result.accessToken());
                assertNotNull(result.refreshToken());
                assertFalse(result.refreshToken().isEmpty());

                verify(credentialRepositoryMock, times(1)).findByBusId(VALID_BUS_ID);
                verify(passwordEncoderMock, times(1)).matches(VALID_PASSWORD, VALID_HASH);
                verify(jwtServiceMock, times(1)).generateAccessToken(VALID_BUS_ID, RoleAuth.DRIVER);
                verify(refreshTokenServiceMock, times(1)).saveRefreshToken(eq(VALID_BUS_ID), anyString(), anyString());
        }

        // =========================================================
        // Credentials Not Found (bus inexistente)
        // =========================================================

        @Test
        void shouldThrowBusinessRuleException_WhenCredentialsEmpty() {

                // Arrange
                when(credentialRepositoryMock.findByBusId(VALID_BUS_ID))
                                .thenReturn(Optional.empty());

                // Act
                BusinessRuleException exception = assertThrows(
                                BusinessRuleException.class,
                                () -> authenticateBusUseCase.execute(VALID_BUS_ID, VALID_PASSWORD));

                // Asserts
                assertEquals(ErrorCode.INVALID_CREDENTIALS, exception.getErrorCode());

                verify(credentialRepositoryMock, times(1)).findByBusId(VALID_BUS_ID);
                verifyNoInteractions(passwordEncoderMock);
                verifyNoInteractions(jwtServiceMock);
                verifyNoInteractions(refreshTokenServiceMock);
        }

        // =========================================================
        // Credential Revoked
        // =========================================================

        @Test
        void shouldThrowBusinessRuleException_WhenCredentialIsRevoked() {

                // Arrange
                when(credentialRepositoryMock.findByBusId(VALID_BUS_ID))
                                .thenReturn(Optional.of(revokedCredential));

                // Act
                BusinessRuleException exception = assertThrows(
                                BusinessRuleException.class,
                                () -> authenticateBusUseCase.execute(VALID_BUS_ID, VALID_PASSWORD));

                // Asserts
                assertEquals(ErrorCode.INVALID_CREDENTIALS, exception.getErrorCode());

                verify(credentialRepositoryMock, times(1)).findByBusId(VALID_BUS_ID);
                verifyNoInteractions(passwordEncoderMock);
                verifyNoInteractions(jwtServiceMock);
                verifyNoInteractions(refreshTokenServiceMock);
        }

        // =========================================================
        // Invalid Password
        // =========================================================

        @Test
        void shouldThrowBusinessRuleException_WhenPasswordNoMatching() {

                // Arrange
                when(credentialRepositoryMock.findByBusId(VALID_BUS_ID))
                                .thenReturn(Optional.of(activeCredential));
                when(passwordEncoderMock.matches(VALID_PASSWORD, VALID_HASH))
                                .thenReturn(false);

                // Act
                BusinessRuleException exception = assertThrows(
                                BusinessRuleException.class,
                                () -> authenticateBusUseCase.execute(VALID_BUS_ID, VALID_PASSWORD));

                // Asserts
                assertEquals(ErrorCode.INVALID_CREDENTIALS, exception.getErrorCode());

                verify(credentialRepositoryMock, times(1)).findByBusId(VALID_BUS_ID);
                verify(passwordEncoderMock, times(1)).matches(VALID_PASSWORD, VALID_HASH);
                verifyNoInteractions(jwtServiceMock);
                verifyNoInteractions(refreshTokenServiceMock);
        }
}