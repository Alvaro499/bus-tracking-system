package com.bustracking.companies.infrastructure.persistence.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bustracking.companies.infrastructure.persistence.entity.TripJpa;

public interface TripJpaRepository extends JpaRepository<TripJpa, UUID> {

    //Advanced query to find today's planned trips for a specific bus based on its routes
       @Query("""
        SELECT t FROM TripJpa t
        JOIN ScheduleJpa s ON s.id = t.scheduleId
        JOIN BusRouteJpa br ON br.routeId = s.routeId
        WHERE br.busId = :busId
        AND t.tripDate = :today
        AND t.status = 'PLANNED'
    """)
    List<TripJpa> findTodayPlannedTripsByBusRoutes(
        @Param("busId") UUID busId,
        @Param("today") LocalDate today
    );
    
}
