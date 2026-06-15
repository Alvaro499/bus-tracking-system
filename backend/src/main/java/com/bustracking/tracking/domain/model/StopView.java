package com.bustracking.tracking.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class StopView {
    private final UUID id;
    private final String name;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final String reference;

    public StopView(UUID id, String name, BigDecimal latitude, BigDecimal longitude, String reference) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reference = reference;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public String getReference() {
        return reference;
    }
}
