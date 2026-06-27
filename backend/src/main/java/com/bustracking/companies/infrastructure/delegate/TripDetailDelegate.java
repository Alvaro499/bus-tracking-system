package com.bustracking.companies.infrastructure.delegate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.bustracking.companies.domain.dto.TripScheduleProjection;
import com.bustracking.companies.domain.dto.TripStopDetailProjection;
import com.bustracking.companies.domain.repository.TripRepository;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.tracking.domain.contract.GetTripDetail;
import com.bustracking.tracking.domain.model.RouteStopView;
import com.bustracking.tracking.domain.model.StopView;
import com.bustracking.tracking.domain.model.TripDetailView;
import com.bustracking.tracking.domain.model.TripStopDetailView;
import com.bustracking.tracking.domain.model.TripView;

@Component
public class TripDetailDelegate implements GetTripDetail {

    private final TripRepository tripRepository;

    public TripDetailDelegate(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Override
    public TripDetailView execute(UUID tripId) {

        Optional<TripScheduleProjection> tripProjection = tripRepository.findTripScheduleById(tripId);
        
        if (tripProjection.isEmpty()) {
            throw new NotFoundException(
                ErrorCode.TRIP_NOT_FOUND,
                "Trip not found",
                "Trip with ID " + tripId + " does not exist"
            );
        }
        
        TripScheduleProjection tripProj = tripProjection.get();
        
        // 2. We create a TripView with the basic trip info (without stops)
        TripView tripView = new TripView(
            tripProj.id(),
            tripProj.routeName(),
            tripProj.origin(),
            tripProj.destination(),
            tripProj.departureTime(),
            tripProj.status().name()
        );
        
        // 3. We obtain the stops for the trip using the new method in the repository
        List<TripStopDetailProjection> stopProjections = tripRepository.findStopsByTripId(tripId);
        
        // 4. We convert the stops to TripStopDetailView
        List<TripStopDetailView> stops = new java.util.ArrayList<>();
        for (TripStopDetailProjection stopProj : stopProjections) {
            RouteStopView routeStop = new RouteStopView(
                stopProj.routeStopId(),
                stopProj.stopId(),
                stopProj.orderIndex(),
                stopProj.estimatedTimeOffset()
            );
            
            StopView stop = new StopView(
                stopProj.stopId(),
                stopProj.stopName(),
                stopProj.stopLat(),
                stopProj.stopLng(),
                stopProj.stopReference()
            );
            
            TripStopDetailView tripStop = new TripStopDetailView(
                routeStop,
                stop,
                stopProj.completedAt()
            );
            
            stops.add(tripStop);
        }
        // 5. We return a TripDetailView containing both the trip info and the list of stops
        return new TripDetailView(tripView, stops);
    }
}
