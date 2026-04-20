package com.bustracking.companies.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "route_stop_fare", schema = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteStopFareJpa {

    @Id
    private UUID id;

    @Column(name = "route_stop_id", nullable = false)
    private UUID routeStopId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteStopFareJpa)) return false;
        return id != null && id.equals(((RouteStopFareJpa) o).getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
