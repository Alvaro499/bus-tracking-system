package com.bustracking.companies.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stop {

    private UUID id;

    private UUID companyId;

    private String name;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String reference;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
