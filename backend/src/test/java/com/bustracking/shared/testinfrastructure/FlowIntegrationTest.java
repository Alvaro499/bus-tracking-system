package com.bustracking.shared.testinfrastructure;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.mock.web.MockHttpServletRequest;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.bustracking.shared.infrastructure.service.JwtService;

// Standard Jakarta EE API (Servlet 5/6) used by Spring Boot 3.x/4.x.
import jakarta.servlet.http.Cookie;

/**
 * @SpringBootTest can not configure MockMvc by default. This annotation tell it to auto-configure MockMvc
 */


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class FlowIntegrationTest extends PostgresTestContainer {

    /*
    * Logic Implemented at 7/4/2026, much later from implementartion of test 
    configuration 
    */
    @Autowired
    protected JwtService jwtService;

    // Helper method that creates a cookie with a token for a specific busId 
    // given in a test method.
    protected RequestPostProcessor withDriverCookie(UUID busId) {
        String token = jwtService.generateAccessToken(busId);
        return new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setCookies(new Cookie("access_token", token));
                return request;
            }
        };
    }

    protected RequestPostProcessor withDriverCookie() {
        return withDriverCookie(UUID.fromString("650e8400-e29b-41d4-a716-446655440001"));
    }

}