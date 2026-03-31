package com.bustracking.tracking.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.shared.valueobjects.GpsCoordinate;
import com.bustracking.tracking.domain.model.BusLocation;
import com.bustracking.tracking.domain.repository.BusLocationRepository;

/**
 * This class:
 * - Receives data from the PWA (busId, latitude, longitude, timestamp)
 * - Validates if the bus exist
 * - Save the net location of the bus
 */



public class UpdateBusLocationCause{
    
    private final BusLocationRepository busLocationRepository;

    public UpdateBusLocationCause(BusLocationRepository busLocationRepository) {
        this.busLocationRepository = busLocationRepository;
    }

    public void execute(UUID busId, BigDecimal lat, BigDecimal lng) {

        var doesExist = busLocationRepository.findByBusId(busId);

        if (doesExist == null || doesExist.isEmpty()) {
            throw new NotFoundException(ErrorCode.BUS_NOT_FOUND, "Bus with ID " + busId + " does not exist");
        }
        
        GpsCoordinate coordinate = new GpsCoordinate(lat,lng);

        BusLocation busLocation = new BusLocation(busId, coordinate, LocalDateTime.now());

        busLocationRepository.save(busLocation);
    }

}
