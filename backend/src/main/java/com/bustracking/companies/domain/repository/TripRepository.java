package com.bustracking.companies.domain.repository;

import java.util.List;
import java.util.UUID;

import com.bustracking.companies.domain.dto.TripScheduleProjection;


public interface TripRepository {
    List<TripScheduleProjection> findTodayPlannedTripsByBusRoutes(UUID busId);
}