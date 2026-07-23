package com.bustracking.companies.unit.delegates;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bustracking.companies.domain.model.Trip;
import com.bustracking.companies.domain.repository.TripRepository;
import com.bustracking.companies.infrastructure.delegate.FinishTripDelegate;
import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.tracking.domain.model.TripFinishView;

@ExtendWith(MockitoExtension.class)
class FinishTripDelegateTest {

    @Mock
    private TripRepository tripRepositoryMock;

    private FinishTripDelegate delegate;

    private static final UUID TRIP_ID = UUID.fromString("b70e8400-e29b-41d4-a716-446655440001");
    private static final UUID SCHEDULE_ID = UUID.randomUUID();
    private static final UUID BUS_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");

    @BeforeEach
    void setUp() {
        delegate = new FinishTripDelegate(tripRepositoryMock);
    }

    // =========================================================
    // Happy Path – Trips Finished Succesfully
    // =========================================================

    @Test
    void shouldCompleteTripAndReturnFinishView_WhenTripIsInProgress() {
        // Arrange
        Trip trip = new Trip(SCHEDULE_ID);          // status PLANNED
        trip.start(BUS_ID);                         // change to IN_PROGRESS, assing actualStartTime
        when(tripRepositoryMock.findById(TRIP_ID)).thenReturn(Optional.of(trip));

        // Act
        TripFinishView result = delegate.execute(TRIP_ID);

        // Assert
        assertEquals(trip.getId(), result.tripId());
        assertEquals("COMPLETED", result.status());
        assertNotNull(result.actualStartTime());
        assertNotNull(result.actualEndTime());
        assertEquals(0, result.delayMinutes());      // delayMinutes was null -> se convierte a 0

        verify(tripRepositoryMock, times(1)).findById(TRIP_ID);
        verify(tripRepositoryMock, times(1)).save(trip);
    }

    // =========================================================
    // Trip no encontrado
    // =========================================================

    @Test
    void shouldThrowNotFoundException_WhenTripDoesNotExist() {
        when(tripRepositoryMock.findById(TRIP_ID)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> delegate.execute(TRIP_ID));

        assertEquals(ErrorCode.TRIP_NOT_FOUND, exception.getErrorCode());
        verify(tripRepositoryMock).findById(TRIP_ID);
        verify(tripRepositoryMock, never()).save(any());
    }

    // =========================================================
    // Trip is not in progress (PLANNED or COMPLETED)
    // =========================================================

    @Test
    void shouldThrowBusinessRuleException_WhenTripIsNotInProgress() {
        // Trip as PLANNED, never was intialized
        Trip plannedTrip = new Trip(SCHEDULE_ID);
        when(tripRepositoryMock.findById(TRIP_ID)).thenReturn(Optional.of(plannedTrip));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> delegate.execute(TRIP_ID));

        assertEquals(ErrorCode.INVALID_STATE, exception.getErrorCode());
        verify(tripRepositoryMock).findById(TRIP_ID);
        verify(tripRepositoryMock, never()).save(any());
    }
}