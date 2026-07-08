package com.bustracking.tracking.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.exception.ValidationException;

import lombok.Getter;

@Getter
public class BusCredential {
    private final UUID id;
    private final UUID busId;
    private final String passwordHash;
    private final LocalDateTime issuedAt;
    private LocalDateTime revokedAt;

    public BusCredential(UUID id, UUID busId, String passwordHash) {
        validateId(id);
        validateBusId(busId);
        validatePasswordHash(passwordHash);
        this.id = id;
        this.busId = busId;
        this.passwordHash = passwordHash;
        this.issuedAt = LocalDateTime.now();
        this.revokedAt = null;
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public void revokeCredentials() {
        if (isRevoked()) {
            throw new BusinessRuleException(
                ErrorCode.INVALID_STATE,
                "Credential already revoked",
                "Cannot revoke an already revoked credential"
            );
        }
        this.revokedAt = LocalDateTime.now();
    }

    private void validateId(UUID id) {
        if (id == null) {
            throw new ValidationException(
                ErrorCode.MISSING_REQUIRED_FIELD,
                "Id is required",
                "id cannot be null in BusCredential"
            );
        }
    }

    private void validateBusId(UUID busId) {
        if (busId == null) {
            throw new ValidationException(
                ErrorCode.MISSING_REQUIRED_FIELD,
                "BusId is required",
                "busId cannot be null in BusCredential"
            );
        }
    }

    private void validatePasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new ValidationException(
                ErrorCode.MISSING_REQUIRED_FIELD,
                "Password hash is required",
                "passwordHash cannot be null or blank"
            );
        }
    }
}