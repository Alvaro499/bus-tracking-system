package com.bustracking.companies.infrastructure.persistence.entity;

import com.bustracking.companies.domain.enums.BusStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "bus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusJpa {

    @Id
    private UUID id;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "plate", unique = true, nullable = false, length = 20)
    private String plate;

    @Column(name = "internal_number", length = 20)
    private String internalNumber;

    @Column(name = "has_ramp")
    private Boolean hasRamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BusStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BusJpa)) return false;
        return id != null && id.equals(((BusJpa) o).getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
