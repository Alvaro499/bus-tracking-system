package com.bustracking.tracking.infrastructure.web.dto.response;

import java.time.LocalTime;
import java.util.UUID;

public record TripResponse(
    UUID id,
    String routeName,
    String origin,
    String destination,
    LocalTime departureTime,
    String status
) {}