package com.bustracking.tracking.application.usecase;

import java.util.UUID;

import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.tracking.domain.model.BusLocation;
import com.bustracking.tracking.domain.repository.BusLocationRepository;
import com.bustracking.tracking.domain.repository.BusValidationRepository;

public class GetBusLocationUseCause{

    private final BusLocationRepository busLocationRepository;
    private final BusValidationRepository busValidationRepository;

    public GetBusLocationUseCause(BusLocationRepository busLocationRepository, BusValidationRepository busValidationRepository) {
        this.busLocationRepository = busLocationRepository;
        this.busValidationRepository = busValidationRepository;
    }

    public BusLocation execute(UUID busId) {

        if(!busValidationRepository.existsById(busId)){
            throw new NotFoundException(ErrorCode.BUS_NOT_FOUND, "Bus not found", "Bus with ID " + busId + " does not exist");
        }

        // we receive an optional, so we validate if the bus location exists, if not we throw an exception
        BusLocation location = busLocationRepository.findByBusId(busId).orElse(null);
        
        if(location == null){
            throw new NotFoundException(ErrorCode.BUS_LOCATION_NOT_FOUND, "Bus location not found", "Bus with ID " + busId + " has no location registered yet");
        }

        return location;
    }

}