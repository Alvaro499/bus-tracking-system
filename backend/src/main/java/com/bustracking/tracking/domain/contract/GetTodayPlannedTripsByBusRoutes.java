package com.bustracking.tracking.domain.contract;

import java.util.List;
import java.util.UUID;

import com.bustracking.tracking.domain.model.TripView;

/**
 * Contract for retrieving today's planned trips for a specific bus based on its routes.
 * This contract is implemented by the Companies module, which provides the actual data retrieval logic.
 */

public interface GetTodayPlannedTripsByBusRoutes {
    List<TripView> execute(UUID busId);
}