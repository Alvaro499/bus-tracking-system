package com.bustracking.tracking.unit.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.bustracking.shared.exception.ValidationException;
import com.bustracking.shared.valueobjects.GpsCoordinate;
import com.bustracking.tracking.domain.model.BusLocation;

import static org.junit.jupiter.api.Assertions.*;

public class BusLocationTest {

    // Test data
    private final UUID validBusId = UUID.randomUUID();
    private final GpsCoordinate validCoordinate = new GpsCoordinate(
        new BigDecimal("9.934739"), 
        new BigDecimal("-84.087502"));

    private final LocalDateTime validTimestamp = LocalDateTime.now();
    //==========================================================
    // Happy Path Tests
    //==========================================================

    @Test
    void shouldCreateBusLocationWithValidData() {
        BusLocation busLocation = new BusLocation(validBusId, validCoordinate,  validTimestamp);
        
        assertEquals(validBusId, busLocation.getBusId());
        assertEquals(validCoordinate, busLocation.getGpsCoordinate());
        assertEquals(validTimestamp, busLocation.getUpdatedAt());
    }


    //==========================================================
    // Invalid busId
    //==========================================================

    @Test
    void shouldThrowWhenBusIdIsNull() {
        assertThrows(ValidationException.class, () ->
            new BusLocation(null, validCoordinate, validTimestamp)
        );
    }

    //==========================================================
    // Invalid Coordinate
    //==========================================================
    
    @Test
    void shouldThrowWhenCoordinateIsNull(){
        assertThrows(ValidationException.class, () ->
            new BusLocation(validBusId, null, validTimestamp)
        );
    }


    //==========================================================
    // Invalid Timestamp        
    //==========================================================

    @Test
    void shouldThrowWhenTimestampIsNull(){
        assertThrows(ValidationException.class, () ->
            new BusLocation(validBusId, validCoordinate, null)
        );
    }

    @Test
    void shouldThrowWhenAllFieldsAreNull() {
        assertThrows(ValidationException.class, () ->
            new BusLocation(null, null, null)
        );
    }
}


