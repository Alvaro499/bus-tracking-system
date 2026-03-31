package com.bustracking.tracking.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bustracking.tracking.infrastructure.persistence.entity.BusLocationJpa;

public interface BusLocationJpaRepository extends JpaRepository<BusLocationJpa, UUID> {

}