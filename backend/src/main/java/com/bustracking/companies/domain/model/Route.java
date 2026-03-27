package com.bustracking.companies.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Route {

    private UUID id;

    private UUID companyId;

    private String name;

    private String origin;

    private String destination;

    private Boolean flatFare;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

