package com.bustracking.tracking.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.shared.valueobjects.GpsCoordinate;
import com.bustracking.tracking.domain.contract.BusExistsById;
import com.bustracking.tracking.domain.model.BusLocation;
import com.bustracking.tracking.domain.repository.BusLocationRepository;

/**
 * Use Case: Update Bus Location
 * 
 * Responsibilities:
 * - Receives data from the PWA (busId, latitude, longitude, timestamp)
 * - Validates if the bus exists (via delegate)
 * - Saves the bus location
 * 
 * Dependencies:
 * - BusExistsById: Delegate from companies module (only does one thing)
 * - BusLocationRepository: Internal repository for tracking data
 * 
 * Note: We depend on a delegate (single responsibility) instead of a full
 * repository interface. This prevents the contract from growing with unrelated methods.
 */
@Service
public class UpdateBusLocationUseCase {
    
    private final BusLocationRepository busLocationRepository;
    private final BusExistsById busExistsById;

    public UpdateBusLocationUseCase(
        BusLocationRepository busLocationRepository,
        BusExistsById busExistsById
    ) {
        this.busLocationRepository = busLocationRepository;
        this.busExistsById = busExistsById;
    }

    public void execute(UUID busId, BigDecimal lat, BigDecimal lng) {

        if (!busExistsById.check(busId)) {
            throw new NotFoundException(
                ErrorCode.BUS_NOT_FOUND,
                "Bus not found",
                "Bus with ID " + busId + " does not exist"
            );
        }

        // Always create a new BusLocation instance (UPSERT handled by repository)
        GpsCoordinate coordinate = new GpsCoordinate(lat, lng);
        BusLocation busLocation = new BusLocation(busId, coordinate, LocalDateTime.now());
        busLocationRepository.save(busLocation);
    }
}
