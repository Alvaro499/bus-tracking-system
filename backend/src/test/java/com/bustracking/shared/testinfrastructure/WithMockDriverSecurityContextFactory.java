package com.bustracking.shared.testinfrastructure;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;
import java.util.UUID;

public class WithMockDriverSecurityContextFactory
        implements WithSecurityContextFactory<WithMockDriver> {

    @Override
    public SecurityContext createSecurityContext(WithMockDriver annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        UUID busId = UUID.fromString("650e8400-e29b-41d4-a716-446655440001");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                busId, null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_DRIVER"))
        );
        context.setAuthentication(auth);
        return context;
    }
}