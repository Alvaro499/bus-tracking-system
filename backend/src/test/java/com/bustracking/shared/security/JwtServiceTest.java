package com.bustracking.shared.security;

/**
 * Unit test for {@link JwtService}.
 *
 * Scope: verifies the real cryptographic logic of token generation and
 * validation (signing, parsing, expiration, signature verification).
 *
 * No mocks are used here. JwtService has no external dependencies to isolate
 * from — its own logic IS what this test verifies, so real instances with
 * real (test-only) secret keys are used throughout.
 *
 * If you need to change how tokens are signed, parsed, or validated,
 * this is the class that should catch a regression.
 *
 * Does NOT test: how the token is extracted from a request, how it's used
 * to build a Spring Authentication object, or authorization rules.
 * See {@link JwtAuthenticationFilterTest} and SecurityConfigTest for those.
 */

public class JwtServiceTest {
    
}
