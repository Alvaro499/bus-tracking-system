package com.bustracking.tracking.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.ValidationException;
import com.bustracking.shared.valueobjects.GpsCoordinate;

/**
 * Value Object that represents the GPS location of a bus at a specific moment.
 * 
 * Immutable by design: does not have its own identity, represents a snapshot
 * of a bus's location at a point in time. To update a location, create a new instance.
 * 
 */
@Getter
public final class BusLocation {

    private final UUID busId;
    private final GpsCoordinate gpsCoordinate;
    private final LocalDateTime updatedAt;


    public BusLocation(UUID busId, GpsCoordinate gpsCoordinate, LocalDateTime updatedAt) {
        validate(busId, gpsCoordinate, updatedAt);
        this.busId = busId;
        //already validated in the GpsCoordinate value object
        this.gpsCoordinate = gpsCoordinate;
        this.updatedAt = updatedAt;
    }

    private void validate(UUID busId, GpsCoordinate gpsCoordinate, LocalDateTime updatedAt) {
        if (busId == null) {
            throw new ValidationException(
                ErrorCode.MISSING_REQUIRED_FIELD,
                "Bus ID is required",
                "busId cannot be null in BusLocation"
            );
        }
        
        if (gpsCoordinate == null) {
            throw new ValidationException(
                ErrorCode.MISSING_REQUIRED_FIELD,
                "GPS Coordinate is required",
                "gpsCoordinate cannot be null in BusLocation"
            );
        }
        
        if (updatedAt == null) {
            throw new ValidationException(
                ErrorCode.MISSING_REQUIRED_FIELD,
                "Updated timestamp is required",
                "updatedAt cannot be null in BusLocation"
            );
        }
    }


}
