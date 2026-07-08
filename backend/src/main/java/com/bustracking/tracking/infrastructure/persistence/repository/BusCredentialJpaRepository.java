package com.bustracking.tracking.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bustracking.tracking.infrastructure.persistence.entity.BusCredentialJpa;

public interface BusCredentialJpaRepository extends JpaRepository<BusCredentialJpa, UUID> {
    Optional<BusCredentialJpa> findByBusId(UUID busId);
}