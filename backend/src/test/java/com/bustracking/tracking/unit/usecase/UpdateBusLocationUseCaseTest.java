package com.bustracking.tracking.unit.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.shared.valueobjects.GpsCoordinate;
import com.bustracking.tracking.application.usecase.UpdateBusLocationUseCase;
import com.bustracking.tracking.domain.contract.BusExistsById;
import com.bustracking.tracking.domain.repository.BusLocationRepository;

//Annotation to enable Mockito (@Mock) in this class
@ExtendWith(MockitoExtension.class)
public class UpdateBusLocationUseCaseTest {

    @Mock
    private BusLocationRepository busLocationRepository;

    // Type of value given by @Bean using a contract (Functional Interface)
    @Mock
    private BusExistsById busExistsById;

    private UpdateBusLocationUseCase updateBusLocationUseCase;

    //Test Data -> Arrange

    private final UUID validBusId = UUID.randomUUID();
    private final GpsCoordinate validCoordinate = new GpsCoordinate(
        new BigDecimal("9.934739"),
        new BigDecimal("-84.087502")
    );


    // Setup before each test to initialize the use case with mocked dependencies
    @BeforeEach
    void setUp(){
        updateBusLocationUseCase = new UpdateBusLocationUseCase(
            busLocationRepository, 
            busExistsById);
    }

    //==========================================================
    // Happy Path Tests
    //==========================================================

    @Test
    void shouldUpdateBusLocationWhenBusExistsAndHasLocation(){

        //Arrange
        when(busExistsById.check(validBusId)).thenReturn(true);

        //Act
        updateBusLocationUseCase.execute(validBusId, validCoordinate.getLat(), validCoordinate.getLng());

        //Assert 
        verify(busExistsById, times(1)).check(validBusId);
        verify(busLocationRepository, times(1)).save(any());
    }


    //==========================================================
    // Bus Does Not Exist in Database Schema Of Companies Module 

    @Test
    void shouldThrowNotFoundExceptionWhenBusDoesNotExist(){

        //Arrange
        when(busExistsById.check(validBusId)).thenReturn(false);

        //Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
            () ->  updateBusLocationUseCase.execute(validBusId, validCoordinate.getLat(), validCoordinate.getLng()));

        assertEquals(ErrorCode.BUS_NOT_FOUND, exception.getErrorCode());

        verify(busExistsById, times(1)).check(validBusId);
        verify(busLocationRepository, times(0)).save(any());

    }

    @Test
    void shouldThrowNotFoundExceptionWhenBusIdIsNull() {
        when(busExistsById.check(null)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> updateBusLocationUseCase.execute(null, 
                validCoordinate.getLat(), 
                validCoordinate.getLng())
        );

        assertEquals(ErrorCode.BUS_NOT_FOUND, exception.getErrorCode());
        verify(busExistsById, times(1)).check(null);
        verify(busLocationRepository, never()).save(any());
    }
}
