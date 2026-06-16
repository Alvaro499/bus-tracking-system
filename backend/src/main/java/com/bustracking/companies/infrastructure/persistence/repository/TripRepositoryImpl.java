package com.bustracking.companies.infrastructure.persistence.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.bustracking.companies.domain.dto.TripScheduleProjection;
import com.bustracking.companies.domain.dto.TripStopDetailProjection;
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

    @Override
    public Optional<Trip> findById(UUID tripId) {
        return tripJpaRepository.findById(tripId).map(r -> new Trip(
            r.getId(),
            r.getScheduleId(),
            r.getBusId(),
            r.getTripDate(),
            r.getCancellationReason(),
            r.getStatus(),
            r.getActualStartTime(),
            r.getActualEndTime(),
            r.getDelayMinutes(),
            r.getAssignedAt(),
            r.getCreatedAt(),
            r.getUpdatedAt()
        ));
    }

    
    @Override
    public Optional<TripScheduleProjection> findTripScheduleById(UUID tripId) {

        return tripJpaRepository.findTripScheduleById(tripId)
            .map(r -> new TripScheduleProjection(
                r.id(),
                r.routeName(),
                r.origin(),
                r.destination(),
                r.departureTime(),
                r.status()
            ));
    }

    @Override
    public List<TripStopDetailProjection> findStopsByTripId(UUID tripId) {
        return tripJpaRepository.findStopsByTripId(tripId)
            .stream()
            .map(r -> new TripStopDetailProjection(
                r.routeStopId(),
                r.stopId(),
                r.stopName(),
                r.stopLat(),
                r.stopLng(),
                r.stopReference(),
                r.orderIndex(),
                r.estimatedTimeOffset(),
                r.completedAt()
            ))
            .toList();
    }

    @Override
    public Trip save(Trip trip) {
        TripJpa tripJpa = toJpa(trip);
        TripJpa saved = tripJpaRepository.save(tripJpa);
        return toDomain(saved);
    }

    private Trip toDomain(TripJpa jpa) {
        return new Trip(
            jpa.getId(),
            jpa.getScheduleId(),
            jpa.getBusId(),
            jpa.getTripDate(),
            jpa.getCancellationReason(),
            jpa.getStatus(),
            jpa.getActualStartTime(),
            jpa.getActualEndTime(),
            jpa.getDelayMinutes(),
            jpa.getAssignedAt(),
            jpa.getCreatedAt(),
            jpa.getUpdatedAt()
        );
    }

    private TripJpa toJpa(Trip trip) {
        return new TripJpa(
            trip.getId(),
            trip.getScheduleId(),
            trip.getBusId(),
            trip.getTripDate(),
            trip.getCancellationReason(),
            trip.getStatus(),
            trip.getActualStartTime(),
            trip.getActualEndTime(),
            trip.getDelayMinutes(),
            trip.getAssignedAt(),
            trip.getCreatedAt(),
            trip.getUpdatedAt()
        );
    }
