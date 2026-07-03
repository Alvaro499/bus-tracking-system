package com.bustracking.tracking.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bus_credential", schema = "tracking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusCredentialJpa {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "bus_id", nullable = false)
    private UUID busId;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof BusCredentialJpa))
            return false;
        return busId != null && busId.equals(((BusCredentialJpa) o).getBusId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
