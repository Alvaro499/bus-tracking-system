package com.bustracking.companies.infrastructure.persistence.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bustracking.companies.infrastructure.persistence.entity.TripJpa;
import com.bustracking.companies.domain.dto.TripScheduleProjection;

public interface TripJpaRepository extends JpaRepository<TripJpa, UUID> {

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
        @Param("today") LocalDate today
    );
}