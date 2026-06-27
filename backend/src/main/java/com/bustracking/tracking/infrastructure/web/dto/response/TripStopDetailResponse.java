package com.bustracking.tracking.infrastructure.web.dto.response;

import java.time.LocalDateTime;

public record TripStopDetailResponse(
    RouteStopResponse routeStop,
    StopResponse stop,
    LocalDateTime completedAt
) {}
