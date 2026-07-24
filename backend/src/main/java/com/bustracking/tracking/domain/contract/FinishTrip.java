package com.bustracking.tracking.domain.contract;

import java.util.UUID;

import com.bustracking.tracking.domain.model.TripFinishView;

@FunctionalInterface
public interface FinishTrip {
    TripFinishView execute(UUID tripId);
}
