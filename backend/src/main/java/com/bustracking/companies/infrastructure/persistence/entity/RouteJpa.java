package com.bustracking.companies.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "route", schema = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteJpa {

    @Id
    private UUID id;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 150)
    private String origin;

    @Column(nullable = false, length = 150)
    private String destination;

    // Indicates if the route has a flat fare or not
    @Column(name = "flat_fare")
    private Boolean flatFare;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteJpa)) return false;
        return id != null && id.equals(((RouteJpa) o).getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
