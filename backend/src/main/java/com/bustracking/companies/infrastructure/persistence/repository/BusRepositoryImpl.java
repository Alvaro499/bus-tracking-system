package com.bustracking.companies.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.bustracking.companies.domain.repository.BusRepository;

@Repository
public class BusRepositoryImpl implements BusRepository {

    private final BusJpaRepository busJpaRepository;

    public BusRepositoryImpl(BusJpaRepository busJpaRepository) {
        this.busJpaRepository = busJpaRepository;
    }

    @Override
    public boolean existsById(UUID busId) {
        return busJpaRepository.existsById(busId);
    }
}
