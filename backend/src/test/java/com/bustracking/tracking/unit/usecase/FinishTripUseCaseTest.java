package com.bustracking.tracking.unit.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.tracking.application.usecase.FinishTripUseCase;
import com.bustracking.tracking.domain.contract.BusExistsById;
import com.bustracking.tracking.domain.contract.FinishTrip;
import com.bustracking.tracking.domain.model.TripFinishView;

@ExtendWith(MockitoExtension.class)
class FinishTripUseCaseTest {

    @Mock
    private BusExistsById busExistsByIdMock;

    @Mock
    private FinishTrip finishTripMock;

    private FinishTripUseCase finishTripUseCase;

    private static final UUID VALID_BUS_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");
    private static final UUID VALID_TRIP_ID = UUID.fromString("b70e8400-e29b-41d4-a716-446655440001");

    @BeforeEach
    void setUp() {
        finishTripUseCase = new FinishTripUseCase(busExistsByIdMock, finishTripMock);
    }

    // =========================================================
    // Happy Path – Trip finished successfully
    // =========================================================
    @Test
    void shouldReturnTripFinishView_WhenBusExistsAndTripIsInProgress() {
        // Arrange
        TripFinishView expectedView = new TripFinishView(
                VALID_TRIP_ID,
                "COMPLETED",
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                5);
        when(busExistsByIdMock.check(VALID_BUS_ID)).thenReturn(true);
        when(finishTripMock.execute(VALID_TRIP_ID)).thenReturn(expectedView);

        // Act
        TripFinishView result = finishTripUseCase.execute(VALID_TRIP_ID, VALID_BUS_ID);

        // Assert
        assertNotNull(result);
        assertEquals(expectedView, result);
        verify(busExistsByIdMock).check(VALID_BUS_ID);
        verify(finishTripMock).execute(VALID_TRIP_ID);
    }

    // =========================================================
    // Bus not found
    // =========================================================
    @Test
    void shouldThrowNotFoundException_WhenBusDoesNotExist() {
        // Arrange
        when(busExistsByIdMock.check(VALID_BUS_ID)).thenReturn(false);

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> finishTripUseCase.execute(VALID_TRIP_ID, VALID_BUS_ID));

        assertEquals(ErrorCode.BUS_NOT_FOUND, exception.getErrorCode());
        verify(busExistsByIdMock).check(VALID_BUS_ID);
        verifyNoInteractions(finishTripMock);
    }

    // =========================================================
    // Propagation of BusinessRuleException from delegate
    // =========================================================
    @Test
    void shouldPropagateBusinessRuleException_WhenFinishTripFails() {
        // Arrange
        when(busExistsByIdMock.check(VALID_BUS_ID)).thenReturn(true);
        doThrow(new BusinessRuleException(ErrorCode.INVALID_STATE,
                "Trip is not in progress"))
                .when(finishTripMock).execute(VALID_TRIP_ID);

        // Act & Assert
        assertThrows(BusinessRuleException.class,
                () -> finishTripUseCase.execute(VALID_TRIP_ID, VALID_BUS_ID));

        verify(busExistsByIdMock).check(VALID_BUS_ID);
        verify(finishTripMock).execute(VALID_TRIP_ID);
    }

    // =========================================================
    // Propagation of NotFoundException from delegate
    // =========================================================
    @Test
    void shouldPropagateNotFoundException_WhenTripDoesNotExist() {
        // Arrange
        when(busExistsByIdMock.check(VALID_BUS_ID)).thenReturn(true);
        doThrow(new NotFoundException(ErrorCode.TRIP_NOT_FOUND,
                "Trip not found"))
                .when(finishTripMock).execute(VALID_TRIP_ID);

        // Act & Assert
        assertThrows(NotFoundException.class,
                () -> finishTripUseCase.execute(VALID_TRIP_ID, VALID_BUS_ID));

        verify(busExistsByIdMock).check(VALID_BUS_ID);
        verify(finishTripMock).execute(VALID_TRIP_ID);
    }
}