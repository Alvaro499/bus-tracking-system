package com.bustracking.shared.valueobjects;

import java.math.BigDecimal;
import java.util.Objects;

import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.ValidationException;

// This value object will be used in the BusLocation domain model, but also in Stop domain Model, so it is placed in the shared package to avoid circular dependencies between tracking and companies modules.
public final class GpsCoordinate  {

    private final BigDecimal lat;
    private final BigDecimal lng;

    public GpsCoordinate(BigDecimal lat, BigDecimal lng){
        validate(lat,lng);
        this.lat = lat;
        this.lng = lng;
    }

    private void validate(BigDecimal lat, BigDecimal lng){

        if (lat == null || lng == null){
            throw new ValidationException(ErrorCode.MISSING_REQUIRED_FIELD, "Latitude and Longitud are missing", "Latitude and Longitude cannot be null");
        }

        if(lat.compareTo(new BigDecimal("-90")) < 0 || lat.compareTo(new BigDecimal(90)) > 0){
            throw new ValidationException(ErrorCode.INVALID_INPUT, "Latitude is out of range", "Latitude must be between -90 and 90");
        }

        if (lng.compareTo(new BigDecimal("-180")) < 0 || lng.compareTo(new BigDecimal(180)) > 0){
            throw new ValidationException(ErrorCode.INVALID_INPUT, "Longitude is out of range", "Longitude must be between -180 and 180");
        }
    }

    public BigDecimal getLat() {return lat;}
    public BigDecimal getLng() {return lng;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GpsCoordinate)) return false;
        GpsCoordinate that = (GpsCoordinate) o;
        return Objects.equals(lat, that.lat) && Objects.equals(lng, that.lng);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lng);
    }

}
