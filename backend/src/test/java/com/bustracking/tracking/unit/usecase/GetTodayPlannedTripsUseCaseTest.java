package com.bustracking.tracking.unit.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import com.bustracking.tracking.application.usecase.GetTodayPlannedTripsUseCase;
import com.bustracking.tracking.domain.contract.BusExistsById;
import com.bustracking.tracking.domain.contract.GetTodayPlannedTripsByBusRoutes;
import com.bustracking.tracking.domain.model.TripView;

@ExtendWith(MockitoExtension.class)
public class GetTodayPlannedTripsUseCaseTest {
    
    @Mock 
    private GetTodayPlannedTripsByBusRoutes getTodayTripsDelegate;
    
    @Mock
    private BusExistsById busExistsById;

    private GetTodayPlannedTripsUseCase getTodayPlannedTripsUseCase;

    // Test data
    private final UUID validBusId = UUID.randomUUID();

    @BeforeEach
    void setUp(){
        //instances always are created in the setUp method, as mocks are not created until runtime
        getTodayPlannedTripsUseCase = new GetTodayPlannedTripsUseCase(getTodayTripsDelegate, busExistsById);
    }

    //===========================================================
    // Happy Path test - execute()
    //===========================================================

    @Test
    void shouldReturnTodayTripsWhenBusExists() {
        // Arrange
        List<TripView> expectedTrips = List.of(
            new TripView(UUID.randomUUID(), "Ruta 300", "San José",
                         "Cartago", LocalTime.of(5, 45), "PLANNED")
        );
        when(busExistsById.check(validBusId)).thenReturn(true);
        when(getTodayTripsDelegate.execute(validBusId)).thenReturn(expectedTrips);

        // Act
        List<TripView> result = getTodayPlannedTripsUseCase.execute(validBusId);

        // Assert
        assertEquals(expectedTrips, result);
        verify(busExistsById, times(1)).check(validBusId);
        verify(getTodayTripsDelegate, times(1)).execute(validBusId);
    }

    @Test
    void shouldReturnEmptyListWhenBusHasNoTripsToday() {
        // Arrange
        when(busExistsById.check(validBusId)).thenReturn(true);
        when(getTodayTripsDelegate.execute(validBusId)).thenReturn(List.of());

        // Act
        List<TripView> result = getTodayPlannedTripsUseCase.execute(validBusId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(getTodayTripsDelegate, times(1)).execute(validBusId);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenBusDoesNotExist() {
        // Arrange
        when(busExistsById.check(validBusId)).thenReturn(false);

        // Act & Assert
        NotFoundException exception = assertThrows(
            NotFoundException.class,
            () -> getTodayPlannedTripsUseCase.execute(validBusId)
        );

        assertEquals(ErrorCode.BUS_NOT_FOUND, exception.getErrorCode());
        verify(busExistsById, times(1)).check(validBusId);
        verify(getTodayTripsDelegate, never()).execute(any());
    }
    
}
