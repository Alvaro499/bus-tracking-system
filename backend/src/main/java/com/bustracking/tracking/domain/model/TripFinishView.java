package com.bustracking.tracking.domain.model;

import java.time.LocalTime;
import java.util.UUID;

public record TripFinishView(
    UUID tripId,
    String status,
    LocalTime actualStartTime,
    LocalTime actualEndTime,
    int delayMinutes
) {}
