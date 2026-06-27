package com.bustracking.tracking.domain.contract;

import java.util.UUID;

import com.bustracking.tracking.domain.model.TripDetailView;

public interface GetTripDetail {
    TripDetailView execute(UUID tripId);
}
