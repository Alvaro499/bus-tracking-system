package com.bustracking.companies.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bustracking.companies.infrastructure.persistence.entity.BusJpa;

/**
 * Spring Data JPA Repository for Bus entity
 * 
 * Provides low-level database access for bus-related operations.
 * This repository is internal to the companies infrastructure layer.
 * 
 * Methods from JpaRepository:
 * - existsById(UUID): Check if a bus exists
 * - findById(UUID): Retrieve a bus by ID
 * - save(BusJpa): Persist a bus
 * - delete(BusJpa): Remove a bus
 * - findAll(): Get all buses
 * 
 * Other modules should NOT depend on this class directly.
 * Instead, they depend on delegates defined in TrackingDelegatesConfig.
 */
@Repository
public interface BusJpaRepository extends JpaRepository<BusJpa, UUID> {
    
}
