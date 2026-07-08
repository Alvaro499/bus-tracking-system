package com.bustracking.shared.testinfrastructure;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockDriverSecurityContextFactory.class)
public @interface WithMockDriver {
}