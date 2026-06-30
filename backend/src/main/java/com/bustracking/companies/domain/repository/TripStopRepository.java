package com.bustracking.companies.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.bustracking.companies.domain.model.TripStop;

public interface TripStopRepository {

    //We need routeStopId 
    Optional<TripStop> findByTripIdAndRouteStopId(UUID tripId, UUID routeStopId);
    TripStop save(TripStop tripStop);

}
