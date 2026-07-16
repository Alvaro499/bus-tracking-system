package com.bustracking.tracking.integration.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bustracking.shared.application.LogoutUseCase;
import com.bustracking.shared.application.RefreshTokenUseCase;
import com.bustracking.shared.application.dto.TokensDTO;
import com.bustracking.shared.exception.BusinessRuleException;
import com.bustracking.shared.exception.ErrorCode;
import com.bustracking.shared.testinfrastructure.ControllerIntegrationTest;
import com.bustracking.tracking.application.usecase.AuthenticateBusUseCase;
import com.bustracking.tracking.infrastructure.web.controller.AuthenticateDriverController;
import com.bustracking.tracking.infrastructure.web.dto.request.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;

@WebMvcTest(AuthenticateDriverController.class)
public class AuthenticateDriverControllerTest extends ControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        AuthenticateBusUseCase authBusUseCaseMock;

        @MockitoBean
        RefreshTokenUseCase refreshTokenUseCase;

        @MockitoBean
        LogoutUseCase logoutUseCase;

        private final ObjectMapper objectMapper = new ObjectMapper();

        // Test common data
        private final UUID BUS_ID = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");
        private final String VALID_PASSWORD = "random_pass";
        private static final TokensDTO VALID_TOKENS = new TokensDTO("fake_access_token", "fake_refresh_token");

        // =========================================================
        // POST /auth/login - Happy Path - Login
        // =========================================================

        @Test
        public void shouldReturnTokenDTO_WhenCredentialAreValid() throws Exception {

                // Arrange
                LoginRequest request = new LoginRequest(BUS_ID.toString(), VALID_PASSWORD);
                when(authBusUseCaseMock.execute(eq(BUS_ID), eq(VALID_PASSWORD)))
                                .thenReturn(VALID_TOKENS);

                // Act and Assert
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(cookie().exists("access_token"))
                                .andExpect(cookie().httpOnly("access_token", true))
                                .andExpect(cookie().path("access_token", "/"))
                                .andExpect(cookie().maxAge("access_token", 900))
                                .andExpect(cookie().exists("refresh_token"))
                                .andExpect(cookie().httpOnly("refresh_token", true))
                                .andExpect(cookie().path("refresh_token", "/auth/refresh"))
                                .andExpect(cookie().maxAge("refresh_token", 604800));
        }

        // =========================================================
        // POST /auth/login - Login - Invalid Credentials
        // =========================================================

        @Test
        void shouldReturn401_WhenCredentialsAreInvalid() throws Exception {
                when(authBusUseCaseMock.execute(eq(BUS_ID), eq("wrong_password")))
                                .thenThrow(new BusinessRuleException(
                                                ErrorCode.INVALID_CREDENTIALS,
                                                "Invalid credentials",
                                                "The password does not match"));

                LoginRequest request = new LoginRequest(BUS_ID.toString(), "wrong_password");

                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isUnauthorized()) // 401
                                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
        }

        // =========================================================
        // POST /auth/refresh - Happy Path - Refresh Token
        // =========================================================

        @Test
        public void shouldReturn200AndSetCookies_WhenRefreshTokenIsValid() throws Exception {
                // Arrange
                String validRefreshToken = "valid_refresh_token_value";
                TokensDTO newTokens = new TokensDTO("new_access_token", "new_refresh_token");

                when(refreshTokenUseCase.execute(eq(validRefreshToken)))
                                .thenReturn(newTokens);

                // Act & Assert
                mockMvc.perform(post("/auth/refresh")
                                .cookie(new Cookie("refresh_token", validRefreshToken)))
                                .andExpect(status().isOk())
                                .andExpect(cookie().exists("access_token"))
                                .andExpect(cookie().httpOnly("access_token", true))
                                .andExpect(cookie().path("access_token", "/"))
                                .andExpect(cookie().maxAge("access_token", 900))
                                .andExpect(cookie().exists("refresh_token"))
                                .andExpect(cookie().httpOnly("refresh_token", true))
                                .andExpect(cookie().path("refresh_token", "/auth/refresh"))
                                .andExpect(cookie().maxAge("refresh_token", 604800));
        }

        // =========================================================
        // POST /auth/refresh - Invalid Refresh Token
        // =========================================================

        @Test
        public void shouldReturn401_WhenRefreshTokenIsInvalid() throws Exception {
                // Arrange
                String invalidRefreshToken = "invalid_or_reused_token";

                when(refreshTokenUseCase.execute(eq(invalidRefreshToken)))
                                .thenThrow(new BusinessRuleException(
                                                ErrorCode.INVALID_CREDENTIALS,
                                                "Invalid refresh token",
                                                "Refresh token not found or already used"));

                // Act & Assert
                mockMvc.perform(post("/auth/refresh")
                                .cookie(new Cookie("refresh_token", invalidRefreshToken)))
                                .andExpect(status().isUnauthorized()) // 401
                                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
        }

        // =========================================================
        // POST /auth/logout - Happy Path - Logout
        // =========================================================

        @Test
        public void shouldReturn200AndClearCookies_WhenLogoutIsSuccessful() throws Exception {
                // Arrange
                String refreshToken = "some_refresh_token";

                // Act & Assert
                mockMvc.perform(post("/auth/logout")
                                .cookie(new Cookie("refresh_token", refreshToken)))
                                .andExpect(status().isOk())
                                .andExpect(cookie().exists("access_token"))
                                .andExpect(cookie().maxAge("access_token", 0))
                                .andExpect(cookie().exists("refresh_token"))
                                .andExpect(cookie().maxAge("refresh_token", 0));

                // Verificar que se llamó al caso de uso
                verify(logoutUseCase, times(1)).execute(eq(refreshToken));
        }
}
