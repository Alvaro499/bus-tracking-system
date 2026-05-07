package com.bustracking.tracking.domain.contract;

import java.util.List;
import java.util.UUID;

import com.bustracking.tracking.domain.model.TripView;

public interface GetTodayPlannedTripsByBusRoutes {
    List<TripView> execute(UUID busId);
}