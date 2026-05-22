package com.bustracking.companies.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.bustracking.companies.domain.repository.BusRouteRepository;

@Repository
public class BusRouteRepositoryImpl implements BusRouteRepository {

    private final BusRouteJpaRepository busRouteJpaRepository;

    public BusRouteRepositoryImpl(BusRouteJpaRepository busRouteJpaRepository) {
        this.busRouteJpaRepository = busRouteJpaRepository;
    }

    @Override
    public List<UUID> findRouteIdsByBusId(UUID busId) {
        return busRouteJpaRepository.findRouteIdsByBusId(busId);
    }
}