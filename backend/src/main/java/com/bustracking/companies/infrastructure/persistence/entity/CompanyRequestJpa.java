package com.bustracking.companies.infrastructure.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bustracking.admin.infrastructure.persistence.entity.UserJpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "company_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequestJpa {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyJpa company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by", nullable = true)
    private UserJpa reviewedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestStatus status;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    //By the momento we don't store a history of the reviews, so we only store the last review date.
    @Column(name = "reviewed_at", nullable = true)
    private LocalDateTime reviewedAt;

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
