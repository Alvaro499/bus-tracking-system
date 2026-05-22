package com.bustracking.companies.infrastructure.persistence.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bus_route", schema = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusRouteJpa {
    @Id
    private UUID id;

    @Column(name = "bus_id", nullable = false)
    private UUID busId;

    @Column(name = "route_id", nullable = false)
    private UUID routeId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusRouteJpa that = (BusRouteJpa) o;
        return busId.equals(that.busId) && routeId.equals(that.routeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(busId, routeId);
    }
}
