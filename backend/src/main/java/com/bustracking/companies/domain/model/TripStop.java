package com.bustracking.companies.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripStop {
    
    private UUID id;
    private UUID trip_id;
    private UUID route_stop_id;
    private LocalDateTime completed_at;
}
