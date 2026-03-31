package com.bustracking.tracking.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.shared.valueobjects.GpsCoordinate;
import com.bustracking.tracking.domain.model.BusLocation;
import com.bustracking.tracking.domain.repository.BusLocationRepository;
import com.bustracking.tracking.domain.repository.BusValidationRepository;

/**
 * This class:
 * - Receives data from the PWA (busId, latitude, longitude, timestamp)
 * - Validates if the bus exist
 * - Save the net location of the bus
 */


@Service
public class UpdateBusLocationCause{
    
    private final BusLocationRepository busLocationRepository;
    private final BusValidationRepository busValidationRepository;

    public UpdateBusLocationCause(BusLocationRepository busLocationRepository, BusValidationRepository busValidationRepository) {
        this.busLocationRepository = busLocationRepository;
        this.busValidationRepository = busValidationRepository;
    }

    public void execute(UUID busId, BigDecimal lat, BigDecimal lng) {

        if (!busValidationRepository.existsById(busId)) {
            throw new NotFoundException(ErrorCode.BUS_NOT_FOUND, "Bus not found", "Bus with ID " + busId + " does not exist");
        }
        
        GpsCoordinate coordinate = new GpsCoordinate(lat,lng);

        BusLocation busLocation = new BusLocation(busId, coordinate, LocalDateTime.now());

        busLocationRepository.save(busLocation);
    }

}
