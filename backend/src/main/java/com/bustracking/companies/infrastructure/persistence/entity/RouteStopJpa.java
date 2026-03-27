package com.bustracking.companies.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "route_stop",
       uniqueConstraints = @UniqueConstraint(columnNames = {"route_id", "order_index"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteStopJpa {

    @Id
    private UUID id;

    @Column(name = "route_id", nullable = false)
    private UUID routeId;

    @Column(name = "stop_id", nullable = false)
    private UUID stopId;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(name = "estimated_time_offset")
    private Integer estimatedTimeOffset;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteStopJpa)) return false;
        return id != null && id.equals(((RouteStopJpa) o).getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
