package com.bustracking.companies.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    private UUID id;

    private UUID routeId;

    private LocalTime departureTime;

    // 1=Monday, 7=Sunday
    private Integer dayOfWeek;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
