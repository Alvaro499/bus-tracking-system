package com.bustracking.companies.infrastructure.persistence.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.bustracking.companies.domain.model.Trip;
import com.bustracking.companies.domain.repository.TripRepository;
import com.bustracking.companies.infrastructure.persistence.entity.TripJpa;

@Repository
public class TripRepositoryImpl implements TripRepository {
 
    private final TripJpaRepository tripJpaRepository;

    public TripRepositoryImpl(TripJpaRepository tripJpaRepository) {
        this.tripJpaRepository = tripJpaRepository;
    }
    
    @Override
    public List<Trip> findTodayPlannedTripsByBusRoutes(UUID busId) {
        LocalDate today = LocalDate.now();

        List<TripJpa> tripsJpa =
            tripJpaRepository.findTodayPlannedTripsByBusRoutes(busId, today);

        List<Trip> trips = new ArrayList<>();

        for (TripJpa jpa : tripsJpa) {

            Trip trip = new Trip(
                jpa.getId(),
                jpa.getScheduleId(),
                jpa.getBusId(),
                jpa.getTripDate(),
                jpa.getStatus(),
                jpa.getActualStartTime(),
                jpa.getActualEndTime()
            );

            trips.add(trip);
        }

        return trips;
    }
}