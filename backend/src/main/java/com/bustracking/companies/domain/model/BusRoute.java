package com.bustracking.companies.domain.model;

import java.util.UUID;

public class BusRoute {
    private final UUID busId;
    private final UUID routeId;

    public BusRoute(UUID busId, UUID routeId) {
        this.busId = busId;
        this.routeId = routeId;
    }

    public UUID getBusId() {
        return busId;
    }
    public UUID getRouteId() {
        return routeId;
    }

}