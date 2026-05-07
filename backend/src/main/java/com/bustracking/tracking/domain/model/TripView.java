package com.bustracking.tracking.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class TripView {
    private final UUID id;
    private final UUID scheduleId;
    private final UUID busId;
    private final LocalDate tripDate;
    private final String status;
    private final LocalTime actualStartTime;
    private final LocalTime actualEndTime;

    public TripView(UUID id, UUID scheduleId, UUID busId, LocalDate tripDate,
                    String status, LocalTime actualStartTime, LocalTime actualEndTime) {
        this.id = id;
        this.scheduleId = scheduleId;
        this.busId = busId;
        this.tripDate = tripDate;
        this.status = status;
        this.actualStartTime = actualStartTime;
        this.actualEndTime = actualEndTime;
    }

    public UUID getId() { return id; }
    public UUID getScheduleId() { return scheduleId; }
    public UUID getBusId() { return busId; }
    public LocalDate getTripDate() { return tripDate; }
    public String getStatus() { return status; }
    public LocalTime getActualStartTime() { return actualStartTime; }
    public LocalTime getActualEndTime() { return actualEndTime; }
}