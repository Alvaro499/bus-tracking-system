package com.bustracking.shared.testinfrastructure;

import java.util.Collections;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCompanyAdminSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCompanyAdmin> {

    @Override
    public SecurityContext createSecurityContext(WithMockCompanyAdmin annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        // Generic Id
        UUID adminId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                adminId, null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN_COMPANY")));
        context.setAuthentication(auth);
        return context;
    }
}