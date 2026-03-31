package com.bustracking.tracking.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.bustracking.shared.valueobjects.GpsCoordinate;
import com.bustracking.tracking.domain.model.BusLocation;
import com.bustracking.tracking.domain.repository.BusLocationRepository;
import com.bustracking.tracking.infrastructure.persistence.entity.BusLocationJpa;

@Repository
public class BusLocationRepositoryImpl implements BusLocationRepository {
    
    private final BusLocationJpaRepository busLocationJpaRepository;

    public BusLocationRepositoryImpl(BusLocationJpaRepository busLocationJpaRepository) {
        this.busLocationJpaRepository = busLocationJpaRepository;
    }

    @Override
    public void save(BusLocation busLocation){
        busLocationJpaRepository.save(toJpa(busLocation));
    }

    @Override
    public Optional<BusLocation> findByBusId(UUID busId) {
        return busLocationJpaRepository.findById(busId)
                .map(this::toDomain);
    }


    private BusLocationJpa toJpa(BusLocation busLocation){
        return new BusLocationJpa(
            busLocation.getBusId(),
            busLocation.getGpsCoordinate().getLat(),
            busLocation.getGpsCoordinate().getLng(),
            busLocation.getUpdatedAt()
        );
    }

    private BusLocation toDomain(BusLocationJpa busLocationJpa){
        return new BusLocation(
            busLocationJpa.getBusId(),
            new GpsCoordinate(busLocationJpa.getLat(), busLocationJpa.getLng()),
            busLocationJpa.getUpdatedAt()
        );

    }
}
