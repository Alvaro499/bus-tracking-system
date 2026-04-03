package com.bustracking.tracking.integration.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import com.bustracking.shared.testinfrastructure.RepositoryIntegrationTest;
import com.bustracking.shared.valueobjects.GpsCoordinate;
import com.bustracking.tracking.domain.model.BusLocation;
import com.bustracking.tracking.infrastructure.persistence.repository.BusLocationRepositoryImpl;
import com.bustracking.tracking.infrastructure.persistence.repository.BusLocationJpaRepository;

/**
 * Integration tests for BusLocationRepository.
 * 
 * Tests the repository layer with real PostgreSQL (Testcontainers).
 * Focuses on persistence behavior only, not on entity/value object logic
 * (those are covered by BusTest, GpsCoordinateTest).
 * 
 * Test approach:
 * - Minimal: only test what the repository is responsible for
 * - Real DB: detect mapping issues, constraint violations
 * - Shared fixtures: company + buses loaded from test-data/
 */
@Sql({
    "/test-data/fixtures-shared.sql",
    "/test-data/tracking-fixtures.sql"
})
class BusLocationRepositoryTest extends RepositoryIntegrationTest {

    @Autowired
    private BusLocationJpaRepository busLocationJpaRepository;

    private BusLocationRepositoryImpl repository;
    
    // Fixed UUIDs from tracking-fixtures.sql
    private static final UUID BUS_ID_1 = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");
    private static final UUID BUS_ID_2 = UUID.fromString("650e8400-e29b-41d4-a716-446655440002");

    @BeforeEach
    void setUp() {
        repository = new BusLocationRepositoryImpl(busLocationJpaRepository);
        // Test data (company + buses) loaded from tracking-fixtures.sql automatically
    }

    // =========================================================
    // Save (INSERT)
    // =========================================================

    @Test
    void shouldSaveBusLocationWhenItDoesNotExist() {
        // Arrange
        GpsCoordinate coordinate = new GpsCoordinate(
            new BigDecimal("9.934739"),
            new BigDecimal("-84.087502")
        );
        BusLocation location = new BusLocation(BUS_ID_1, coordinate, LocalDateTime.now());

        // Act
        repository.save(location);

        // Assert
        Optional<BusLocation> found = repository.findByBusId(BUS_ID_1);
        assertTrue(found.isPresent());
        assertEquals(BUS_ID_1, found.get().getBusId());
        assertEquals(coordinate.getLat(), found.get().getGpsCoordinate().getLat());
        assertEquals(coordinate.getLng(), found.get().getGpsCoordinate().getLng());
    }

    // =========================================================
    // Save (UPSERT - update existing)
    // =========================================================

    @Test
    void shouldUpdateBusLocationWhenItAlreadyExists() {
        // Arrange: Insert first location
        BusLocation firstLocation = new BusLocation(
            BUS_ID_2,
            new GpsCoordinate(new BigDecimal("9.934739"), new BigDecimal("-84.087502")),
            LocalDateTime.now()
        );
        repository.save(firstLocation);

        // Update with second location
        GpsCoordinate newCoordinate = new GpsCoordinate(
            new BigDecimal("10.000000"),
            new BigDecimal("-85.000000")
        );
        BusLocation updatedLocation = new BusLocation(BUS_ID_2, newCoordinate, LocalDateTime.now());

        // Act
        repository.save(updatedLocation);

        // Assert
        Optional<BusLocation> found = repository.findByBusId(BUS_ID_2);
        assertTrue(found.isPresent());
        assertEquals(newCoordinate.getLat(), found.get().getGpsCoordinate().getLat());
        assertEquals(newCoordinate.getLng(), found.get().getGpsCoordinate().getLng());
    }

    // =========================================================
    // FindByBusId (non-existent bus)
    // =========================================================

    @Test
    void shouldReturnEmptyForNonExistentBus() {
        // Arrange
        UUID nonExistentBusId = UUID.randomUUID();

        // Act
        Optional<BusLocation> found = repository.findByBusId(nonExistentBusId);

        // Assert
        assertTrue(found.isEmpty());
    }
}
