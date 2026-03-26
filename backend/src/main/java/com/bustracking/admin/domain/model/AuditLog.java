package com.bustracking.admin.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import tools.jackson.databind.*;
import com.bustracking.admin.domain.enums.Action;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    private UUID id;
    private UUID userId;
    private String entityType;
    private UUID entityId;
    private Action action;
    private JsonNode oldValues;
    private JsonNode newValues;
    private LocalDateTime occurredAt;
}
