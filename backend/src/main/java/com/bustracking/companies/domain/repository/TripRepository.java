package com.bustracking.companies.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.bustracking.companies.domain.dto.TripScheduleProjection;
import com.bustracking.companies.domain.dto.TripStopDetailProjection;
import com.bustracking.companies.domain.model.Trip;


public interface TripRepository {
    List<TripScheduleProjection> findTodayPlannedTripsByBusRoutes(UUID busId);
    Optional<Trip> findById(UUID tripId);
    Trip save(Trip trip);

    Optional<TripScheduleProjection> findTripScheduleById(UUID tripId);
    
    // A trip with no stops is also valid, so we return an empty list instead of Optional
    List<TripStopDetailProjection> findStopsByTripId(UUID tripId);
}


