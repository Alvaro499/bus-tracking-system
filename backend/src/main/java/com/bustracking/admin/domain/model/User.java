package com.bustracking.admin.domain.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User{

    private UUID id;
    private String email;
    private String password;
    private String globalRole;
    private boolean isActive; 
    private LocalDateTime createdAt;
}