Entiendo que quieres una explicación clara y formal de la diferencia entre usar `withDriverCookie` y usar cookies reales obtenidas del login en los Flow Tests. Aquí te la dejo en el formato que pides.

---

## 2.2 Estrategias de autenticación en Flow Tests

#### Descripción general

Los Flow Tests requieren que las peticiones incluyan un token de acceso válido, ya que atraviesan la cadena de seguridad real. Sin embargo, no todos los flujos de negocio comienzan con un login. Dependiendo de qué acción se esté probando, la autenticación se puede establecer de dos maneras distintas: **inyectando una cookie pre‑fabricada** con un helper, o **ejecutando el endpoint de login real** y capturando las cookies que el backend genera.

La diferencia fundamental radica en el punto de partida del flujo: cuando el login no forma parte de lo que se quiere validar se usa un helper para no contaminar el test con pasos irrelevantes; cuando el propio proceso de autenticación es el objeto bajo prueba, se realiza un login real y se trabaja con sus cookies.

---

#### Estrategia 1: Inyección directa de cookie (`withDriverCookie`)

##### Cuándo se usa

Se utiliza en flujos de negocio donde **el inicio de sesión no es parte del escenario que se quiere probar**. El objetivo del test es, por ejemplo, confirmar una parada, iniciar un viaje o consultar la ubicación del bus. En estos casos, realizar un login real añadiría ruido y pasos innecesarios, además de requerir datos de credenciales que no interesan al flujo.

##### Cómo funciona

La clase base `FlowIntegrationTest` expone el helper `withDriverCookie`. Este método usa el `JwtService` real para generar un access token firmado con la misma clave secreta que emplea el entorno de pruebas. El token se envuelve en una cookie llamada `access_token` y se adjunta a la petición simulada mediante un `RequestPostProcessor`.

###### Ejemplo extraído de `ConfirmStopFlowTest`

```java
class ConfirmStopFlowTest extends FlowIntegrationTest {

    private static final UUID BUS_ID = UUID.fromString("650e8400-...");

    @Test
    void shouldConfirmStopAndReturnUpdatedDetail() throws Exception {
        // El helper withDriverCookie genera un token real para BUS_ID
        // y lo inyecta directamente en la petición.
        mockMvc.perform(post("/tracking/trips/{tripId}/stops/{routeStopId}/confirm",
                TRIP_ID, ROUTE_STOP_ID)
                .with(withDriverCookie(BUS_ID)))
                .andExpect(status().isOk());
    }
}
```

En este test, el login **no se ejecuta**. La cookie `access_token` se construye en memoria y se adjunta a la petición. El filtro `JwtAuthenticationFilter` la valida exactamente igual que en producción, pero sin pasar por el endpoint `/auth/login`.

---

#### Estrategia 2: Obtención de cookies mediante login real

##### Cuándo se usa

Se utiliza cuando **el propio proceso de autenticación forma parte del flujo que se quiere probar**. Es el caso de `AuthenticationFlowTest`, donde el escenario es "login → refresh → acceso a recurso protegido → logout". Aquí no tendría sentido inyectar una cookie prefabricada, porque el comportamiento que se desea verificar es precisamente la generación, rotación y revocación de los tokens.

##### Cómo funciona

El primer paso del test realiza una petición `POST /auth/login` con credenciales válidas. El backend autentica al bus, genera los tokens y los devuelve en las cabeceras `Set-Cookie`. El test extrae esas cookies del `MockHttpServletResponse` y las reutiliza en las peticiones posteriores, simulando exactamente cómo actuaría un navegador.

###### Ejemplo extraído de `AuthenticationFlowTest`

```java
class AuthenticationFlowTest extends FlowIntegrationTest {

    private static final String BUS_ID = "650e8400-...";
    private static final String PASSWORD = "driver123";

    @Test
    void shouldCompleteFullAuthenticationFlowSuccessfully() throws Exception {
        // --- 1. Login real ---
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"busId\":\"" + BUS_ID + "\",\"password\":\"" + PASSWORD + "\"}"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().exists("refresh_token"))
                .andReturn();

        // --- 2. Extraer las cookies de la respuesta ---
        Cookie accessTokenCookie = extractCookie(loginResult.getResponse().getCookies(), "access_token");
        Cookie refreshTokenCookie = extractCookie(loginResult.getResponse().getCookies(), "refresh_token");

        // --- 3. Usar las cookies reales en las siguientes peticiones ---
        mockMvc.perform(get("/tracking/trips/today")
                .cookie(accessTokenCookie))
                .andExpect(status().isOk());
    }
}
```

Aquí el test **no usa `withDriverCookie`**. En su lugar, captura las cookies que el propio backend emitió y las va pasando de una petición a otra, igual que haría un cliente HTTP real. Esto garantiza que el flujo de autenticación (incluyendo la rotación de tokens y la invalidación por logout) se prueba de extremo a extremo.

---

#### Comparación directa

| Aspecto | Inyección con `withDriverCookie` | Login real + cookies |
|---------|----------------------------------|----------------------|
| ¿Se ejecuta el endpoint `/auth/login`? | No | Sí |
| ¿Se necesita la contraseña del bus? | No (solo el `busId`) | Sí |
| ¿Se prueba la lógica de credenciales? | No (ya está cubierta en otros tests) | Sí, indirectamente |
| ¿Se prueba la rotación/logout? | No | Sí |
| Momento típico de uso | Flujos de negocio ajenos al login | Flujo completo de autenticación |