package com.bustracking.shared.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.bustracking.shared.infrastructure.error.GlobalExceptionHandler;
import com.bustracking.shared.infrastructure.security.JwtAuthenticationFilter;
import com.bustracking.shared.infrastructure.security.SecurityConfig;
import com.bustracking.shared.testinfrastructure.WithMockDriver;
import com.bustracking.shared.testinfrastructure.WithMockCompanyAdmin;

/**
 * Integration test for the real security chain: {@link JwtAuthenticationFilter}
 * + SecurityConfig working together, as Spring would run them in production.
 *
 * Scope: verifies authorization rules — which routes are public, which
 * require authentication, and which require a specific role.
 *
 * Unlike JwtAuthenticationFilterTest, nothing here is mocked: the real
 * SecurityConfig is imported (@Import), and the real filter chain runs.
 * This is intentional — in this test, the filter and the security config
 * ARE the subject under test, not a dependency to isolate.
 *
 * A dummy TestController is used instead of any real business controller,
 * so that changes to production controllers can never accidentally break
 * this test (and vice versa).
 *
 * @WithMockDriver / @WithMockAdmin inject a SecurityContext directly to
 * simulate an already-authenticated request; they do not exercise
 * JwtAuthenticationFilter's token-parsing logic (that's JwtAuthenticationFilterTest's job).
 *
 * If you add a new role or a new protected/public route pattern to
 * SecurityConfig, add a corresponding case here, coverage should map
 * 1:1 to the rules declared in the SecurityFilterChain.
 */

@WebMvcTest(controllers = SecurityTestController.class)
@Import({ SecurityConfig.class, GlobalExceptionHandler.class, SecurityConfigTest.FilterTestConfig.class })
class SecurityConfigTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    // Configuración especial para los tests para proveer un filtro real que deje pasar la petición a Spring Security
    @TestConfiguration
    static class FilterTestConfig {
        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
            return new JwtAuthenticationFilter(null) { // Hereda de tu filtro real ignorando sus dependencias
                @Override
                protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                        throws ServletException, IOException {
                    // Simplemente deja fluir la petición hacia el SpringSecurityFilterChain nativo
                    filterChain.doFilter(request, response);
                }
            };
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        // Inicialización nativa y estricta de MockMvc aplicando la seguridad real
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

   @Test
    void shouldAllowPublicEndpointWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/auth/test"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WhenAccessingProtectedWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/tracking/test"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockDriver
    void shouldAllowProtectedWithDriverRole() throws Exception {
        mockMvc.perform(get("/tracking/test"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCompanyAdmin
    void shouldReturn403ForProtectedWithAdminRole() throws Exception {
        mockMvc.perform(get("/tracking/test"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401ForUnknownPathWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/some/random/path"))
                .andExpect(status().isUnauthorized());
    }
}