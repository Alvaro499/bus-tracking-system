package com.bustracking.companies.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bustracking.companies.domain.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    private UUID id;
    private String taxId;
    private String name;
    private String email;
    private String phone;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
