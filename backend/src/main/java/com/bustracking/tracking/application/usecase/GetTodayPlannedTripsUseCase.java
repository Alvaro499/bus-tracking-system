package com.bustracking.tracking.application.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.NotFoundException;
import com.bustracking.tracking.domain.contract.BusExistsById;
import com.bustracking.tracking.domain.contract.GetTodayPlannedTripsByBusRoutes;
import com.bustracking.tracking.domain.model.TripView;

@Service
public class GetTodayPlannedTripsUseCase {

    private final GetTodayPlannedTripsByBusRoutes getTodayTripsDelegate;
    private final BusExistsById busExistsById;

    public GetTodayPlannedTripsUseCase(GetTodayPlannedTripsByBusRoutes getTodayTripsDelegate,
                                BusExistsById busExistsById) {
        this.getTodayTripsDelegate = getTodayTripsDelegate;
        this.busExistsById = busExistsById;
    }

    public List<TripView> execute(UUID busId) {
        if (!busExistsById.check(busId)) {
            throw new NotFoundException(
                ErrorCode.BUS_NOT_FOUND,
                "Bus not found",
                "Bus with ID " + busId + " does not exist"
            );
        }

        // Companies module using the contract returns the list of today's planned trips for the current bus
        return getTodayTripsDelegate.execute(busId);
    }

}
