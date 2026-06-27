package com.bustracking.tracking.infrastructure.web.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record StopResponse(
    UUID id,
    String name,
    BigDecimal latitude,
    BigDecimal longitude,
    String reference
) {}
