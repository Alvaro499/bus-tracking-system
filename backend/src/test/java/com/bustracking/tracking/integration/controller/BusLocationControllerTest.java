package com.bustracking.tracking.integration.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

//https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/test/context/bean/override/mockito/MockitoBean.html
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.shared.testinfrastructure.ControllerIntegrationTest;
import com.bustracking.shared.valueobjects.GpsCoordinate;
import com.bustracking.tracking.application.usecase.GetBusLocationUseCase;
import com.bustracking.tracking.application.usecase.UpdateBusLocationUseCase;
import com.bustracking.tracking.domain.model.BusLocation;

class BusLocationControllerTest extends ControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
            .andExpect(jsonPath("$.lng").value(-84.087502));
    }


    // =========================================================
    // GET /tracking/buses/{busId}/location - Error Cases
    // =========================================================

    @Test
    void shouldReturn404WhenBusDoesNotExist() throws Exception {
        // Arrange
        when(getBusLocationUseCase.execute(validBusId))
            .thenThrow(new NotFoundException(
                ErrorCode.BUS_NOT_FOUND,
                "Bus not found",
                "Bus with ID " + validBusId + " does not exist"
            ));

        // Act & Assert
        mockMvc.perform(get("/tracking/buses/{busId}/location", validBusId))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn404WhenBusHasNoLocation() throws Exception {
        // Arrange
        when(getBusLocationUseCase.execute(validBusId))
            .thenThrow(new NotFoundException(
                ErrorCode.BUS_LOCATION_NOT_FOUND,
                "Bus location not found",
                "Bus with ID " + validBusId + " has no location registered yet"
            ));

        // Act & Assert
        mockMvc.perform(get("/tracking/buses/{busId}/location", validBusId))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenBusIdIsNotValidUUID() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/tracking/buses/{busId}/location", "not-a-valid-uuid"))
            .andExpect(status().isBadRequest());
    }


}