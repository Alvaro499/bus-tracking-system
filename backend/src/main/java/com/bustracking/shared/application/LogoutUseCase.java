package com.bustracking.shared.application;

import com.bustracking.shared.infrastructure.service.RefreshTokenService;
import org.springframework.stereotype.Service;

@Service
public class LogoutUseCase {
    private final RefreshTokenService refreshTokenService;

    public LogoutUseCase(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    public void execute(String refreshToken) {
        refreshTokenService.revokeRefreshToken(refreshToken);
    }
}