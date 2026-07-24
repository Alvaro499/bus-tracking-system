package com.bustracking.companies.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bustracking.companies.infrastructure.persistence.entity.ScheduleJpa;

@Repository
public interface ScheduleJpaRepository extends JpaRepository<ScheduleJpa, UUID>{
    
}
