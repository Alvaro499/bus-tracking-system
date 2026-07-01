package com.bustracking.tracking.integration.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bustracking.shared.testinfrastructure.ControllerIntegrationTest;
import com.bustracking.tracking.application.usecase.ConfirmStopUseCase;
import com.bustracking.tracking.application.usecase.StartTripUseCase;
import com.bustracking.tracking.domain.model.TripDetailView;
import com.bustracking.tracking.infrastructure.mappers.TripDetailMapper;
import com.bustracking.tracking.infrastructure.web.controller.DriverTripCommandController;
import com.bustracking.tracking.infrastructure.web.dto.response.TripDetailResponse;
import com.bustracking.tracking.infrastructure.web.dto.response.TripResponse;

@WebMvcTest(DriverTripCommandController.class)
class DriverTripCommandControllerTest extends ControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StartTripUseCase startTripUseCase;

    @MockitoBean
    private ConfirmStopUseCase confirmStopUseCase;

    @MockitoBean
    private TripDetailMapper tripDetailMapper;

    // Shared Test Data
    private static final UUID VALID_TRIP_ID = UUID.fromString("b70e8400-e29b-41d4-a716-446655440001");
    private static final UUID VALID_ROUTE_STOP_ID = UUID.fromString("c80e8400-e29b-41d4-a716-446655440001");

    // =========================================================
    // POST /tracking/trips/{tripId}/start
    // =========================================================
    @Nested
    class StartTrip {

        // =========================================================
        // POST /tracking/trips/{tripId}/start - Happy Path
        // =========================================================

        @Test
        public void shouldReturn204WhenTripIsStartedSuccessfully() throws Exception {
            // Arrange
            doNothing().when(startTripUseCase).execute(eq(VALID_TRIP_ID), any());

            // Act & Assert
            mockMvc.perform(post("/tracking/trips/{tripId}/start", VALID_TRIP_ID))
                    .andExpect(status().isNoContent());

            verify(startTripUseCase).execute(eq(VALID_TRIP_ID), any());
        }

        // =========================================================
        // POST /tracking/trips/{tripId}/start - Invalid UUID
        // =========================================================

        @Test
        public void shouldReturn400WhenTripIdIsNotValidUUID() throws Exception {
            mockMvc.perform(post("/tracking/trips/{tripId}/start", "not-a-valid-uuid"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(startTripUseCase);
        }
    }

    // =========================================================
    // POST /tracking/trips/{tripId}/stops/{routeStopId}/confirm
    // =========================================================
    @Nested
    class ConfirmStop {

        // =========================================================
        // POST /tracking/trips/{tripId}/stops/{routeStopId}/confirm - Happy Path
        // =========================================================

        @Test
        public void shouldReturn200AndTripDetail_WhenStopIsConfirmed() throws Exception {
            // Arrange
            TripDetailView mockView = mock(TripDetailView.class);
            when(confirmStopUseCase.execute(eq(VALID_TRIP_ID), eq(VALID_ROUTE_STOP_ID), any()))
                    .thenReturn(mockView);

            TripDetailResponse mockResponse = new TripDetailResponse(
                    new TripResponse(VALID_TRIP_ID, "Ruta 300", "San José", "Cartago",
                            LocalTime.of(8, 0), "IN_PROGRESS"),
                    List.of());
            when(tripDetailMapper.toResponse(mockView)).thenReturn(mockResponse);

            // Act & Assert
            mockMvc.perform(post("/tracking/trips/{tripId}/stops/{routeStopId}/confirm",
                            VALID_TRIP_ID, VALID_ROUTE_STOP_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.trip.id").value(VALID_TRIP_ID.toString()))
                    .andExpect(jsonPath("$.trip.routeName").value("Ruta 300"))
                    .andExpect(jsonPath("$.trip.departureTime").value("08:00:00"))
                    .andExpect(jsonPath("$.trip.status").value("IN_PROGRESS"))
                    .andExpect(jsonPath("$.stops").isArray());

            verify(confirmStopUseCase).execute(eq(VALID_TRIP_ID), eq(VALID_ROUTE_STOP_ID), any());
            verify(tripDetailMapper).toResponse(mockView);
        }

        // =========================================================
        // POST /tracking/trips/{tripId}/stops/{routeStopId}/confirm - Invalid UUIDs
        // =========================================================

        @Test
        public void shouldReturn400_WhenTripIdIsNotValidUUID() throws Exception {
            mockMvc.perform(post("/tracking/trips/{tripId}/stops/{routeStopId}/confirm",
                            "not-a-valid-uuid", VALID_ROUTE_STOP_ID))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(confirmStopUseCase);
        }
    }
}