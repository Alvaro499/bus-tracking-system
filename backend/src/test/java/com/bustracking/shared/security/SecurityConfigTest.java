package com.bustracking.shared.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
 * SecurityConfig, add a corresponding case here — coverage should map
 * 1:1 to the rules declared in the SecurityFilterChain.
 */

@WebMvcTest(controllers = SecurityTestController.class)
@Import({ SecurityConfig.class, GlobalExceptionHandler.class })
class SecurityConfigTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() throws Exception {
        // We force the breach between  TestSecurityContextHolder -> SecurityContextHolder,
        // instead of dependin on implícit auto-configuration from @WebMvcTest
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        Mockito.doAnswer(invocation -> {
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jwtAuthenticationFilter).doFilter(
                Mockito.any(ServletRequest.class),
                Mockito.any(ServletResponse.class),
                Mockito.any(FilterChain.class));
    }

    @Test
    void shouldAllowPublicEndpointWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/auth/test"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn403WhenAccessingProtectedWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/tracking/test"))
                .andExpect(status().isForbidden());
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
    void shouldReturn403ForUnknownPathWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/some/random/path"))
                .andExpect(status().isForbidden());
    }
}