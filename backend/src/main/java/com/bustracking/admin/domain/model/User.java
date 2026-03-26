package com.bustracking.admin.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bustracking.admin.domain.enums.GlobalRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UUID id;
    private String email;
    private String password;
    private GlobalRole globalRole;
    private boolean isActive;
    private LocalDateTime createdAt;
}