package com.bustracking.companies.integration.repository;

import static com.bustracking.shared.testinfrastructure.TestSqlScripts.*;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import com.bustracking.companies.infrastructure.persistence.repository.TripStopJpaRepository;
import com.bustracking.companies.infrastructure.persistence.repository.TripStopRepositoryImpl;

@Sql(scripts = {CLEANUP}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class TripStopRepositoryTest {

    @Autowired
    private TripStopJpaRepository tripStopJpaRepository;
    private TripStopRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new TripStopRepositoryImpl(tripStopJpaRepository);
    }

}