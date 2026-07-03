package com.bustracking.tracking.domain.repository;

import java.util.Optional;
import java.util.UUID;
import com.bustracking.tracking.domain.model.BusCredential;

public interface BusCredentialRepository {
    Optional<BusCredential> findByBusId(UUID busId);
}