package com.bustracking.companies.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

 import com.bustracking.companies.infrastructure.persistence.entity.BusRouteJpa;

@Repository
public interface BusRouteJpaRepository extends JpaRepository<BusRouteJpa, UUID> {

    List<UUID> findRouteIdsByBusId(UUID busId);
}