package com.bustracking.tracking.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.tracking.domain.contract.BusExistsById;
import com.bustracking.tracking.domain.contract.GetTripDetail;
import com.bustracking.tracking.domain.model.TripDetailView;

@Service
public class GetTripDetailUseCase {

    private final BusExistsById busExistsById;
    private final GetTripDetail getTripDetail;

    public GetTripDetailUseCase(BusExistsById busExistsById, GetTripDetail getTripDetail) {
        this.busExistsById = busExistsById;
        this.getTripDetail = getTripDetail;
    }

    public TripDetailView execute(UUID busId, UUID tripId) {
        if (!busExistsById.check(busId)) {
            throw new NotFoundException(
                ErrorCode.BUS_NOT_FOUND,
                "Bus not found",
                "Bus with ID " + busId + " does not exist"
            );
        }

        return getTripDetail.execute(tripId);
    }

}
