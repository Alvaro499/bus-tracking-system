package com.bustracking.tracking.unit.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ValidationException;
import com.bustracking.tracking.domain.model.BusCredential;

public class BusCredentialTest {

    // Test Common Data
    private final UUID validId = UUID.randomUUID();
    private final UUID validBusId = UUID.randomUUID();
    private final String validPasswordHash = "someHashedPassword";

    // ==========================================================
    // Happy Path Tests - Creating New BusCredential
    // ==========================================================

    @Test
    void shouldCreateBusCredentialWithValidValues() {
        BusCredential credential = new BusCredential(validId, validBusId, validPasswordHash);

        assertEquals(validId, credential.getId());
        assertEquals(validBusId, credential.getBusId());
        assertEquals(validPasswordHash, credential.getPasswordHash());
        assertNotNull(credential.getIssuedAt());
        assertNull(credential.getRevokedAt());
        assertFalse(credential.isRevoked());
    }

    // ==========================================================
    // Validation Tests -
    // ==========================================================

    @Test
    void shouldThrowWhenIdIsNull() {
        assertThrows(ValidationException.class, () -> new BusCredential(null, validBusId, validPasswordHash));
    }

    // ==========================================================
    // Validation Tests -
    // ==========================================================

    @Test
    void shouldThrowWhenBusIdIsNull() {
        assertThrows(ValidationException.class, () -> new BusCredential(validId, null, validPasswordHash));
    }

    // ==========================================================
    // Validation Tests -
    // ==========================================================

    @Test
    void shouldThrowWhenPasswordHashIsNull() {
        assertThrows(ValidationException.class, () -> new BusCredential(validId, validBusId, null));
    }

    // ==========================================================
    // Validation Tests -
    // ==========================================================

    @Test
    void shouldThrowWhenPasswordHashIsBlank() {
        assertThrows(ValidationException.class, () -> new BusCredential(validId, validBusId, "   "));
    }

    // ==========================================================
    // Revocation Logic Tests -
    // ==========================================================

    @Test
    void shouldRevokeCredentialsSuccessfully() {
        BusCredential credential = new BusCredential(validId, validBusId, validPasswordHash);
        assertFalse(credential.isRevoked());

        credential.revokeCredentials();

        assertTrue(credential.isRevoked());
        assertNotNull(credential.getRevokedAt());
    }

    // ==========================================================
    // Revocation Logic Tests -
    // ==========================================================

    @Test
    void shouldThrowWhenRevokingAlreadyRevokedCredential() {
        BusCredential credential = new BusCredential(validId, validBusId, validPasswordHash);
        credential.revokeCredentials();

        // If we revoke again the method will throwBusinessRuleException
        assertThrows(BusinessRuleException.class, credential::revokeCredentials);
    }
}