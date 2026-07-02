package com.bustracking.companies.unit.delegates;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bustracking.companies.domain.dto.TripStopDetailProjection;
import com.bustracking.companies.domain.enums.TripStatus;
import com.bustracking.companies.domain.model.Trip;
import com.bustracking.companies.domain.model.TripStop;
import com.bustracking.companies.domain.repository.TripRepository;
import com.bustracking.companies.domain.repository.TripStopRepository;
import com.bustracking.companies.infrastructure.delegate.ConfirmStopDelegate;
import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
public class ConfirmStopDelegateTest {

    // Mocks
    @Mock
    private TripRepository tripRepository;
    @Mock
    private TripStopRepository tripStopRepository;

    private ConfirmStopDelegate confirmStopDelegate;

    // Test Common Data
    // Arrange
    private final UUID TRIP_ID = UUID.fromString("b70e8400-e29b-41d4-a716-446655440001");
    private final UUID STOP_ID = UUID.fromString("c80e8400-e29b-41d4-a716-446655440001");
    private final UUID SCHEDULE_ID = UUID.fromString("d90e8400-e29b-41d4-a716-446655440001");

    @BeforeEach
    void setUp() {
        confirmStopDelegate = new ConfirmStopDelegate(tripRepository, tripStopRepository);
    }

    // =========================================================
    // Happy Path Test - Execute
    // =========================================================
    @Test
    public void shouldConfirmStop_WhenAllInputsAreValid() {
        // Arrange
        // We create a real Trip object and configure the repository to return it
        Trip realTrip = new Trip(UUID.randomUUID());
        realTrip.start(UUID.randomUUID());
        when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(realTrip));

        // We create the real projection of the first incomplete stop
        TripStopDetailProjection firstStop = new TripStopDetailProjection(
                STOP_ID, // routeStopId
                UUID.randomUUID(), // stopId
                "Stop Name", // stopName
                BigDecimal.valueOf(9.0), // stopLat
                BigDecimal.valueOf(-84.0), // stopLng
                "Reference", // stopReference
                1, // orderIndex
                0, // estimatedTimeOffset
                null // completedAt
        );

        when(tripRepository.findStopsByTripId(TRIP_ID)).thenReturn(List.of(firstStop));

        // 3. Crear TripStop real y configurar repositorio para devolverlo
        TripStop realTripStop = new TripStop(TRIP_ID, STOP_ID);
        when(tripStopRepository.findByTripIdAndRouteStopId(TRIP_ID, STOP_ID))
                .thenReturn(Optional.of(realTripStop));

        // Act
        confirmStopDelegate.execute(TRIP_ID, STOP_ID);

        // Assert
        verify(tripRepository, times(1)).findById(TRIP_ID);
        verify(tripRepository, times(1)).findStopsByTripId(TRIP_ID);
        verify(tripStopRepository, times(1)).findByTripIdAndRouteStopId(TRIP_ID, STOP_ID);
        verify(tripStopRepository, times(1)).save(realTripStop);
        assertNotNull(realTripStop.getCompletedAt());
    }

    // =========================================================
    // exectue - Trip Not Found
    // =========================================================

    @Test
    public void shouldThrowNotFoundException_WhenTripDoesNotExist() {
        // Arrange
        when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            confirmStopDelegate.execute(TRIP_ID, STOP_ID);
        });

        assertEquals(ErrorCode.TRIP_NOT_FOUND, exception.getErrorCode());

        // Verificamos que SOLO se llamó a findById y NUNCA a los otros métodos
        verify(tripRepository, times(1)).findById(TRIP_ID);
        verify(tripRepository, never()).findStopsByTripId(any());
        verify(tripStopRepository, never()).findByTripIdAndRouteStopId(any(), any());
        verify(tripStopRepository, never()).save(any());
    }

    // =========================================================
    // execute - Trip Not In Progress
    // =========================================================
    @Test
    public void shouldThrowBusinessRuleException_WhenTripIsNotInProgress() {
        // Arrange: we never call the start() method, so the trip is not in IN_PROGRESS
        Trip realTrip = new Trip(UUID.randomUUID());

        // We make sure that is not in IN_PROGRESS (in case constructor changes in the
        // future)
        assertNotEquals(TripStatus.IN_PROGRESS, realTrip.getStatus());
        when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(realTrip));

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            confirmStopDelegate.execute(TRIP_ID, STOP_ID);
        });

        assertEquals(ErrorCode.INVALID_STATE, exception.getErrorCode());

        // Verificamos que solo se llamó a findById y que el resto NO se ejecuta
        verify(tripRepository, times(1)).findById(TRIP_ID);
        verify(tripRepository, never()).findStopsByTripId(any());
        verify(tripStopRepository, never()).findByTripIdAndRouteStopId(any(), any());
        verify(tripStopRepository, never()).save(any());
    }

    // =========================================================
    // execute - Trip Has No Stops
    // =========================================================
    @Test
    public void shouldThrowBusinessRuleException_WhenTripHasNoStops() {
        // Arrange
        Trip realTrip = new Trip(UUID.randomUUID());
        realTrip.start(UUID.randomUUID());
        when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(realTrip));

        // Empty list of stops to simulate a trip with no stops
        when(tripRepository.findStopsByTripId(TRIP_ID)).thenReturn(List.of());

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            confirmStopDelegate.execute(TRIP_ID, STOP_ID);
        });

        assertEquals(ErrorCode.INVALID_STATE, exception.getErrorCode());

        verify(tripRepository, times(1)).findById(TRIP_ID);
        verify(tripRepository, times(1)).findStopsByTripId(TRIP_ID);

        // never
        verify(tripStopRepository, never()).findByTripIdAndRouteStopId(any(), any());
        verify(tripStopRepository, never()).save(any());
    }

    @Test
    public void shouldThrowBusinessRuleException_WhenAllStopsAreCompleted() {
        // Arrange
        Trip realTrip = new Trip(UUID.randomUUID());
        realTrip.start(UUID.randomUUID());
        when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(realTrip));

        // Create two stops, the first one is uncompleted, the second one is completed
        TripStopDetailProjection firstStop = new TripStopDetailProjection(
                UUID.randomUUID(), // routeStopId
                UUID.randomUUID(), // stopId
                "First Stop", // stopName
                BigDecimal.valueOf(9.0), // stopLat
                BigDecimal.valueOf(-84.0), // stopLng
                "Reference", // stopReference
                1, // orderIndex
                0, // estimatedTimeOffset
                LocalDateTime.now() // completedAt
        );

        TripStopDetailProjection secondStop = new TripStopDetailProjection(
                STOP_ID, // routeStopId (the one we will try to confirm)
                UUID.randomUUID(), // stopId
                "Second Stop", // stopName
                BigDecimal.valueOf(9.1), // stopLat
                BigDecimal.valueOf(-84.1), // stopLng
                "Reference", // stopReference
                2, // orderIndex
                5, // estimatedTimeOffset
                LocalDateTime.now() // completedAt
        );

        when(tripRepository.findStopsByTripId(TRIP_ID)).thenReturn(List.of(firstStop, secondStop));

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            confirmStopDelegate.execute(TRIP_ID, STOP_ID);
        });

        assertEquals(ErrorCode.INVALID_STATE, exception.getErrorCode());

        verify(tripRepository, times(1)).findById(TRIP_ID);
        verify(tripRepository, times(1)).findStopsByTripId(TRIP_ID);
        verify(tripStopRepository, never()).findByTripIdAndRouteStopId(any(), any());
        verify(tripStopRepository, never()).save(any());
    }

    @Test
    public void shouldThrowBusinessRuleException_WhenStopAreOutOfOrder() {
        // Arrange
        Trip realTrip = new Trip(UUID.randomUUID());
        realTrip.start(UUID.randomUUID());
        when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(realTrip));

        // Create two completed stops
        TripStopDetailProjection firstStop = new TripStopDetailProjection(
                UUID.randomUUID(), // routeStopId
                UUID.randomUUID(), // stopId
                "First Stop", // stopName
                BigDecimal.valueOf(9.0), // stopLat
                BigDecimal.valueOf(-84.0), // stopLng
                "Reference", // stopReference
                1, // orderIndex
                0, // estimatedTimeOffset
                null // completedAt
        );

        TripStopDetailProjection secondStop = new TripStopDetailProjection(
                STOP_ID, // routeStopId (the one we will try to confirm)
                UUID.randomUUID(), // stopId
                "Second Stop", // stopName
                BigDecimal.valueOf(9.1), // stopLat
                BigDecimal.valueOf(-84.1), // stopLng
                "Reference", // stopReference
                2, // orderIndex
                5, // estimatedTimeOffset
                null // completedAt
        );

        when(tripRepository.findStopsByTripId(TRIP_ID)).thenReturn(List.of(firstStop, secondStop));

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            confirmStopDelegate.execute(TRIP_ID, STOP_ID);
        });

        assertEquals(ErrorCode.INVALID_STATE, exception.getErrorCode());

        verify(tripRepository, times(1)).findById(TRIP_ID);
        verify(tripRepository, times(1)).findStopsByTripId(TRIP_ID);
        verify(tripStopRepository, never()).findByTripIdAndRouteStopId(any(), any());
        verify(tripStopRepository, never()).save(any());
    }

}
