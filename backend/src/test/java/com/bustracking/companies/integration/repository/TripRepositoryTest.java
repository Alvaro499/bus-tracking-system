package com.bustracking.companies.integration.repository;

import static com.bustracking.shared.testinfrastructure.TestSqlScripts.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import com.bustracking.companies.domain.dto.TripScheduleProjection;
import com.bustracking.companies.domain.dto.TripStopDetailProjection;
import com.bustracking.companies.infrastructure.persistence.repository.TripJpaRepository;
import com.bustracking.companies.infrastructure.persistence.repository.TripRepositoryImpl;
import com.bustracking.shared.testinfrastructure.RepositoryIntegrationTest;

@Sql(scripts = { CLEANUP }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
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

    @Nested
    @Sql(scripts = { BASE, TRIP_COMMON, PLANNED_TRIPS, TRIP_FILTER_DATA })
    class FindTodayPlannedTripsByBusRoutes {

        // =========================================================
        // findTodayPlannedTripsByBusRoutes — Happy Path
        // =========================================================

        @Test
        void shouldReturnPlannedTripsForToday_WhenBusHasRoutes() {
            List<TripScheduleProjection> result = repository.findTodayPlannedTripsByBusRoutes(BUS_ID_1);

            assertFalse(result.isEmpty());
            assertEquals("Cartago-Orosi", result.get(0).routeName());
            assertEquals("Cartago", result.get(0).origin());
            assertEquals("Orosi", result.get(0).destination());
            assertEquals("PLANNED", result.get(0).status().name());
        }

        @Test
        void shouldReturnEmptyList_WhenBusHasNoTripsToday() {
            // BUS_ID_2 exists in tracking-base.sql but has no routes or trips
            List<TripScheduleProjection> result = repository.findTodayPlannedTripsByBusRoutes(BUS_ID_2);

            assertTrue(result.isEmpty());
        }

        // =========================================================
        // findTodayPlannedTripsByBusRoutes — Where Clauses
        // =========================================================

        @Test
        void shouldNotReturnTripsFromOtherBusRoutes() {
            List<TripScheduleProjection> result = repository.findTodayPlannedTripsByBusRoutes(UUID.randomUUID());

            assertTrue(result.isEmpty());
        }

        @Test
        void shouldNotReturnTripsWithStatusInProgress() {
            // IN_PROGRESS trip loaded by tracking-trips.sql (filter test data section)
            List<TripScheduleProjection> result = repository.findTodayPlannedTripsByBusRoutes(BUS_ID_1);

            assertFalse(result.isEmpty());
            result.forEach(trip -> assertEquals("PLANNED", trip.status().name()));
        }

        @Test
        void shouldNotReturnTripsFromYesterday() {
            // Yesterday PLANNED trip loaded by tracking-trips.sql (filter test data
            // section)
            List<TripScheduleProjection> result = repository.findTodayPlannedTripsByBusRoutes(BUS_ID_1);

            assertFalse(result.isEmpty());
            result.forEach(trip -> assertEquals("PLANNED", trip.status().name()));
        }

    }

    // =========================================================
    // findTripScheduleById
    // =========================================================
    @Nested
    @Sql(scripts = { BASE, TRIP_COMMON, TRIP_DETAIL })
    class FindTripScheduleById {

        private static final UUID TRIP_DETAIL_ID = UUID.fromString("b70e8400-e29b-41d4-a716-446655449999");

        // =========================================================
        // findTripScheduleById — Happy Path
        // =========================================================
        @Test
        void shouldReturnTrip_WhenIdExists() {
            // Arrange

            // Act
            Optional<TripScheduleProjection> result = repository.findTripScheduleById(TRIP_DETAIL_ID);

            // Assert
            assertTrue(result.isPresent());
            TripScheduleProjection trip = result.get();
            assertEquals("Cartago-Orosi", trip.routeName());
            assertEquals("Cartago", trip.origin());
            assertEquals("Orosi", trip.destination());
            assertEquals(LocalTime.of(15, 0), trip.departureTime());
            assertEquals("IN_PROGRESS", trip.status().name());
        }

        // =========================================================
        // findTripScheduleById — Where Clauses
        // =========================================================

        @Test
        void shouldReturnEmptyOptional_WhenIdDoesNotExist() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();

            // Act
            Optional<TripScheduleProjection> result = repository.findTripScheduleById(nonExistentId);

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    // =========================================================
    // findStopsByTripId
    // =========================================================
    @Nested
    @Sql(scripts = { BASE, TRIP_COMMON, TRIP_DETAIL })
    class FindStopsByTripId {

        private static final UUID TRIP_DETAIL_ID = UUID.fromString("b70e8400-e29b-41d4-a716-446655449999");

        @Test
        void shouldReturnStopsOrderedByIndex_WhenTripExists() {

            // Act
            List<TripStopDetailProjection> stops = repository.findStopsByTripId(TRIP_DETAIL_ID);

            // Assert
            assertEquals(6, stops.size());

            // Verificar orden por orderIndex
            assertEquals(1, stops.get(0).orderIndex());
            assertEquals(2, stops.get(1).orderIndex());
            assertEquals(3, stops.get(2).orderIndex());
            assertEquals(4, stops.get(3).orderIndex());
            assertEquals(5, stops.get(4).orderIndex());
            assertEquals(6, stops.get(5).orderIndex());

            // Nombres esperados (los 6, en orden)
            assertEquals("Cartago", stops.get(0).stopName());
            assertEquals("Colegio Vicente", stops.get(1).stopName());
            assertEquals("Estado de Paraiso", stops.get(2).stopName());
            assertEquals("Paraiso Centro", stops.get(3).stopName());
            assertEquals("Recinto UCR Paraiso", stops.get(4).stopName());
            assertEquals("Orosi", stops.get(5).stopName());
        }

        @Test
        void shouldReturnEmptyList_WhenTripDoesNotExist() {
            // Act
            List<TripStopDetailProjection> stops = repository.findStopsByTripId(UUID.randomUUID());

            // Assert
            assertNotNull(stops);
            assertTrue(stops.isEmpty());
        }

        // =========================================================
        // Original method shpuld return all stops from a trip, wether completed (not
        // null) or not (null)

        @Test
        void shouldReturnCompletedAtNotNull_WhenStopCompleted() {
            List<TripStopDetailProjection> stops = repository.findStopsByTripId(TRIP_DETAIL_ID);

            // Las paradas con orderIndex 1 y 3 tienen completedAt no nulo
            assertNotNull(stops.get(0).completedAt()); // Cartago
            assertNotNull(stops.get(2).completedAt()); // Estado de Paraiso
        }

        @Test
        void shouldReturnCompletedAtNull_WhenStopNotCompleted() {
            List<TripStopDetailProjection> stops = repository.findStopsByTripId(TRIP_DETAIL_ID);

            // Las paradas con orderIndex 2, 4, 5 y 6 tienen completedAt nulo
            assertNull(stops.get(1).completedAt()); // Colegio Vicente
            assertNull(stops.get(3).completedAt()); // Paraiso Centro
            assertNull(stops.get(4).completedAt()); // Recinto UCR Paraiso
            assertNull(stops.get(5).completedAt()); // Orosi
        }
    }

}