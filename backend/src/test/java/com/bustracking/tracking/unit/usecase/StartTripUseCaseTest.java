package com.bustracking.tracking.unit.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.tracking.application.usecase.StartTripUseCase;
import com.bustracking.tracking.domain.contract.BusExistsById;
import com.bustracking.tracking.domain.contract.StartTrip;

@ExtendWith(MockitoExtension.class)
public class StartTripUseCaseTest {
    
    @Mock
    private StartTrip startTrip;

    @Mock
    private BusExistsById busExistsById;

    private StartTripUseCase startTripUseCase;

    //Test Data
    private final UUID VALID_BUS_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");
    private final UUID VALID_TRIP_ID = UUID.fromString("b70e8400-e29b-41d4-a716-446655440001");

    @BeforeEach
    void setUp() {
        startTripUseCase = new StartTripUseCase(startTrip, busExistsById);
    }

    // =========================================================
    // Start Trip - Happy Path
    // =========================================================

    @Test
    void shouldStartTrip_WhenBusExists() {
        // Arrange
        when(busExistsById.check(VALID_BUS_ID)).thenReturn(true);

        // Act
        startTripUseCase.execute(VALID_TRIP_ID, VALID_BUS_ID);

        // Assert
        verify(busExistsById, times(1)).check(VALID_BUS_ID);
        verify(startTrip, times(1)).execute(VALID_TRIP_ID, VALID_BUS_ID);
    }    

    // =========================================================
    // Start Trip - Bus Not Found
    // =========================================================

    @Test
    void shouldThrowNotFoundException_WhenBusDoesNotExist() {
        // Arrange
        when(busExistsById.check(VALID_BUS_ID)).thenReturn(false);

        // Act & Assert
        NotFoundException exception = assertThrows(
            NotFoundException.class,
            () -> startTripUseCase.execute(VALID_TRIP_ID, VALID_BUS_ID)
        );

        assertEquals(ErrorCode.BUS_NOT_FOUND, exception.getErrorCode());
        verify(busExistsById).check(VALID_BUS_ID);
        verify(startTrip, never()).execute(any(), any());
    }

    // =========================================================
    // Start Trip - Invalid Input (Null Bus ID)
    // =========================================================
    @Test
    void shouldThrowNotFoundException_WhenBusIdIsNull(){

        when(busExistsById.check(null)).thenReturn(false);

        // Act & Assert
        assertThrows(
            NotFoundException.class,
            () -> startTripUseCase.execute(VALID_TRIP_ID, null)
        );

        verify(busExistsById, times(1)).check(null);
        verify(startTrip, never()).execute(any(), any());
    }

    // =========================================================
    // Start Trip Delegate - BussinessException Propagation
    // =========================================================
    @Test
    void shouldPropagateBusinessRuleException_WhenStartTripFails() {
        // Arrange
        when(busExistsById.check(VALID_BUS_ID)).thenReturn(true);
        doThrow(new BusinessRuleException(ErrorCode.INVALID_STATE, "Trip not in PLANNED state"))
            .when(startTrip).execute(VALID_TRIP_ID, VALID_BUS_ID);

        // Act & Assert
        BusinessRuleException exception = assertThrows(
            BusinessRuleException.class,
            () -> startTripUseCase.execute(VALID_TRIP_ID, VALID_BUS_ID)
        );

        assertEquals(ErrorCode.INVALID_STATE, exception.getErrorCode());
        verify(busExistsById, times(1)).check(VALID_BUS_ID);
        verify(startTrip, times(1)).execute(VALID_TRIP_ID, VALID_BUS_ID);
    }

    // =========================================================
    // Start Trip Delegate - NotFoundException Propagation
    // =========================================================

    @Test
    void shouldPropagateNotFoundException_WhenStartTripFailsBecauseTripNotFound() {
        // Arrange
        when(busExistsById.check(VALID_BUS_ID)).thenReturn(true);
        doThrow(new NotFoundException(
            ErrorCode.TRIP_NOT_FOUND, 
            "Trip not found"))
            .when(startTrip).execute(VALID_TRIP_ID, VALID_BUS_ID);

        // Act & Assert
        NotFoundException exception = assertThrows(
            NotFoundException.class,
            () -> startTripUseCase.execute(VALID_TRIP_ID, VALID_BUS_ID)
        );

        assertEquals(ErrorCode.TRIP_NOT_FOUND, exception.getErrorCode());
        verify(busExistsById, times(1)).check(VALID_BUS_ID);
        verify(startTrip, times(1)).execute(VALID_TRIP_ID, VALID_BUS_ID);
    }
}
