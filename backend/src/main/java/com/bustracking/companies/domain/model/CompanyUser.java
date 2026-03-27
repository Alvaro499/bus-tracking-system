package com.bustracking.companies.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bustracking.companies.domain.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUser {

    private UUID id;
    private UUID userId;
    private UUID companyId;
    private Role role;
    private LocalDateTime createdAt;
}
