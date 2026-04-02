package com.bustracking.tracking.infrastructure.web.dto.request;

import java.math.BigDecimal;

public record UpdateBusLocationRequest (
    BigDecimal lat,
    BigDecimal lng
) {}
