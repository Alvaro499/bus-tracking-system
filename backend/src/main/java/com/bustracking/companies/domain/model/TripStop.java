package com.bustracking.companies.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.ValidationException;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TripStop {

    private UUID id;
    private UUID tripId;
    private UUID routeStopId;
    private LocalDateTime completedAt;

    public TripStop(UUID tripId, UUID routeStopId) {
        validateTripId(tripId);
        validateRouteStopId(routeStopId);
        this.id = UUID.randomUUID();
        this.tripId = tripId;
        this.routeStopId = routeStopId;
        this.completedAt = null;
    }

    // VALIDATION METHODS

    private void validateTripId(UUID tripId) {
        if (tripId == null) {
            throw new ValidationException(
                    ErrorCode.MISSING_REQUIRED_FIELD,
                    "Trip ID is required",
                    "Cannot create a TripStop without a tripId");
        }
    }

    private void validateRouteStopId(UUID routeStopId) {
        if (routeStopId == null) {
            throw new ValidationException(
                    ErrorCode.MISSING_REQUIRED_FIELD,
                    "Route stop ID is required",
                    "Cannot create a TripStop without a routeStopId");
        }
    }

    // Business Rule Methods
    
    public void markCompleted() {
        if (completedAt != null) {
            throw new BusinessRuleException(
                    ErrorCode.INVALID_STATE,
                    "Stop already completed",
                    "Cannot mark a stop as completed if it has already been completed");
        }
        this.completedAt = LocalDateTime.now();
    }
}