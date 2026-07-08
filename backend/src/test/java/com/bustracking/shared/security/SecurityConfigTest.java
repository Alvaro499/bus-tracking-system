package com.bustracking.shared.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import com.bustracking.shared.infrastructure.error.GlobalExceptionHandler;
import com.bustracking.shared.infrastructure.security.JwtAuthenticationFilter;
import com.bustracking.shared.infrastructure.security.SecurityConfig;
import com.bustracking.shared.testinfrastructure.WithMockDriver;
import com.bustracking.shared.testinfrastructure.WithMockCompanyAdmin;

@WebMvcTest(controllers = SecurityTestController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
@ImportAutoConfiguration(exclude = {
    SecurityAutoConfiguration.class,
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
class SecurityConfigTest {

       @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() throws Exception {
        // Fuerza el puente TestSecurityContextHolder -> SecurityContextHolder,
        // en vez de depender de la auto-configuración implícita de @WebMvcTest
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
            Mockito.any(FilterChain.class)
        );
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