package com.bustracking.tracking.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.ValidationException;
import com.bustracking.shared.valueobjects.GpsCoordinate;

/**
 * Domain Model that represents the GPS location of a bus.
 * 
 * Contains essential real-time location information,
 * including coordinates and the timestamp of the last update.
 * This model is independent of persistence.
 * 
 * Note: This is an immutable model. Use updateLocation() to modify coordinates.
 */
@Getter
@NoArgsConstructor
public class BusLocation {

    private UUID busId;
    private GpsCoordinate gpsCoordinate;
    private LocalDateTime updatedAt;


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

    /**
     * Updates the location with new GPS coordinates.
     * This is the only way to modify a BusLocation after creation.
      */
    public void updateLocation(GpsCoordinate newCoordinate) {
        if (newCoordinate == null) {
            throw new ValidationException(
                ErrorCode.MISSING_REQUIRED_FIELD,
                "GPS Coordinate is required",
                "gpsCoordinate cannot be null in updateLocation"
            );
        }
        this.gpsCoordinate = newCoordinate;
        this.updatedAt = LocalDateTime.now();
    }

}
