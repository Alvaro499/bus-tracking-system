package com.bustracking.shared.infrastructure.security;

import com.bustracking.shared.infrastructure.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

/**
 * This class is responsible for managing JWT authentication.
 * The usecases like AuthenticateBusUseCase manage the authentication process
 * and creation of JWT tokens,
 * while this class is responsible for validating the JWT tokens in incoming
 * requests.
 * 
 * This filter is the heart of JWT authentication. Se ejecuta una vez por cada
 * petición HTTP (OncePerRequestFilter),
 * extrae el access_token de la cookie, lo valida y, si es correcto, establece
 * el contexto de seguridad para que Spring
 * Security autorice el acceso a los endpoints protegidos.
 * 
 * Este filtro no rechaza peticiones sin token ni lanza excepciones. Simplemente
 * no establece autenticación.
 * Será la configuración de seguridad (SecurityConfig) la que exija o no
 * autenticación según la ruta.
 */

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // 1. We extract the access_token from the cookies of the request
        String token = extractTokenFromCookie(request);

        // 2. If there is a token and it is valid, authenticate
        if (token != null && jwtService.isTokenValid(token)) {

            UUID busId = jwtService.extractBusId(token);

            // We extract the role from the token claims to set the correct authorities in
            // Spring Security
            String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));

            var authorities = Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + role));

            // Create an authentication object with the busId and role, and set it in the
            // security context
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    busId, // controllers can get the busId from the authentication object
                    null, // password is not needed for JWT authentication
                    authorities);

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            // Set the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * It extracts the value of the "access_token" cookie from the request.
     * If it doesn't exist, it returns null (no authentication).
     */
    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}