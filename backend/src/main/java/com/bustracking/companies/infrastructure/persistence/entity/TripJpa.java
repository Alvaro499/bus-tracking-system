package com.bustracking.companies.infrastructure.persistence.entity;

import com.bustracking.companies.domain.enums.TripStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;


@Entity
@Table(name = "trip", schema = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripJpa {

    @Id
    private UUID id;

    @Column(name = "schedule_id", nullable = false)
    private UUID scheduleId;

    @Column(name = "bus_id")
    private UUID busId;

    @Column(name = "trip_date", nullable = false)
    private LocalDate tripDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TripStatus status;

    @Column(name = "actual_start_time")
    private LocalTime actualStartTime;

    @Column(name = "actual_end_time")
    private LocalTime actualEndTime;

    @Column(name = "delay_minutes")
    private Integer delayMinutes;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TripJpa)) return false;
        return id != null && id.equals(((TripJpa) o).getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
