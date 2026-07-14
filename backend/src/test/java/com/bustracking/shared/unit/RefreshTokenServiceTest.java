package com.bustracking.shared.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.shared.infrastructure.service.RefreshTokenService;
import com.bustracking.tracking.application.usecase.ConfirmStopUseCase;
import com.bustracking.tracking.domain.contract.BusExistsById;
import com.bustracking.tracking.domain.contract.ConfirmStop;
import com.bustracking.tracking.domain.contract.GetTripDetail;
import com.bustracking.tracking.domain.model.TripDetailView;
import com.fasterxml.jackson.databind.ObjectMapper;

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

}
