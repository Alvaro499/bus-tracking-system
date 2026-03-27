package com.bustracking.companies.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteStop {

    private UUID id;

    private UUID routeId;

    private UUID stopId;

    private Integer orderIndex;

    private Integer estimatedTimeOffset;

    private LocalDateTime createdAt;
}
