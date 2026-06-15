package com.bustracking.companies.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TripStopDetailProjection(
    UUID routeStopId,
    UUID stopId,
    String stopName,
    BigDecimal stopLat,
    BigDecimal stopLng,
    String stopReference,
    Integer orderIndex,
    Integer estimatedTimeOffset,
    LocalDateTime completedAt
) {}
