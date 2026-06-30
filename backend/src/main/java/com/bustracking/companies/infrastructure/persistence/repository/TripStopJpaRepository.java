package com.bustracking.companies.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bustracking.companies.infrastructure.persistence.entity.TripStopJpa;

public interface TripStopJpaRepository extends JpaRepository<TripStopJpa, UUID> {
    Optional<TripStopJpa> findByTripIdAndRouteStopId(UUID tripId, UUID routeStopId);
}