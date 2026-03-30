package com.bustracking.tracking.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.bustracking.shared.valueobjects.GpsCoordinate;

/**
 * Domain Model that represents the GPS location of a bus.
 * 
 * Contains essential real-time location information,
 * including coordinates and the timestamp of the last update.
 * This model is independent of persistence.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusLocation {

    private UUID busId;
    private GpsCoordinate gpsCoordinate;
    private LocalDateTime updatedAt;
}
