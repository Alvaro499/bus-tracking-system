package com.bustracking.companies.infrastructure.persistence.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.bustracking.companies.domain.dto.TripScheduleProjection;
import com.bustracking.companies.domain.repository.TripRepository;

@Repository
public class TripRepositoryImpl implements TripRepository {

    private final TripJpaRepository tripJpaRepository;

    public TripRepositoryImpl(TripJpaRepository tripJpaRepository) {
        this.tripJpaRepository = tripJpaRepository;
    }

    @Override
    public List<TripScheduleProjection> findTodayPlannedTripsByBusRoutes(UUID busId) {
        return tripJpaRepository
            .findTodayPlannedTripsByBusRoutes(busId, LocalDate.now())
            .stream()
            .map(r -> new TripScheduleProjection(
                r.id(),
                r.routeName(),
                r.origin(),
                r.destination(),
                r.departureTime(),
                r.status()
            ))
            .toList();
    }
}