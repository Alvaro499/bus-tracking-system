package com.bustracking.shared.integration;

import static com.bustracking.shared.testinfrastructure.TestSqlScripts.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.bustracking.shared.testinfrastructure.FlowIntegrationTest;


@Sql(scripts = {
        CLEANUP,
        BASE,
        TRIP_COMMON,
        BUS_CREDENTIALS
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AuthenticationFlowTest extends FlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String BUS_ID = "650e8400-e29b-41d4-a716-446655440001";
    private static final String PASSWORD = "driver123";

    // =========================================================
    // Happy Path — Login → Refresh → Logout
    // =========================================================

    /**
     * Full authentication flow:
     * 1. Login with valid credentials → obtain cookies
     * 2. Access protected endpoint with access token cookie → 200
     * 3. Refresh tokens (rotate refresh token) → obtain new cookies
     * 4. Verify old refresh token no longer works → 401
     * 5. Access protected endpoint with new access token → 200
     * 6. Logout → clear cookies
     * 7. Verify refresh after logout fails → 401
     */
    @Test
    void shouldCompleteFullAuthenticationFlowSuccessfully() throws Exception {
        // --- 1. Login ---
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"busId\":\"" + BUS_ID + "\",\"password\":\"" + PASSWORD + "\"}"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"))
                .andReturn();

        Cookie[] loginCookies = loginResult.getResponse().getCookies();
        Cookie accessTokenCookie = extractCookie(loginCookies, "access_token");
        Cookie refreshTokenCookie = extractCookie(loginCookies, "refresh_token");

        // --- 2. Access protected endpoint ---
        mockMvc.perform(get("/tracking/trips/today")
                .cookie(accessTokenCookie))
                .andExpect(status().isOk());

        // --- 3. Refresh tokens ---
        MvcResult refreshResult = mockMvc.perform(post("/auth/refresh")
                .cookie(refreshTokenCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"))
                .andReturn();

        Cookie[] newCookies = refreshResult.getResponse().getCookies();
        Cookie newAccessTokenCookie = extractCookie(newCookies, "access_token");
        Cookie newRefreshTokenCookie = extractCookie(newCookies, "refresh_token");

        // --- 4. Old refresh token should no longer work ---
        mockMvc.perform(post("/auth/refresh")
                .cookie(refreshTokenCookie))
                .andExpect(status().isUnauthorized());

        // --- 5. Access protected endpoint with new access token ---
        mockMvc.perform(get("/tracking/trips/today")
                .cookie(newAccessTokenCookie))
                .andExpect(status().isOk());

        // --- 6. Logout ---
        mockMvc.perform(post("/auth/logout")
                .cookie(newRefreshTokenCookie))
                .andExpect(status().isOk());

        // --- 7. Refresh after logout should fail ---
        mockMvc.perform(post("/auth/refresh")
                .cookie(newRefreshTokenCookie))
                .andExpect(status().isUnauthorized());
    }

    private Cookie extractCookie(Cookie[] cookies, String name) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        throw new AssertionError("Cookie not found: " + name);
    }
}
