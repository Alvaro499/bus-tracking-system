package com.bustracking.companies.domain.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.bustracking.companies.domain.model.Trip;


public interface TripRepository {
    List<Trip> findTodayPlannedTripsByBusRoutes(UUID busId);

}