package com.bustracking.shared.application;

import com.bustracking.shared.application.dto.TokensDTO;
import com.bustracking.shared.domain.RoleAuth;
import com.bustracking.shared.infrastructure.service.JwtService;
import com.bustracking.shared.infrastructure.service.RefreshTokenService;
import com.bustracking.shared.infrastructure.service.RefreshTokenService.RefreshTokenResult;

import org.springframework.stereotype.Service;

@Service
public class RefreshTokenUseCase {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public RefreshTokenUseCase(RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    public TokensDTO execute(String rawRefreshToken) {
        RefreshTokenResult result = refreshTokenService.validateAndRotateRefreshToken(rawRefreshToken);
        RoleAuth role = RoleAuth.valueOf(result.role());
        String newAccessToken = jwtService.generateAccessToken(result.busId(), role);
        return new TokensDTO(newAccessToken, result.newRawToken());
    }
}
