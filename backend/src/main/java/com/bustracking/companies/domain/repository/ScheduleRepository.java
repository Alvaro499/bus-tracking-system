package com.bustracking.companies.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.bustracking.companies.domain.model.Schedule;

public interface ScheduleRepository {

    Optional<Schedule> findById(UUID scheduleId);
}
