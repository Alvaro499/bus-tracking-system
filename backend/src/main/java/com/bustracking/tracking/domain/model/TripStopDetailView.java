package com.bustracking.tracking.domain.model;

import java.time.LocalDateTime;

public class TripStopDetailView {
    private final RouteStopView routeStop;
    private final StopView stop;
    private final LocalDateTime completedAt;

    public TripStopDetailView(RouteStopView routeStop, StopView stop, LocalDateTime completedAt) {
        this.routeStop = routeStop;
        this.stop = stop;
        this.completedAt = completedAt;
    }

    public RouteStopView getRouteStop() {
        return routeStop;
    }

    public StopView getStop() {
        return stop;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}
