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
import com.bustracking.tracking.application.usecase.ConfirmStopUseCase;
import com.bustracking.tracking.domain.contract.BusExistsById;
import com.bustracking.tracking.domain.contract.ConfirmStop;
import com.bustracking.tracking.domain.contract.GetTripDetail;
import com.bustracking.tracking.domain.model.TripDetailView;

@ExtendWith(MockitoExtension.class)
public class ConfirmStopUseCaseTest {

    // Mocks
    @Mock
    private BusExistsById busExistsByIdMock;
    @Mock
    private ConfirmStop confirmStopMock;
    @Mock
    private GetTripDetail getTripDetailMock;

    private ConfirmStopUseCase confirmStopUseCase;

    // Test Common Data
    private final UUID VALID_BUS_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");
    private final UUID VALID_TRIP_ID = UUID.fromString("b70e8400-e29b-41d4-a716-446655440001");
    private final UUID VALID_ROUTE_STOP_ID = UUID.fromString("c80e8400-e29b-41d4-a716-446655440001");

    @BeforeEach
    void setUp() {
        confirmStopUseCase = new ConfirmStopUseCase(
                busExistsByIdMock,
                confirmStopMock,
                getTripDetailMock);
    }

    // =========================================================
    // Happy Path Test - Execute
    // =========================================================
    @Test
    public void shouldConfirmStopAndReturnUpdatedDetail_WhenAllInputsAreValid() {
        // Arrange
        TripDetailView expectedDetail = mock(TripDetailView.class);
        when(busExistsByIdMock.check(VALID_BUS_ID)).thenReturn(true);
        when(getTripDetailMock.execute(VALID_TRIP_ID)).thenReturn(expectedDetail);

        // Act
        TripDetailView result = confirmStopUseCase.execute(VALID_TRIP_ID, VALID_ROUTE_STOP_ID, VALID_BUS_ID);

        // Assert
        assertNotNull(result);
        // Assert that the returned TripDetailView is the same as the expected one from this usecase call
        assertEquals(expectedDetail, result);
        verify(busExistsByIdMock, times(1)).check(VALID_BUS_ID);
        verify(confirmStopMock, times(1)).execute(VALID_TRIP_ID, VALID_ROUTE_STOP_ID);
        verify(getTripDetailMock, times(1)).execute(VALID_TRIP_ID);
    }

    @Test
    void shouldThrowNotFoundException_WhenBusDoesNotExist() {
        // Arrange
        when(busExistsByIdMock.check(VALID_BUS_ID)).thenReturn(false);

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> confirmStopUseCase.execute(VALID_TRIP_ID, VALID_ROUTE_STOP_ID, VALID_BUS_ID));

        assertEquals(ErrorCode.BUS_NOT_FOUND, exception.getErrorCode());
        verify(busExistsByIdMock, times(1)).check(VALID_BUS_ID);
        verify(confirmStopMock, never()).execute(any(), any());
        verify(getTripDetailMock, never()).execute(any());
    }

    // =========================================================
    // Propagate BusinessRuleException from ConfirmStop delegate
    // =========================================================
    @Test
    void shouldPropagateBusinessRuleException_WhenConfirmStopFails() {
        // Arrange
        when(busExistsByIdMock.check(VALID_BUS_ID)).thenReturn(true);
        doThrow(new BusinessRuleException(
                ErrorCode.INVALID_STATE,
                "Trip is not in progress")).when(confirmStopMock).execute(VALID_TRIP_ID, VALID_ROUTE_STOP_ID);

        // Act & Assert
       assertThrows(
                BusinessRuleException.class,
                () -> confirmStopUseCase.execute(VALID_TRIP_ID, VALID_ROUTE_STOP_ID, VALID_BUS_ID));

        verify(busExistsByIdMock, times(1)).check(VALID_BUS_ID);
        verify(confirmStopMock, times(1)).execute(VALID_TRIP_ID, VALID_ROUTE_STOP_ID);
        verify(getTripDetailMock, never()).execute(any());
    }

    // =========================================================
    // Propagate NotFoundException from GetTripDetail
    // =========================================================
    @Test
    void shouldPropagateNotFoundException_WhenGetTripDetailFails() {
        // Arrange
        when(busExistsByIdMock.check(VALID_BUS_ID)).thenReturn(true);
        doThrow(new NotFoundException(
                ErrorCode.TRIP_NOT_FOUND,
                "Trip not found")).when(getTripDetailMock).execute(VALID_TRIP_ID);

        // Act & Assert
        assertThrows(
                NotFoundException.class,
                () -> confirmStopUseCase.execute(VALID_TRIP_ID, VALID_ROUTE_STOP_ID, VALID_BUS_ID));

        verify(busExistsByIdMock, times(1)).check(VALID_BUS_ID);
        verify(confirmStopMock, times(1)).execute(VALID_TRIP_ID, VALID_ROUTE_STOP_ID);
        verify(getTripDetailMock, times(1)).execute(VALID_TRIP_ID);
    }

}
