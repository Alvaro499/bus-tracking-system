package com.bustracking.companies.infrastructure.config;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bustracking.companies.domain.model.Trip;
import com.bustracking.companies.domain.repository.BusRepository;
import com.bustracking.companies.domain.repository.TripRepository;
import com.bustracking.companies.infrastructure.delegate.ConfirmStopDelegate;
import com.bustracking.companies.infrastructure.delegate.FinishTripDelegate;
import com.bustracking.companies.infrastructure.delegate.TripDetailDelegate;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.tracking.domain.contract.BusExistsById;
import com.bustracking.tracking.domain.contract.ConfirmStop;
import com.bustracking.tracking.domain.contract.FinishTrip;
import com.bustracking.tracking.domain.contract.GetTodayPlannedTripsByBusRoutes;
import com.bustracking.tracking.domain.contract.GetTripDetail;
import com.bustracking.tracking.domain.contract.StartTrip;
import com.bustracking.tracking.domain.model.TripView;

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

    // Importing TripView is not a problem because it's a simple DTO used for data
    // transfer between modules.
    // The tracking module only knows about TripView, not the internal Trip entity
    // or JPA
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
                        result.status().name()))
                .toList();
    }

    @Bean
    public StartTrip startTrip() {
        // We use lambda expression avoiding creating an unneessary class.
        return (tripId, busId) -> {
            Optional<Trip> optionalTrip = tripRepository.findById(tripId);

            if (optionalTrip.isEmpty()) {
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

    // When someone ask for GetTripDetail, we return an instance of
    // TripDetailDelegate, which implements GetTripDetail interface.
    @Bean
    public GetTripDetail getTripDetail(TripDetailDelegate delegate) {
        return delegate;
    }

    @Bean
    public ConfirmStop confirmStop(ConfirmStopDelegate delegate) {
        return delegate;
    }

    @Bean
    public FinishTrip finishTrip(FinishTripDelegate delegate) {
        return delegate;
    }
}