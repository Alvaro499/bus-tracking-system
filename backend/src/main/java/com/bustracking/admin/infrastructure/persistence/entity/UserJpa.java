package com.bustracking.admin.infrastructure.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "\"user\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserJpa {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 12)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "global_role", nullable = false, length = 20)
    private GlobalRole globalRole;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum GlobalRole {
        PLATFORM_ADMIN,
        COMPANY_USER
    }
}
