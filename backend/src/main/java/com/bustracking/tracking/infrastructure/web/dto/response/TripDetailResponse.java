package com.bustracking.tracking.infrastructure.web.dto.response;

import java.util.List;

public record TripDetailResponse(
    TripResponse trip,
    List<TripStopDetailResponse> stops
) {}