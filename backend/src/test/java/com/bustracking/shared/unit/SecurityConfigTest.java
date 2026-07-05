/*
package com.bustracking.shared.unit;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

// SpringBootTest completo, SIN addFilters=false: la cadena real corre.
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    private UsernamePasswordAuthenticationToken driverAuth() {
        return new UsernamePasswordAuthenticationToken(
                UUID.randomUUID(), null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_DRIVER")));
    }

    private UsernamePasswordAuthenticationToken adminAuth() {
        return new UsernamePasswordAuthenticationToken(
                UUID.randomUUID(), null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    // --- /auth/** debe ser público sin importar el estado de autenticación ---
    @Test
    void authLoginIsPublicWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content("{}")) // body inválido a propósito
                .andExpect(status().isNot(401)); // NO debe ser 401; puede ser 400 por validación
    }

    // --- /tracking/** requiere rol DRIVER ---
    @Test
    void trackingEndpointRejectsRequestWithoutToken() throws Exception {
        mockMvc.perform(post("/tracking/trips/{id}/start", UUID.randomUUID()))
                .andExpect(status().isUnauthorized()); // 401
    }

    @Test
    void trackingEndpointRejectsWrongRole() throws Exception {
        mockMvc.perform(post("/tracking/trips/{id}/start", UUID.randomUUID())
                .with(authentication(adminAuth())))
                .andExpect(status().isForbidden()); // 403
    }

    @Test
    void trackingEndpointAcceptsDriverRole() throws Exception {
        mockMvc.perform(post("/tracking/trips/{id}/start", UUID.randomUUID())
                .with(authentication(driverAuth())))
                .andExpect(status().isNot(401))
                .andExpect(status().isNot(403));
        // No verificamos 204 exacto porque el use case real no está mockeado aquí;
        // solo nos interesa que la puerta de seguridad deje pasar.
    }
}


Notas importantes sobre este diseño:

Uso @SpringBootTest en vez de @WebMvcTest porque necesitas la cadena de filtros real y completa, y por a la vez traes todo el contexto de la app (más lento, pero son pocos tests, así que el costo es aceptable).
No mockeo los use cases aquí a propósito — el objetivo de esta clase no es probar la lógica de negocio (eso ya está en DriverTripCommandControllerTest), sino solo el "portón de entrada". Si te preocupa que falle por dependencias reales (BD, etc.), puedes mockear los use cases igual y dejar el resto real; lo esencial es que los filtros de seguridad sí corran.
Los nombres de test están escritos en términos de la regla, no del controller: trackingEndpointRejectsWrongRole, no startTripRejectsWrongRole. Si agregas un nuevo endpoint bajo /tracking/** mañana, este test ya lo cubre sin tocarlo.

Con esto tienes la separación de responsabilidades completa:
TestQué verificaFiltros de seguridadDriverTripCommandControllerTestSerialización, status codes, delegación al use caseDesactivados (addFilters=false)SecurityConfigTestReglas de autorización por patrón de rutaActivos (cadena real)JwtAuthenticationFilterTest (si no lo tienes aún)Extracción de cookie, validación de token, construcción del SecurityContextEs el propio filtro bajo prueba, aislado

     */