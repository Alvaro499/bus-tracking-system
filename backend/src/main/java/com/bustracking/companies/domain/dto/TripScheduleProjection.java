package com.bustracking.companies.domain.dto;

import java.time.LocalTime;
import java.util.UUID;

import com.bustracking.companies.domain.enums.TripStatus;


/**
 * Projection interface for fetching trip schedule details.
 */

public record TripScheduleProjection(
    UUID id,
    String routeName,
    String origin,
    String destination,
    LocalTime departureTime,
    TripStatus status
) {}