package com.bustracking.tracking.infrastructure.web.dto.response;

import java.util.UUID;

public record RouteStopResponse(
    UUID id,
    UUID stopId,
    Integer orderIndex,
    Integer estimatedTimeOffset
) {}
