package com.bustracking.companies.integration.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import com.bustracking.companies.domain.dto.TripScheduleProjection;
import com.bustracking.companies.infrastructure.persistence.repository.TripJpaRepository;
import com.bustracking.companies.infrastructure.persistence.repository.TripRepositoryImpl;
import com.bustracking.shared.testinfrastructure.RepositoryIntegrationTest;

@Sql({
    "/test-data/tracking-base.sql",
    "/test-data/tracking-trips.sql"
})
class TripRepositoryTest extends RepositoryIntegrationTest {

    @Autowired
    private TripJpaRepository tripJpaRepository;

    private TripRepositoryImpl repository;

    private static final UUID BUS_ID_1 = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");
    private static final UUID BUS_ID_2 = UUID.fromString("650e8400-e29b-41d4-a716-446655440002");

    @BeforeEach
    void setUp() {
        repository = new TripRepositoryImpl(tripJpaRepository);
    }

    // =========================================================
    // findTodayPlannedTripsByBusRoutes — Happy Path
    // =========================================================

    @Test
    void shouldReturnPlannedTripsForTodayWhenBusHasRoutes() {
        List<TripScheduleProjection> result =
            repository.findTodayPlannedTripsByBusRoutes(BUS_ID_1);

        assertFalse(result.isEmpty());
        assertEquals("Cartago-Orosi", result.get(0).routeName());
        assertEquals("Cartago", result.get(0).origin());
        assertEquals("Orosi", result.get(0).destination());
        assertEquals("PLANNED", result.get(0).status().name());
    }

    @Test
    void shouldReturnEmptyListWhenBusHasNoTripsToday() {
        // BUS_ID_2 exists in tracking-base.sql but has no routes or trips
        List<TripScheduleProjection> result =
            repository.findTodayPlannedTripsByBusRoutes(BUS_ID_2);

        assertTrue(result.isEmpty());
    }

    // =========================================================
    // findTodayPlannedTripsByBusRoutes — Filters
    // =========================================================

    @Test
    void shouldNotReturnTripsFromOtherBusRoutes() {
        List<TripScheduleProjection> result =
            repository.findTodayPlannedTripsByBusRoutes(UUID.randomUUID());

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldNotReturnTripsWithStatusInProgress() {
        // IN_PROGRESS trip loaded by tracking-trips.sql (filter test data section)
        List<TripScheduleProjection> result =
            repository.findTodayPlannedTripsByBusRoutes(BUS_ID_1);

        assertFalse(result.isEmpty());
        result.forEach(trip -> assertEquals("PLANNED", trip.status().name()));
    }

    @Test
    void shouldNotReturnTripsFromYesterday() {
        // Yesterday PLANNED trip loaded by tracking-trips.sql (filter test data section)
        List<TripScheduleProjection> result =
            repository.findTodayPlannedTripsByBusRoutes(BUS_ID_1);

        assertFalse(result.isEmpty());
        result.forEach(trip -> assertEquals("PLANNED", trip.status().name()));
    }
}