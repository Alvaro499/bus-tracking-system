package com.bustracking.companies.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bustracking.companies.domain.enums.RequestStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequest {

    private UUID id;
    private UUID companyId;
    private UUID reviewedBy;
    private RequestStatus status;
    private String rejectionReason;
    private LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;
}
