package com.bustracking.tracking.infrastructure.web.dto.request;

public record LoginRequest(
    String busId,
    String password
){}