package com.bustracking.tracking.integration.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bustracking.shared.testinfrastructure.ControllerIntegrationTest;
import com.bustracking.tracking.application.usecase.GetTodayPlannedTripsUseCase;
import com.bustracking.tracking.application.usecase.GetTripDetailUseCase;
import com.bustracking.tracking.domain.model.TripView;
import com.bustracking.tracking.infrastructure.mappers.TripDetailMapper;
import com.bustracking.tracking.infrastructure.web.controller.DriverTripQueryController;

@WebMvcTest(DriverTripQueryController.class)
class DriverTripQueryControllerTest extends ControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetTodayPlannedTripsUseCase getTodayPlannedTripsUseCase;

    @MockitoBean
    private GetTripDetailUseCase getTripDetailUseCase;

    @MockitoBean
    private TripDetailMapper tripDetailMapper;

    // Test data
    private final TripView plannedTrip = new TripView(
        UUID.randomUUID(),
        "Ruta 300",
        "San José",
        "Cartago",
        LocalTime.of(5, 45),
        "PLANNED"
    );

    // =========================================================
    // GET /tracking/trips/today - Happy Path
    // =========================================================

    @Test
    void shouldReturnTodayTripsWhenBusHasPlannedTrips() throws Exception {
        // Arrange
        when(getTodayPlannedTripsUseCase.execute(any()))
            .thenReturn(List.of(plannedTrip));

        // Act & Assert
        mockMvc.perform(get("/tracking/trips/today"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].routeName").value("Ruta 300"))
            .andExpect(jsonPath("$[0].origin").value("San José"))
            .andExpect(jsonPath("$[0].destination").value("Cartago"))
            .andExpect(jsonPath("$[0].departureTime").value("05:45:00"))
            .andExpect(jsonPath("$[0].status").value("PLANNED"));
    }

    @Test
    void shouldReturnEmptyListWhenBusHasNoTripsToday() throws Exception {
        // Arrange
        when(getTodayPlannedTripsUseCase.execute(any()))
            .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/tracking/trips/today"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    // =========================================================
    // POST /tracking/trips/{tripId}/start — SCRUM-51 (pendiente)
    // =========================================================

    // =========================================================
    // POST /tracking/trips/{tripId}/cancel — SCRUM-55 (pendiente)
    // =========================================================
}