package com.bustracking.shared.testinfrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.bustracking.shared.infrastructure.error.GlobalExceptionHandler;
import com.bustracking.shared.infrastructure.security.JwtAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;


/*

    @WebMvcTest: starts only the web layer, including controllers, filters, and Spring MVC configuration. 
    No persistence layer, no security, and no services.

*/

@WebMvcTest
@ActiveProfiles("test")
@Import(GlobalExceptionHandler.class)
public abstract class ControllerIntegrationTest {

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void disableJwtFilter() throws Exception {
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

}
