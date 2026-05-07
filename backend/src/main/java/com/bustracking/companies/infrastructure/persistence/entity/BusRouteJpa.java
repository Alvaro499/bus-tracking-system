package com.bustracking.companies.infrastructure.persistence.entity;

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

}
