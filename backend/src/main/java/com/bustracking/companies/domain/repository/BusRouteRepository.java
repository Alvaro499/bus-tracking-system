package com.bustracking.companies.domain.repository;


import java.util.List;
import java.util.UUID;

public interface BusRouteRepository {
    List<UUID> findRouteIdsByBusId(UUID busId);
}