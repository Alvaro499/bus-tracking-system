package com.bustracking.tracking.application.usecase;

import java.util.List;
import java.util.UUID;

import com.bustracking.tracking.domain.contract.BusExistsById;
import com.bustracking.tracking.domain.contract.GetTodayPlannedTripsByBusRoutes;
import com.bustracking.tracking.domain.model.TripView;

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
            throw new IllegalArgumentException("Bus with ID " + busId + " does not exist.");
        }

        // Companies module by using the contract returns the list of today's planned trips for the current bus
        return getTodayTripsDelegate.execute(busId);
    }

}
