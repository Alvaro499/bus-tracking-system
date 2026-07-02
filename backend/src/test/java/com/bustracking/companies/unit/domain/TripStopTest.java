package com.bustracking.companies.unit.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.Test;

import com.bustracking.companies.domain.model.TripStop;
import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ValidationException;

public class TripStopTest {

    // Common data test
        // Common data test
    private final UUID validTripId = UUID.randomUUID();
    private final UUID validRouteStopId = UUID.randomUUID();

    //==========================================================
    // Happy Path Tests - Constructor
    //==========================================================

    @Test
    public void shouldCreateValidTripStop_WhenValuesAreValid(){

        // Arrange
        TripStop tripStop = new TripStop(validTripId, validRouteStopId);

        // Act - Assert 
        assertNotNull(tripStop.getId());
        assertEquals(validTripId, tripStop.getTripId());
        assertEquals(validRouteStopId, tripStop.getRouteStopId());
        assertNull(tripStop.getCompletedAt());
    }

     // ==========================================================
    // Validation Tests - 
    // ==========================================================
    
    @Test
    public void shouldThrowException_WhenTripIdIsNull() {
        // Arrange
        UUID nullTripId = null;

        // Act & Assert
        assertThrows(ValidationException.class, () -> {
            new TripStop(nullTripId, validRouteStopId);
        });
    }

    @Test
    public void shouldThrowException_WhenRouteStopIdIsNull() {
        // Arrange
        UUID nullRouteStopId = null;

        // Act & Assert
        assertThrows(ValidationException.class, () -> {
            new TripStop(validTripId, nullRouteStopId);
        });
    }

    // =========================================================
    // Happy Path Test - markCompleted
    // =========================================================

    @Test
    public void shouldMarkTripStopAsCompleted_WhenNotCompletedYet() {
        
        // Arrange
        TripStop tripStop = new TripStop(validTripId, validRouteStopId);

        // Act
        tripStop.markCompleted();

        // Assert
        assertNotNull(tripStop.getCompletedAt());
    }

    // =========================================================
    // Business Rule Tests - markCompleted
    // =========================================================
    @Test
    public void shouldThrowException_WhenStopAlreadyCompleted() {
        // Arrange
        TripStop tripStop = new TripStop(validTripId, validRouteStopId);
        tripStop.markCompleted();

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> {
            tripStop.markCompleted();
        });

    }
}
