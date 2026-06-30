package com.bustracking.companies.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.bustracking.companies.domain.model.TripStop;
import com.bustracking.companies.domain.repository.TripStopRepository;
import com.bustracking.companies.infrastructure.persistence.entity.TripStopJpa;

@Repository
public class TripStopRepositoryImpl implements TripStopRepository {

    private final TripStopJpaRepository jpaRepository;

    public TripStopRepositoryImpl(TripStopJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TripStop save(TripStop tripStop) {
        TripStopJpa jpa = toJpa(tripStop);
        TripStopJpa saved = jpaRepository.save(jpa);
        return toDomain(saved);
    }

    private TripStopJpa toJpa(TripStop domain) {
        return new TripStopJpa(
                domain.getId(),
                domain.getTripId(),
                domain.getRouteStopId(),
                domain.getCompletedAt());
    }

    private TripStop toDomain(TripStopJpa jpa) {
        return new TripStop(
                jpa.getId(),
                jpa.getTripId(),
                jpa.getRouteStopId(),
                jpa.getCompletedAt());
    }

    @Override
    public Optional<TripStop> findByTripIdAndRouteStopId(UUID tripId, UUID routeStopId) {
        return jpaRepository.findByTripIdAndRouteStopId(tripId, routeStopId)
                .map(this::toDomain);
    }
}
