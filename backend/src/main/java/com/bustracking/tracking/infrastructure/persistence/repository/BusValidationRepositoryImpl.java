package com.bustracking.tracking.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;

import com.bustracking.tracking.domain.repository.BusValidationRepository;

public class BusValidationRepositoryImpl implements BusValidationRepository{

    private final JdbcTemplate jdbc;

    public BusValidationRepositoryImpl(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }

    @Override
    public boolean existsById(UUID busId) {
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM bus WHERE id = ?",
            Integer.class,
            busId
        );
        return count != null && count > 0;
    }

}
