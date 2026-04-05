package com.bustracking.tracking.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.tracking.domain.contract.BusExistsById;
import com.bustracking.tracking.domain.model.BusLocation;
import com.bustracking.tracking.domain.repository.BusLocationRepository;

/**
 * Use Case: Get Bus Location
 * 
 * Responsibilities:
 * - Validates if the bus exists (via delegate)
 * - Retrieves the current location of a bus
 * - Throws not found if bus has no location data
 * 
 * Dependencies:
 * - BusExistsById: Delegate from companies module (only does one thing)
 * - BusLocationRepository: Internal repository for tracking data
 */
@Service
public class GetBusLocationUseCase {

    private final BusLocationRepository busLocationRepository;
    private final BusExistsById busExistsById;

    public GetBusLocationUseCase(
        BusLocationRepository busLocationRepository,
        BusExistsById busExistsById
    ) {
        this.busLocationRepository = busLocationRepository;
        this.busExistsById = busExistsById;
    }

    public BusLocation execute(UUID busId) {

        if (!busExistsById.check(busId)) {
            throw new NotFoundException(
                ErrorCode.BUS_NOT_FOUND,
                "Bus not found",
                "Bus with ID " + busId + " does not exist"
            );
        }

        // we receive an optional, so we validate if the bus location exists, if not we throw an exception
        BusLocation location = busLocationRepository.findByBusId(busId).orElse(null);
        
        if (location == null) {
            throw new NotFoundException(
                ErrorCode.BUS_LOCATION_NOT_FOUND,
                "Bus location not found",
                "Bus with ID " + busId + " has no location registered yet"
            );
        }

        return location;
    }

}