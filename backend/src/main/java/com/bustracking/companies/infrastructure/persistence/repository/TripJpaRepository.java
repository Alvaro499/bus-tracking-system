package com.bustracking.companies.infrastructure.persistence.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bustracking.companies.infrastructure.persistence.entity.TripJpa;
import com.bustracking.companies.domain.dto.TripScheduleProjection;
import com.bustracking.companies.domain.dto.TripStopDetailProjection;

public interface TripJpaRepository extends JpaRepository<TripJpa, UUID> {

    /*
     * 
     * La consulta JPQL findTodayPlannedTripsByBusRoutes:
     * 
     * Filtra por br.busId = :busId → solo viajes de las rutas asignadas a ese bus.
     * 
     * Filtra por t.tripDate = :today → solo viajes del día actual.
     * 
     * Filtra por t.status = 'PLANNED' → solo viajes que aún no han comenzado
     * (disponibles para ser tomados).
     * 
     */

    @Query("SELECT new com.bustracking.companies.domain.dto.TripScheduleProjection(" +
            "t.id, r.name, r.origin, r.destination, s.departureTime, t.status) " +
            "FROM TripJpa t " +
            "JOIN ScheduleJpa s ON s.id = t.scheduleId " +
            "JOIN RouteJpa r ON r.id = s.routeId " +
            "JOIN BusRouteJpa br ON br.routeId = r.id " +
            "WHERE br.busId = :busId " +
            "AND t.tripDate = :today " +
            "AND t.status = 'PLANNED'")
    List<TripScheduleProjection> findTodayPlannedTripsByBusRoutes(
            @Param("busId") UUID busId,
            @Param("today") LocalDate today);


    @Query("SELECT new com.bustracking.companies.domain.dto.TripScheduleProjection(" +
            "t.id, r.name, r.origin, r.destination, s.departureTime, t.status) " +
            "FROM TripJpa t " +
            "JOIN ScheduleJpa s ON s.id = t.scheduleId " +
            "JOIN RouteJpa r ON r.id = s.routeId " +
            "WHERE t.id = :tripId")
    Optional<TripScheduleProjection> findTripScheduleById(@Param("tripId") UUID tripId);


    @Query("SELECT new com.bustracking.companies.domain.dto.TripStopDetailProjection(" +
       "rs.id, s.id, s.name, s.latitude, s.longitude, s.reference, " +
       "rs.orderIndex, rs.estimatedTimeOffset, ts.completedAt) " +
       "FROM RouteStopJpa rs " +
       "JOIN StopJpa s ON s.id = rs.stopId " +
       "JOIN ScheduleJpa sch ON sch.routeId = rs.routeId " +
       "JOIN TripJpa t ON t.scheduleId = sch.id " +
       "LEFT JOIN TripStopJpa ts ON ts.tripId = t.id AND ts.routeStopId = rs.id " +
       "WHERE t.id = :tripId " +
       "ORDER BY rs.orderIndex")
    List<TripStopDetailProjection> findStopsByTripId(@Param("tripId") UUID tripId);
}