package com.bustracking.tracking.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import com.bustracking.tracking.domain.model.BusCredential;
import com.bustracking.tracking.domain.repository.BusCredentialRepository;
import com.bustracking.tracking.infrastructure.persistence.entity.BusCredentialJpa;

@Repository
public class BusCredentialRepositoryImpl implements BusCredentialRepository {

    private final BusCredentialJpaRepository jpaRepository;

    public BusCredentialRepositoryImpl(BusCredentialJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<BusCredential> findByBusId(UUID busId) {
        return jpaRepository.findByBusId(busId).map(this::toDomain);
    }

    private BusCredential toDomain(BusCredentialJpa jpa) {
        return new BusCredential(
            jpa.getId(),
            jpa.getBusId(),
            jpa.getPasswordHash(),
            jpa.getIssuedAt(),
            jpa.getRevokedAt()
        );
    }
}