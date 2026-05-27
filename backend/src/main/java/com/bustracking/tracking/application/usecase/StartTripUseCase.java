package com.bustracking.tracking.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.tracking.domain.contract.BusExistsById;
import com.bustracking.tracking.domain.contract.StartTrip;

@Service
public class StartTripUseCase {

    private final StartTrip startTrip;
    private BusExistsById busExistsById;
    
    public StartTripUseCase(StartTrip startTrip, BusExistsById busExistsById) {
        this.startTrip = startTrip;
        this.busExistsById = busExistsById;
    }


    public void execute(UUID tripId, UUID busId){

        // We validate if the bus exists before starting the trip.
        if (!busExistsById.check(busId)) {
            throw new NotFoundException(
                ErrorCode.BUS_NOT_FOUND,
                "Bus not found",
                "Bus with ID " + busId + " does not exist"
            );
        }

        // We delegate the logic of starting the trip to company module
        startTrip.execute(tripId, busId);
    }
    
}
