package com.bustracking.companies.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bustracking.companies.domain.repository.BusRepository;
import com.bustracking.companies.domain.repository.TripRepository;
import com.bustracking.tracking.domain.contract.BusExistsById;
import com.bustracking.tracking.domain.contract.GetTodayPlannedTripsByBusRoutes;
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

    // Importing TripView is not a problem because it's a simple DTO used for data transfer between modules.
    // The tracking module only knows about TripView, not the internal Trip entity or JPA
    @Bean
    public GetTodayPlannedTripsByBusRoutes getTodayPlannedTripsByBusRoutes() {
        return busId -> tripRepository.findTodayPlannedTripsByBusRoutes(busId)
            .stream()
            .map(trip -> new TripView(
                trip.getId(),
                trip.getScheduleId(),
                trip.getBusId(),
                trip.getTripDate(),
                trip.getStatus().name(), // TripStatus enum → String
                trip.getActualStartTime(),
                trip.getActualEndTime()
            ))
            .toList();
    }
}