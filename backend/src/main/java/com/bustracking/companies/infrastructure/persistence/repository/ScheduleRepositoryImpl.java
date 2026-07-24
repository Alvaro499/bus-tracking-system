package com.bustracking.companies.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.bustracking.companies.domain.model.Schedule;
import com.bustracking.companies.domain.repository.ScheduleRepository;
import com.bustracking.companies.infrastructure.persistence.entity.ScheduleJpa;

@Repository
public class ScheduleRepositoryImpl implements ScheduleRepository {

    private final ScheduleJpaRepository scheduleRepository;

    public ScheduleRepositoryImpl(ScheduleJpaRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;

    }

    @Override
    public Optional<Schedule> findById(UUID scheduleId) {
        Optional<ScheduleJpa> optionalJpa = scheduleRepository.findById(scheduleId);

        if (optionalJpa.isPresent()) {
            ScheduleJpa entity = optionalJpa.get();
            Schedule domainModel = toDomain(entity);
            return Optional.of(domainModel);
        } else {
            return Optional.empty();
        }

        //With  syntactic sugar
        //return scheduleRepository.findById(scheduleId).map(this::toDomain);
    }

    //
    private Schedule toDomain(ScheduleJpa jpa) {
        return new Schedule(
                jpa.getId(),
                jpa.getRouteId(),
                jpa.getDepartureTime(),
                jpa.getDayOfWeek(),
                jpa.getStartDate(),
                jpa.getEndDate(),
                jpa.getIsActive(),
                jpa.getCreatedAt(),
                jpa.getUpdatedAt()
        );
    }
}