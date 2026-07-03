package com.bustracking.tracking.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;

/**
 * Represents the credentials of a bus for authentication purposes.
 * 
 * Immutable by design: once created, the credentials cannot be changed.
 */

@Getter
public class BusCredential {
    private final UUID id;
    private final UUID busId;
    private final String passwordHash;
    private final LocalDateTime issuedAt;
    private final LocalDateTime revokedAt;

    public BusCredential(UUID id, UUID busId, String passwordHash,
                         LocalDateTime issuedAt, LocalDateTime revokedAt) {
        this.id = id;
        this.busId = busId;
        this.passwordHash = passwordHash;
        this.issuedAt = issuedAt;
        this.revokedAt = revokedAt;
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }
}