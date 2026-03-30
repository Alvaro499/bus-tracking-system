package com.bustracking.tracking.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.bustracking.tracking.domain.model.BusLocation;

public interface BusLocationRepository {
    
    //upsert method to save or update the bus location
    void save(BusLocation busLocation);

    Optional<BusLocation> findByBusId(UUID busId);


    //List<BusLocation> findAllByBusIds(List<UUID> busIds);

}
