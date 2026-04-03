package com.bustracking.tracking.unit.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.shared.valueobjects.GpsCoordinate;
import com.bustracking.tracking.application.usecase.GetBusLocationUseCase;
import com.bustracking.tracking.domain.contract.BusExistsById;
import com.bustracking.tracking.domain.model.BusLocation;
import com.bustracking.tracking.domain.repository.BusLocationRepository;

//Annotation to enable Mockito (@Mock) in this class
@ExtendWith(MockitoExtension.class)
public class GetBusLocationUseCaseTest {

    @Mock
    private BusLocationRepository busLocationRepository;

    @Mock
    private BusExistsById busExistsById;

    private GetBusLocationUseCase getBusLocationUseCase;

    // Test data
    private final UUID validBusId = UUID.randomUUID();
    private final GpsCoordinate validCoordinate = new GpsCoordinate(
        new BigDecimal("9.934739"),
        new BigDecimal("-84.087502")
    );
    private final LocalDateTime validTimestamp = LocalDateTime.now();

    // Setup before each test to initialize the use case with mocked dependencies
    @BeforeEach
    void setUp() {
        getBusLocationUseCase = new GetBusLocationUseCase(
            busLocationRepository,
            busExistsById
        );
    }

    //==========================================================
    // Happy Path Tests
    //==========================================================

    @Test
    void shouldReturnBusLocationWhenBusExistsAndHasLocation() {
        // Arrange
        BusLocation expectedLocation = new BusLocation(
            validBusId,
            validCoordinate,
            validTimestamp
        );

        when(busExistsById.check(validBusId)).thenReturn(true);
        when(busLocationRepository.findByBusId(validBusId))
            .thenReturn(Optional.of(expectedLocation));

        // Act
        BusLocation actualLocation = getBusLocationUseCase.execute(validBusId);

        // Assert
        assertEquals(expectedLocation, actualLocation);
        assertEquals(validBusId, actualLocation.getBusId());
        assertEquals(validCoordinate, actualLocation.getGpsCoordinate());
        verify(busExistsById, times(1)).check(validBusId);
        verify(busLocationRepository, times(1)).findByBusId(validBusId);
    }

    //==========================================================
    // Bus Does Not Exist in Companies Module (No Coupling Violation)
    //==========================================================

    @Test
    void shouldThrowNotFoundExceptionWhenBusDoesNotExist() {
        // Arrange
        when(busExistsById.check(validBusId)).thenReturn(false);

        // Act & Assert
        NotFoundException exception = assertThrows(
            NotFoundException.class,
            () -> getBusLocationUseCase.execute(validBusId)
        );

        assertEquals(ErrorCode.BUS_NOT_FOUND, exception.getErrorCode());
        verify(busExistsById, times(1)).check(validBusId);
        verify(busLocationRepository, never()).findByBusId(validBusId);
    }

    //==========================================================
    // Bus Exists But Has No Location
    //==========================================================

    @Test
    void shouldThrowNotFoundExceptionWhenBusHasNoLocation() {
        // Arrange
        when(busExistsById.check(validBusId)).thenReturn(true);
        when(busLocationRepository.findByBusId(validBusId))
            .thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(
            NotFoundException.class,
            () -> getBusLocationUseCase.execute(validBusId)
        );

        assertEquals(ErrorCode.BUS_LOCATION_NOT_FOUND, exception.getErrorCode());
        verify(busExistsById, times(1)).check(validBusId);
        verify(busLocationRepository, times(1)).findByBusId(validBusId);
    }

    //==========================================================
    // Null Bus ID
    //==========================================================

    @Test
    void shouldThrowNotFoundExceptionWhenBusIdIsNull() {
        // Arrange
        when(busExistsById.check(null)).thenReturn(false);

        // Act & Assert
        assertThrows(
            NotFoundException.class,
            () -> getBusLocationUseCase.execute(null)
        );
    }
}
