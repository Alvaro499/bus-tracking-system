package com.bustracking.companies.domain.model;

import com.bustracking.companies.domain.enums.BusStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bus {

    private UUID id;

    private UUID companyId;

    private String plate;

    private String internalNumber;

    private Boolean hasRamp;

    private BusStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
