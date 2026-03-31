package com.bustracking.tracking.domain.repository;

import java.util.UUID;

// Contract to define what tracking needs about buses, this is used to validate if the bus exists before updating its location
// without coupling the tracking module with the bus management module, 

public interface BusValidationRepository {
    boolean existsById(UUID busId);
}