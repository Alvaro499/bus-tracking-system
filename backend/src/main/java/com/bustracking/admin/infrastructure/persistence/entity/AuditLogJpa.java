package com.bustracking.admin.infrastructure.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import tools.jackson.databind.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogJpa {

    @Id
    private UUID id; // generado por la BD

    @Column(name = "user_id", nullable = true)
    private UUID userId; // solo guardamos el UUID, no la relación completa

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Action action;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "old_values")
    private JsonNode oldValues;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_values")
    private JsonNode newValues;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private LocalDateTime occurredAt;

    public enum Action {
        CREATE,
        UPDATE,
        DELETE,
        ASSIGN,
        REASSIGN
    }
}
