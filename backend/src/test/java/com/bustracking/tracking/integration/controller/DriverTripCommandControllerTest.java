package com.bustracking.tracking.integration.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.shared.testinfrastructure.ControllerIntegrationTest;
import com.bustracking.shared.testinfrastructure.WithMockDriver;
import com.bustracking.tracking.application.usecase.ConfirmStopUseCase;
import com.bustracking.tracking.application.usecase.FinishTripUseCase;
import com.bustracking.tracking.application.usecase.StartTripUseCase;
import com.bustracking.tracking.domain.model.TripDetailView;
import com.bustracking.tracking.domain.model.TripFinishView;
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
    private FinishTripUseCase finishTripUseCase;

    @MockitoBean
    private TripDetailMapper tripDetailMapper;

    // Shared Test Data
    private static final UUID VALID_TRIP_ID = UUID.fromString("b70e8400-e29b-41d4-a716-446655440001");
    private static final UUID VALID_ROUTE_STOP_ID = UUID.fromString("c80e8400-e29b-41d4-a716-446655440001");
    private static final UUID DRIVER_BUS_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");

    /*
     * Test Cases for DriverTripCommandController
     * 
     * This method sets up a mock authentication token for a driver with a specific
     * bus ID.
     * It is used to simulate an authenticated driver in the test cases.
     */

    private UsernamePasswordAuthenticationToken authenticatedDriver() {
        return new UsernamePasswordAuthenticationToken(
                DRIVER_BUS_ID,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_DRIVER")));
    }

    // =========================================================
    // POST /tracking/trips/{tripId}/start
    // =========================================================
    @Nested
    class StartTrip {

        // =========================================================
        // POST /tracking/trips/{tripId}/start - Happy Path
        // =========================================================

        @Test
        @WithMockDriver
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
        @WithMockDriver
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
        @WithMockDriver
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

            verify(confirmStopUseCase, times(1)).execute(eq(VALID_TRIP_ID), eq(VALID_ROUTE_STOP_ID), any());
            verify(tripDetailMapper, times(1)).toResponse(mockView);
        }

        // =========================================================
        // POST /tracking/trips/{tripId}/stops/{routeStopId}/confirm - Invalid UUIDs
        // this method tests that if either tripId or routeStopId is not a valid UUID
        // =========================================================

        @Test
        @WithMockDriver
        public void shouldReturn400_WhenAnyIdIsNotValidUUID() throws Exception {
            mockMvc.perform(post("/tracking/trips/{tripId}/stops/{routeStopId}/confirm",
                    "not-a-valid-uuid", VALID_ROUTE_STOP_ID))
                    .andExpect(status().isBadRequest());

            verify(confirmStopUseCase, times(0)).execute(any(), any(), any());
        }
    }

    // =========================================================
    // POST /tracking/trips/{tripId}/finish
    // =========================================================
    @Nested
    class FinishTrip {

        @Test
        @WithMockDriver
        void shouldReturn200AndFinishData_WhenTripIsFinishedSuccessfully() throws Exception {
            // Arrange
            TripFinishView finishView = new TripFinishView(
                    VALID_TRIP_ID,
                    "COMPLETED",
                    LocalTime.of(8, 0),
                    LocalTime.of(10, 0),
                    5);
            when(finishTripUseCase.execute(eq(VALID_TRIP_ID), any()))
                    .thenReturn(finishView);

            // Act & Assert
            mockMvc.perform(post("/tracking/trips/{tripId}/finish", VALID_TRIP_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tripId").value(VALID_TRIP_ID.toString()))
                    .andExpect(jsonPath("$.status").value("COMPLETED"))
                    .andExpect(jsonPath("$.actualStartTime").value("08:00:00"))
                    .andExpect(jsonPath("$.actualEndTime").value("10:00:00"))
                    .andExpect(jsonPath("$.delayMinutes").value(5));

            verify(finishTripUseCase).execute(eq(VALID_TRIP_ID), any());
        }

        // Optional Method: GlobalExceptionHandlerTest is already testin this mapping
        @Test
        @WithMockDriver
        void shouldReturn404_WhenTripNotFound() throws Exception {
            when(finishTripUseCase.execute(eq(VALID_TRIP_ID), any()))
                    .thenThrow(new NotFoundException(ErrorCode.TRIP_NOT_FOUND,
                            "Trip not found"));

            mockMvc.perform(post("/tracking/trips/{tripId}/finish", VALID_TRIP_ID))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("TRIP_NOT_FOUND"));
        }

    }
}