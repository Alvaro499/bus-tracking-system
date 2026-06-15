package com.bustracking.tracking.domain.model;

import java.util.List;

public class TripDetailView {
    private final TripView trip;
    private final List<TripStopDetailView> stops;

    // constructor con todos los campos
    public TripDetailView(TripView trip, List<TripStopDetailView> stops) {
        this.trip = trip;
        this.stops = stops;
    }

    // getters
    public TripView getTrip() {
        return trip;
    }

    public List<TripStopDetailView> getStops() {
        return stops;
    }
}