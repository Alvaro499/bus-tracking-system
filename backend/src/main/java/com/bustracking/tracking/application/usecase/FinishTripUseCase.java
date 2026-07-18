package com.bustracking.tracking.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.tracking.domain.contract.BusExistsById;
import com.bustracking.tracking.domain.contract.FinishTrip;
import com.bustracking.tracking.domain.model.TripFinishView;

@Service
public class FinishTripUseCase {

    private final BusExistsById busExistsById;
    private final FinishTrip finishTrip;

    public FinishTripUseCase(BusExistsById busExistsById, FinishTrip finishTrip) {
        this.busExistsById = busExistsById;
        this.finishTrip = finishTrip;
    }

    public TripFinishView execute(UUID tripId, UUID busId) {
        if (!busExistsById.check(busId)) {
            throw new NotFoundException(
                    ErrorCode.BUS_NOT_FOUND,
                    "Bus not found",
                    "Bus with ID " + busId + " does not exist");
        }
        return finishTrip.execute(tripId);
    }
}