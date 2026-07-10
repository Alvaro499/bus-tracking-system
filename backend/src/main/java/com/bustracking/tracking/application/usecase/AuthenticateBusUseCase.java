package com.bustracking.tracking.application.usecase;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bustracking.shared.domain.RoleAuth;
import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.infrastructure.service.JwtService;
import com.bustracking.shared.infrastructure.service.RefreshTokenService;
import com.bustracking.tracking.application.dto.TokensDTO;
import com.bustracking.tracking.domain.model.BusCredential;
import com.bustracking.tracking.domain.repository.BusCredentialRepository;

@Service
public class AuthenticateBusUseCase {

    private final BusCredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;

    //Services
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthenticateBusUseCase(BusCredentialRepository credentialRepository,PasswordEncoder passwordEncoder,
                                    JwtService jwtService,
                                    RefreshTokenService refreshTokenService) {
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public TokensDTO execute(UUID busId, String rawPassword) {
        Optional<BusCredential> optionalCredential = credentialRepository.findByBusId(busId);

        if (optionalCredential.isEmpty()) {
            throw new BusinessRuleException(
                    ErrorCode.INVALID_CREDENTIALS,
                    "Invalid credentials",
                    "Bus not found or invalid credentials");
        }
        ;

        BusCredential credential = optionalCredential.get();

        if (credential.isRevoked()) {
            throw new BusinessRuleException(
                    ErrorCode.INVALID_CREDENTIALS,
                    "Invalid credentials",
                    "The credentials for this bus have been revoked");
        }

        if (!passwordEncoder.matches(rawPassword, credential.getPasswordHash())) {
            throw new BusinessRuleException(
                    ErrorCode.INVALID_CREDENTIALS,
                    "Invalid credentials",
                    "The password does not match");
        }

        String accessToken = jwtService.generateAccessToken(busId, RoleAuth.DRIVER);
        String refreshToken = generateRefreshToken();

        refreshTokenService.saveRefreshToken(busId, refreshToken);

        return new TokensDTO(accessToken, refreshToken);
    }

    private String generateRefreshToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}