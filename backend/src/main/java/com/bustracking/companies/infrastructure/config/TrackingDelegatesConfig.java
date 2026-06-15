package com.bustracking.companies.infrastructure.config;

import java.util.Optional;
import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bustracking.companies.domain.dto.TripScheduleProjection;
import com.bustracking.companies.domain.dto.TripStopDetailProjection;
import com.bustracking.companies.domain.model.Trip;
import com.bustracking.companies.domain.repository.BusRepository;
import com.bustracking.companies.domain.repository.TripRepository;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.tracking.domain.contract.BusExistsById;
import com.bustracking.tracking.domain.contract.GetTodayPlannedTripsByBusRoutes;
import com.bustracking.tracking.domain.contract.GetTripDetail;
import com.bustracking.tracking.domain.contract.StartTrip;
import com.bustracking.tracking.domain.model.TripView;
import com.bustracking.tracking.domain.model.TripDetailView;
import com.bustracking.tracking.domain.model.TripStopDetailView;
import com.bustracking.tracking.domain.model.RouteStopView;
import com.bustracking.tracking.domain.model.StopView;

/**
 * Configuration for Tracking Module Delegates
 * 
 * This class is responsible for wiring the delegates (functional contracts)
 * that the tracking module requires. It acts as a bridge between modules:
 * - Companies provides the implementation (via Spring Data repositories)
 * - Tracking receives the behavior as a functional interface
 * 
 * All cross-module delegation is centralized here, making dependencies explicit
 * and preventing tight coupling.
 * 
 * By using delegates instead of full repository interfaces, we:
 * - Keep contracts minimal (single responsibility)
 * - Prevent garbage method accumulation in interfaces
 * - Make dependencies clear (tracking needs "does bus exist?", nothing more)
 */
@Configuration
public class TrackingDelegatesConfig {

    private final BusRepository busRepository;
    private final TripRepository tripRepository;


    public TrackingDelegatesConfig(BusRepository busRepository, TripRepository tripRepository) {
        this.busRepository = busRepository;
        this.tripRepository = tripRepository;
    }   

    /**
     * Provides a delegate for checking if a bus exists
     * 
     * This bean is injected into the tracking module's use cases.
     * Companies module keeps full control of the logic (is the bus active?
     * does it belong to an active company? etc).
     * Tracking module only knows "bus exists or not".
     * 
     * @return a function that checks bus existence by ID
     */
    @Bean
    public BusExistsById busExistsById() {
        // Method reference: delegates to the repository's existsById method
        // 
        // This is equivalent to:
        // BusExistsById delegate = (UUID busId) -> busRepository.existsById(busId);
        return busRepository::existsById;
    }

    // Importing TripView is not a problem because it's a simple DTO used for data transfer between modules.
    // The tracking module only knows about TripView, not the internal Trip entity or JPA
    @Bean
    public GetTodayPlannedTripsByBusRoutes getTodayPlannedTripsByBusRoutes() {
        return busId -> tripRepository.findTodayPlannedTripsByBusRoutes(busId)
            .stream()
            .map(result -> new TripView(
                result.id(),
                result.routeName(),
                result.origin(),
                result.destination(),
                result.departureTime(),
                result.status().name()
            ))
            .toList();
    }

    @Bean 
    public StartTrip startTrip() {
        // We use lambda expression avoiding creating an unneessary class.
        return (tripId, busId) -> {
            Optional<Trip> optionalTrip = tripRepository.findById(tripId);

            if(optionalTrip.isEmpty()) {
                throw new NotFoundException(
                    ErrorCode.TRIP_NOT_FOUND,
                    "Trip not found",
                    "Trip with ID " + tripId + " not found for Bus ID " + busId);
            }
            // We extract the Trip entity
            Trip trip = optionalTrip.get();
            // We use trip domain logic to start the trip
            trip.start(busId);
            // We save the updated trip back to the repository
            tripRepository.save(trip);
        };
    }

    @Bean
    public GetTripDetail getTripDetail(TripRepository tripRepository) {
        return new GetTripDetail() {
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
        };
    }
}