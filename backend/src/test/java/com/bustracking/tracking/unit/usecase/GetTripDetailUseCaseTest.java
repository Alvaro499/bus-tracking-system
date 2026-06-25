package com.bustracking.tracking.unit.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.tracking.application.usecase.GetTripDetailUseCase;
import com.bustracking.tracking.domain.contract.BusExistsById;
import com.bustracking.tracking.domain.contract.GetTripDetail;
import com.bustracking.tracking.domain.model.RouteStopView;
import com.bustracking.tracking.domain.model.StopView;
import com.bustracking.tracking.domain.model.TripDetailView;
import com.bustracking.tracking.domain.model.TripStopDetailView;
import com.bustracking.tracking.domain.model.TripView;

@ExtendWith(MockitoExtension.class)
public class GetTripDetailUseCaseTest {

    @Mock
    private GetTripDetail getTripDetail;

    @Mock
    private BusExistsById busExistsById;

    private GetTripDetailUseCase getTripDetailUseCase;

    private final UUID VALID_BUS_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");
    private final UUID VALID_TRIP_ID = UUID.fromString("b70e8400-e29b-41d4-a716-446655440001");

    @BeforeEach
    public void setUp() {
        getTripDetailUseCase = new GetTripDetailUseCase(busExistsById, getTripDetail);

    }

    // ==========================================================
    // Happy Path Tests
    // ==========================================================

    @Test
    public void shouldReturnTripDetail_WhenBusExists() {

        // Arrange
        when(busExistsById.check(VALID_BUS_ID)).thenReturn(true);
        TripDetailView expected = mock(TripDetailView.class);
        when(getTripDetail.execute(VALID_TRIP_ID)).thenReturn(expected);

        // Act
        TripDetailView result = getTripDetailUseCase.execute(VALID_BUS_ID, VALID_TRIP_ID);

        // Assert
        assertEquals(expected, result);
        verify(busExistsById, times(1)).check(VALID_BUS_ID);
        verify(getTripDetail, times(1)).execute(VALID_TRIP_ID);
    }

    /*
        Second Version of the happy path test, using dummy models instead of mocks.
     */

    @Test
    public void shouldReturnTripDetail_UsingDummyModels_WhenBusExists() {

        // Arrange
        when(busExistsById.check(VALID_BUS_ID)).thenReturn(true);

        TripView tripView = new TripView(
            VALID_TRIP_ID, "Ruta 300", "San José", "Cartago",
            LocalTime.of(8, 0), "PLANNED"
        );

        TripStopDetailView stop1 = new TripStopDetailView(
        new RouteStopView(UUID.randomUUID(), UUID.randomUUID(), 1, 0),
        new StopView(UUID.randomUUID(), "Parada 1", BigDecimal.ZERO, BigDecimal.ZERO,
        "Ref 1"),
        null
        );
        TripDetailView expected = new TripDetailView(tripView, List.of(stop1));
        when(getTripDetail.execute(VALID_TRIP_ID)).thenReturn(expected);

        // Act
        TripDetailView result = getTripDetailUseCase.execute(VALID_BUS_ID, VALID_TRIP_ID);

        // Assert
        assertEquals(expected, result);
        verify(busExistsById, times(1)).check(VALID_BUS_ID);
        verify(getTripDetail, times(1)).execute(VALID_TRIP_ID);
    }

    // ==========================================================
    // Sad Path Tests - execute()
    // ==========================================================

    @Test
    public void shouldThrowNotFoundException_WhenBusDoesNotExist() {

        // Arrange
        when(busExistsById.check(VALID_BUS_ID)).thenReturn(false);

        // Act & Assert
        NotFoundException exception = assertThrows(
            NotFoundException.class,
            () -> getTripDetailUseCase.execute(VALID_BUS_ID, VALID_TRIP_ID)
        );

        assertEquals(ErrorCode.BUS_NOT_FOUND, exception.getErrorCode());
        verify(busExistsById, times(1)).check(VALID_BUS_ID);
        verify(getTripDetail, never()).execute(any());
    }

    // ==========================================================
    // Sad Path Tests - execute()
    //  Delegate throws NotFoundException when trip not found
    // =========================================================

    @Test
    public void shouldPropagateNotFoundException_WhenTripNotFound() {

        // Arrange
        when(busExistsById.check(VALID_BUS_ID)).thenReturn(true);
        when(getTripDetail.execute(VALID_TRIP_ID)).thenThrow(
            new NotFoundException(ErrorCode.TRIP_NOT_FOUND, "Trip not found", "Trip with ID " + VALID_TRIP_ID + " does not exist")
        );

        // Act & Assert
        NotFoundException exception = assertThrows(
            NotFoundException.class,
            () -> getTripDetailUseCase.execute(VALID_BUS_ID, VALID_TRIP_ID)
        );

        assertEquals(ErrorCode.TRIP_NOT_FOUND, exception.getErrorCode());
        verify(busExistsById, times(1)).check(VALID_BUS_ID);
        verify(getTripDetail, times(1)).execute(VALID_TRIP_ID);
    }

    
}
