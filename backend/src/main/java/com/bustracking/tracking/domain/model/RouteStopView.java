package com.bustracking.tracking.domain.model;

import java.util.UUID;

public class RouteStopView {
    private final UUID id;
    private final UUID stopId;
    private final int orderIndex;
    private final int estimatedTimeOffset;

    public RouteStopView(UUID id, UUID stopId, int orderIndex, int estimatedTimeOffset) {
        this.id = id;
        this.stopId = stopId;
        this.orderIndex = orderIndex;
        this.estimatedTimeOffset = estimatedTimeOffset;
    }

    public UUID getId() {
        return id;
    }

    public UUID getStopId() {
        return stopId;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public int getEstimatedTimeOffset() {
        return estimatedTimeOffset;
    }
}
