package com.bustracking.tracking.infrastructure.web.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record TripResponse(
    UUID id,
    UUID scheduleId,
    UUID busId,
    LocalDate tripDate,
    String status,
    LocalTime actualStartTime,
    LocalTime actualEndTime
) {}