package com.bustracking.tracking.infrastructure.web.dto.response;

import java.time.LocalTime;
import java.util.UUID;

public record TripFinishResponse(
    UUID tripId,
    String status,
    LocalTime actualStartTime,
    LocalTime actualEndTime,
    int delayMinutes
) {}