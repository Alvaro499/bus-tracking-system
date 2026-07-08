package com.bustracking.shared.testinfrastructure;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCompanyAdminSecurityContextFactory.class)
public @interface WithMockCompanyAdmin {
}
