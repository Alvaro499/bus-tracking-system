package com.bustracking.companies.domain.repository;

import java.util.UUID;

public interface BusRepository {
    
    boolean existsById(UUID busId);
}