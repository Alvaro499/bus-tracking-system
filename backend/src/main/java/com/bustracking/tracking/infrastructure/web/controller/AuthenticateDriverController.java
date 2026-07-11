package com.bustracking.tracking.infrastructure.web.controller;

import com.bustracking.shared.application.LogoutUseCase;
import com.bustracking.shared.application.RefreshTokenUseCase;
import com.bustracking.shared.application.dto.TokensDTO;
import com.bustracking.tracking.application.usecase.AuthenticateBusUseCase;
import com.bustracking.tracking.infrastructure.web.dto.request.LoginRequest;
import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticateDriverController {

    private final AuthenticateBusUseCase authenticateBusUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;

    public AuthenticateDriverController(AuthenticateBusUseCase authenticateBusUseCase,
            RefreshTokenUseCase refreshTokenUseCase,
            LogoutUseCase logoutUseCase) {
        this.authenticateBusUseCase = authenticateBusUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request) {
        TokensDTO tokens = authenticateBusUseCase.execute(UUID.fromString(request.busId()), request.password());
        return buildTokenResponse(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(@CookieValue("refresh_token") String refreshToken) {
        TokensDTO tokens = refreshTokenUseCase.execute(refreshToken);
        return buildTokenResponse(tokens);
    }

    private ResponseEntity<Void> buildTokenResponse(TokensDTO tokens) {

        ResponseCookie accessCookie = ResponseCookie.from("access_token", tokens.accessToken())
                .httpOnly(true) // Prevents JavaScript access to the cookie
                .secure(false) // Only in dev could be false, in production true with HTTPS
                .sameSite("Strict") // Prevents CSRF attacks by avoiding other sites call the API and get/send the
                                    // cookie
                .path("/") // cookie can be sent to any endpoint of the API
                .maxAge(900) // 15 min in seconds
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", tokens.refreshToken())
                .httpOnly(true) // Prevents JavaScript access to the cookie
                .secure(false) // Only in dev could be false, in production true with HTTPS
                .sameSite("Strict")
                .path("/auth/refresh") // Only sent when refreshing
                .maxAge(604800) // 7 days in seconds
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue("refresh_token") String refreshToken) {
        logoutUseCase.execute(refreshToken);
        ResponseCookie deleteAccessCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();
        ResponseCookie deleteRefreshCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/auth/refresh")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteAccessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, deleteRefreshCookie.toString())
                .build();
    }
}