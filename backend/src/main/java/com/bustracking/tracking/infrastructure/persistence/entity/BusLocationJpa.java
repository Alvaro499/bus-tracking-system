package com.bustracking.tracking.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bus_location")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusLocationJpa {

    @Id
    @Column(name = "bus_id")
    private UUID busId;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal lat;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal lng;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BusLocationJpa)) return false;
        return busId != null && busId.equals(((BusLocationJpa) o).getBusId());
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
