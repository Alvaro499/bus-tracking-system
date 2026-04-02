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

    // =========================================================
    // Equals and HashCode
    // =========================================================

    @Test
    void shouldBeEqualWhenSameValues() {
        LocalDateTime timestamp = LocalDateTime.now();
        UUID busId = UUID.randomUUID();
        GpsCoordinate coordinate = new GpsCoordinate(new BigDecimal("9.934739"), new BigDecimal("-84.087502"));

        BusLocation location1 = new BusLocation(busId, coordinate, timestamp);
        BusLocation location2 = new BusLocation(busId, coordinate, timestamp);

        assertEquals(location1, location2);
        assertEquals(location1.hashCode(), location2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentBusIds() {
        BusLocation location1 = new BusLocation(UUID.randomUUID(), validCoordinate, validTimestamp);
        BusLocation location2 = new BusLocation(UUID.randomUUID(), validCoordinate, validTimestamp);

        assertNotEquals(location1, location2);
    }

    @Test
    void shouldNotBeEqualWhenDifferentCoordinates() {
        GpsCoordinate coordinate1 = new GpsCoordinate(new BigDecimal("9.934739"), new BigDecimal("-84.087502"));
        GpsCoordinate coordinate2 = new GpsCoordinate(new BigDecimal("10.0"), new BigDecimal("-85.0"));

        BusLocation location1 = new BusLocation(validBusId, coordinate1, validTimestamp);
        BusLocation location2 = new BusLocation(validBusId, coordinate2, validTimestamp);

        assertNotEquals(location1, location2);
    }

    @Test
    void shouldNotBeEqualWhenDifferentTimestamps() {
        LocalDateTime timestamp1 = LocalDateTime.now();
        LocalDateTime timestamp2 = timestamp1.plusMinutes(1);

        BusLocation location1 = new BusLocation(validBusId, validCoordinate, timestamp1);
        BusLocation location2 = new BusLocation(validBusId, validCoordinate, timestamp2);

        assertNotEquals(location1, location2);
    }
}


