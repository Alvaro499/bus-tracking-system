package com.bustracking.shared.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.bustracking.shared.infrastructure.security.JwtAuthenticationFilter;
import com.bustracking.shared.infrastructure.service.JwtService;


/**
 * Unit test for {@link JwtAuthenticationFilter}.
 *
 * Scope: verifies that the filter correctly orchestrates calls to
 * {@link JwtService} to build (or not build) a SecurityContext
 * Authentication, based on the presence/validity of the token.
 *
 * JwtService is mocked here. This test does NOT care whether a token is
 * "really" valid cryptographically — that's already covered in
 * {@link JwtServiceTest}. Here we only care about the filter's own logic:
 * extracting the cookie, deciding whether to authenticate, and never
 * touching JwtService when there's no token (verifyNoInteractions).
 *
 * Conceptually equivalent to a UseCase test: SUT orchestrates a mocked
 * dependency; only branching and interaction are verified, not the
 * dependency's internal correctness.
 *
 * Does NOT test: authorization rules (which roles can access which routes).
 * See SecurityConfigTest for that.
 */

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final UUID VALID_BUS_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");
    private static final String VALID_TOKEN = "valid.jwt.token";

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService);
        SecurityContextHolder.clearContext();
    }

    // Método helper para invocar el método protegido
    private void invokeFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        ReflectionTestUtils.invokeMethod(jwtAuthenticationFilter,
                "doFilterInternal", request, response, chain);
    }

    // =========================================================
    // Happy Path: Valid Token
    // =========================================================

    @Test
    void shouldSetAuthentication_WhenTokenIsValid() throws Exception {
        Cookie[] cookies = { new Cookie("access_token", VALID_TOKEN) };
        when(request.getCookies()).thenReturn(cookies);
        when(jwtService.isTokenValid(VALID_TOKEN)).thenReturn(true);
        when(jwtService.extractBusId(VALID_TOKEN)).thenReturn(VALID_BUS_ID);
        when(jwtService.extractClaim(eq(VALID_TOKEN), any())).thenReturn("DRIVER");

        // We invoke "doFilterInternal" from JwtAuthenticationFilter by using Reflection
        invokeFilter(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(VALID_BUS_ID, auth.getPrincipal());
        assertNull(auth.getCredentials());
        assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DRIVER")));

        verify(filterChain).doFilter(request, response);
    }

    // =========================================================
    // Ausence of Cookie
    // =========================================================

    @Test
    void shouldNotSetAuthentication_WhenCookieIsMissing() throws Exception {
        when(request.getCookies()).thenReturn(null);

        invokeFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(jwtService);
        verify(filterChain).doFilter(request, response);
    }

    // =========================================================
    // Invalid Token
    // =========================================================

    @Test
    void shouldNotSetAuthentication_WhenTokenIsInvalid() throws Exception {
        Cookie[] cookies = { new Cookie("access_token", "invalid.token") };
        when(request.getCookies()).thenReturn(cookies);
        when(jwtService.isTokenValid("invalid.token")).thenReturn(false);

        invokeFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService, never()).extractBusId(anyString());
        verify(jwtService, never()).extractClaim(anyString(), any());
        verify(filterChain).doFilter(request, response);
    }
}