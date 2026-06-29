package com.bustracking.companies.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "trip_stop", schema = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripStopJpa {
    @Id
    private UUID id;

    @Column(name = "trip_id", nullable = false)
    private UUID tripId;

    @Column(name = "route_stop_id", nullable = false)
    private UUID routeStopId;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}

