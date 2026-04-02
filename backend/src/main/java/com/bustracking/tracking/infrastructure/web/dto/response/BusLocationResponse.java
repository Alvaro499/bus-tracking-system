package com.bustracking.tracking.infrastructure.web.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BusLocationResponse(
    UUID busId,
    BigDecimal lat,
    BigDecimal lng,
    LocalDateTime updatedAt
) {}