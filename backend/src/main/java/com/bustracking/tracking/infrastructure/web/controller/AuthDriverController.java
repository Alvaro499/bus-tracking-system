package com.bustracking.tracking.infrastructure.web.controller;

import com.bustracking.tracking.application.dto.TokensDTO;
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
public class AuthDriverController {

    private final AuthenticateBusUseCase authenticateBusUseCase;

    public AuthDriverController(AuthenticateBusUseCase authenticateBusUseCase) {
        this.authenticateBusUseCase = authenticateBusUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request) {
        TokensDTO tokens = authenticateBusUseCase.execute(UUID.fromString(request.busId()), request.password());

        ResponseCookie accessCookie = ResponseCookie.from("access_token", tokens.accessToken())
                .httpOnly(true)
                .secure(false) // Only in dev could be false, in production true with HTTPS
                .sameSite("Strict")
                .path("/")
                .maxAge(900) // 15 min in seconds
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", tokens.refreshToken())
                .httpOnly(true)
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
}