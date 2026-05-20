package com.bustracking.tracking.integration.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
//https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/test/context/bean/override/mockito/MockitoBean.html
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.bustracking.shared.testinfrastructure.ControllerIntegrationTest;
import com.bustracking.shared.valueobjects.GpsCoordinate;
import com.bustracking.tracking.application.usecase.GetBusLocationUseCase;
import com.bustracking.tracking.application.usecase.UpdateBusLocationUseCase;
import com.bustracking.tracking.domain.model.BusLocation;
import com.bustracking.tracking.infrastructure.web.controller.BusLocationController;

@WebMvcTest(BusLocationController.class)
class BusLocationControllerTest extends ControllerIntegrationTest {

    // In order to test the controller in isolation, we will mock the use cases it depends on.
    @Autowired
    private MockMvc mockMvc;

    //To convert objects to JSON and vice versa, this is the objectMapper Spring provides by default.
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private GetBusLocationUseCase getBusLocationUseCase;

    @MockitoBean
    private UpdateBusLocationUseCase updateBusLocationUseCase;

    // Test data
    private final UUID validBusId = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");
    private final BigDecimal validLat = new BigDecimal("9.934739");
    private final BigDecimal validLng = new BigDecimal("-84.087502");
    private final LocalDateTime validTimestamp = LocalDateTime.of(2025, 1, 1, 12, 0, 0);

    private final BusLocation validBusLocation = new BusLocation(
        validBusId,
        new GpsCoordinate(validLat, validLng),
        validTimestamp
    );

    // =========================================================
    // GET /tracking/buses/{busId}/location - Happy Path
    // =========================================================

    @Test
    void shouldReturnBusLocationWhenBusExists() throws Exception {
        // Arrange
        when(getBusLocationUseCase.execute(validBusId))
            .thenReturn(validBusLocation);

        // Act & Assert
        mockMvc.perform(get("/tracking/buses/{busId}/location", validBusId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.busId").value(validBusId.toString()))
            .andExpect(jsonPath("$.lat").value(9.934739))
            .andExpect(jsonPath("$.lng").value(-84.087502))
            .andExpect(jsonPath("$.updatedAt").exists())
            .andExpect(jsonPath("$.updatedAt").value("2025-01-01T12:00:00"));
    }


    // =========================================================
    // GET /tracking/buses/{busId}/location - Spring Validation
    // =========================================================

    @Test
    void shouldReturn400WhenBusIdIsNotValidUUID() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/tracking/buses/{busId}/location", "not-a-valid-uuid"))
            .andExpect(status().isBadRequest());
    }

    // Note: NotFoundException → 404 is tested in TrackingExceptionHandlerTest
    // This keeps the contract centralized and avoids duplication

    // =========================================================
    // POST /tracking/buses/{busId}/location - Happy Path
    // =========================================================

    @Test
    void shouldUpdateBusLocationWhenValidDataProvided() throws Exception {
        // Arrange
        String requestBody = objectMapper.writeValueAsString(
            new Object() {
                public final BigDecimal lat = new BigDecimal("9.934739");
                public final BigDecimal lng = new BigDecimal("-84.087502");
            }
        );

        //only for void methods, for methods that return something, we use when().thenReturn()
        doNothing().when(updateBusLocationUseCase)
            .execute(eq(validBusId), any(), any());

        // Act & Assert
        mockMvc.perform(post("/tracking/buses/{busId}/location", validBusId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk());

        // only when we want to verify that the method was called.
        verify(updateBusLocationUseCase, times(1))
            .execute(eq(validBusId), any(), any());
    }


    // =========================================================
    // POST /tracking/buses/{busId}/location - Spring Validation
    // =========================================================

    @Test
    void shouldReturn400WhenPostingWithInvalidUUID() throws Exception {
        // Arrange
        String requestBody = objectMapper.writeValueAsString(
            new Object() {
                public final BigDecimal lat = new BigDecimal("9.934739");
                public final BigDecimal lng = new BigDecimal("-84.087502");
            }
        );

        // Act & Assert
        mockMvc.perform(post("/tracking/buses/{busId}/location", "invalid-uuid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest());

        verify(updateBusLocationUseCase, never())
            .execute(any(), any(), any());
    }

    
    @Test
    void shouldReturn400WhenPostingWithoutContentType() throws Exception {
        // Arrange
        String requestBody = objectMapper.writeValueAsString(
            new Object() {
                public final BigDecimal lat = new BigDecimal("9.934739");
                public final BigDecimal lng = new BigDecimal("-84.087502");
            }
        );

        // Act & Assert
        mockMvc.perform(post("/tracking/buses/{busId}/location", validBusId)
                .content(requestBody))
            .andExpect(status().isBadRequest());

        verify(updateBusLocationUseCase, never())
            .execute(any(), any(), any());
    }

    // Note: NotFoundException → 404 is tested in TrackingExceptionHandlerTest
    // This keeps the contract centralized and avoids duplication
}