package com.bustracking.tracking.unit.mappers;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.bustracking.tracking.domain.model.*;
import com.bustracking.tracking.infrastructure.mappers.TripDetailMapper;
import com.bustracking.tracking.infrastructure.web.dto.response.*;

public class TripDetailMapperTest {

    private TripDetailMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TripDetailMapper();
    }

    // ==========================================================
    // Happy Path Tests - mapToTripDetailResponse()
    // ==========================================================

    @Test
    void shouldMapTripDetailViewToResponse_WhenTripHasMultipleStops() {
        // ─Arrange
        TripView tripView = new TripView(
                UUID.fromString("b70e8400-e29b-41d4-a716-446655440001"),
                "Ruta 300",
                "San José",
                "Cartago",
                LocalTime.of(8, 0),
                "IN_PROGRESS");

        RouteStopView routeStop1 = new RouteStopView(
                UUID.randomUUID(), UUID.randomUUID(), 1, 0);

        StopView stop1 = new StopView(
                UUID.randomUUID(), "Parada A", new BigDecimal("9.8612"),
                new BigDecimal("-83.9180"), "Frente al mercado");

        TripStopDetailView stopDetail1 = new TripStopDetailView(
                routeStop1, stop1, LocalDateTime.of(2026, 6, 24, 8, 5));

        RouteStopView routeStop2 = new RouteStopView(
                UUID.randomUUID(), UUID.randomUUID(), 2, 15);

        StopView stop2 = new StopView(
                UUID.randomUUID(), "Parada B", new BigDecimal("9.8523"),
                new BigDecimal("-83.8651"), "Iglesia de Paraíso");

        TripStopDetailView stopDetail2 = new TripStopDetailView(
                routeStop2, stop2, null); // ← parada no completada

        TripDetailView detailView = new TripDetailView(
                tripView, List.of(stopDetail1, stopDetail2));

        // Act
        TripDetailResponse response = mapper.toResponse(detailView);

        // Assert
          // Trip
        TripResponse tripResponse = response.trip();
        assertEquals(tripView.getId(), tripResponse.id());
        assertEquals(tripView.getRouteName(), tripResponse.routeName());
        assertEquals(tripView.getOrigin(), tripResponse.origin());
        assertEquals(tripView.getDestination(), tripResponse.destination());
        assertEquals(tripView.getDepartureTime(), tripResponse.departureTime());
        assertEquals(tripView.getStatus(), tripResponse.status());

                 // Stop List
        List<TripStopDetailResponse> stops = response.stops();
        assertEquals(2, stops.size());

                 // First Stop (completed)
        TripStopDetailResponse stopResponse1 = stops.get(0);
        assertEquals(routeStop1.getId(), stopResponse1.routeStop().id());
        assertEquals(routeStop1.getStopId(), stopResponse1.routeStop().stopId());
        assertEquals(routeStop1.getOrderIndex(), stopResponse1.routeStop().orderIndex());
        assertEquals(routeStop1.getEstimatedTimeOffset(), stopResponse1.routeStop().estimatedTimeOffset());
        assertEquals(stop1.getId(), stopResponse1.stop().id());
        assertEquals(stop1.getName(), stopResponse1.stop().name());
        assertEquals(stop1.getLatitude(), stopResponse1.stop().latitude());
        assertEquals(stop1.getLongitude(), stopResponse1.stop().longitude());
        assertEquals(stop1.getReference(), stopResponse1.stop().reference());
        assertEquals(LocalDateTime.of(2026, 6, 24, 8, 5), stopResponse1.completedAt());

        // Second Stop (not completed)
        TripStopDetailResponse stopResponse2 = stops.get(1);
        assertEquals(routeStop2.getId(), stopResponse2.routeStop().id());
        assertEquals(stop2.getName(), stopResponse2.stop().name());
        assertNull(stopResponse2.completedAt());
    }

    @Test
    void shouldMapEmptyStopsList_WhenTripHasNoStops() {
        // Arrange
        TripView tripView = new TripView(
                UUID.fromString("b70e8400-e29b-41d4-a716-446655440001"),
                "Ruta 100",
                "Cartago",
                "Paraíso",
                LocalTime.of(7, 30),
                "PLANNED");
        TripDetailView detailView = new TripDetailView(tripView, List.of());

        // Act
        TripDetailResponse response = mapper.toResponse(detailView);

        // Assert – Trip mapeado correctamente
        TripResponse tripResponse = response.trip();
        assertEquals(tripView.getId(), tripResponse.id());
        assertEquals(tripView.getRouteName(), tripResponse.routeName());
        assertEquals(tripView.getOrigin(), tripResponse.origin());
        assertEquals(tripView.getDestination(), tripResponse.destination());
        assertEquals(tripView.getDepartureTime(), tripResponse.departureTime());
        assertEquals(tripView.getStatus(), tripResponse.status());

        // Lista de paradas vacía
        assertNotNull(response.stops());
        assertTrue(response.stops().isEmpty());
    }

    @Test
    void shouldMapStopWithNullReference() {
        // Arrange
        TripView tripView = new TripView(
                UUID.fromString("b70e8400-e29b-41d4-a716-446655440001"),
                "Ruta 200",
                "Heredia",
                "Alajuela",
                LocalTime.of(9, 0),
                "IN_PROGRESS");

        RouteStopView routeStop = new RouteStopView(
                UUID.randomUUID(), UUID.randomUUID(), 1, 0);

        StopView stop = new StopView(
                UUID.randomUUID(), "Parada sin ref", new BigDecimal("10.0000"),
                new BigDecimal("-84.0000"), null); // ← referencia nula

        TripStopDetailView stopDetail = new TripStopDetailView(
                routeStop, stop, null);

        TripDetailView detailView = new TripDetailView(
                tripView, List.of(stopDetail));

        // Act
        TripDetailResponse response = mapper.toResponse(detailView);

        // Assert
        assertEquals(1, response.stops().size());
        TripStopDetailResponse stopResponse = response.stops().get(0);
        assertEquals(stop.getName(), stopResponse.stop().name());
        assertNull(stopResponse.stop().reference()); // se mapea como null
        assertNull(stopResponse.completedAt()); // parada sin completar
    }
}