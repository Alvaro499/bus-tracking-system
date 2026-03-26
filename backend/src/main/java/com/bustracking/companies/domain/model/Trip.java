package com.bustracking.companies.domain.model;

import com.bustracking.companies.domain.enums.TripStatus;
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
public class Trip {

    private UUID id;

    private UUID scheduleId;

    private UUID busId;

    private LocalDate tripDate;

    private TripStatus status;

    private LocalTime actualStartTime;

    private LocalTime actualEndTime;

    private Integer delayMinutes;

    private LocalDateTime assignedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
