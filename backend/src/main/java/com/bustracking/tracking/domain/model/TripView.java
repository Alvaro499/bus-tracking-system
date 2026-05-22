package com.bustracking.tracking.domain.model;

import java.time.LocalTime;
import java.util.UUID;

public class TripView {

    private final UUID id;
    private final String routeName;
    private final String origin;
    private final String destination;
    private final LocalTime departureTime;
    private final String status;

    public TripView(UUID id, String routeName, String origin,
                    String destination, LocalTime departureTime, String status) {
        this.id = id;
        this.routeName = routeName;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.status = status;
    }

    public UUID getId() { return id; }
    public String getRouteName() { return routeName; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public LocalTime getDepartureTime() { return departureTime; }
    public String getStatus() { return status; }
}