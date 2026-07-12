## Fase 2 – Petición a un endpoint protegido (`/tracking/**`)

Una vez que el conductor ha iniciado sesión, el navegador tiene almacenadas las cookies `access_token` y `refresh_token`. A partir de ese momento, cada petición que realice a los endpoints bajo `/tracking/**` seguirá el flujo que se detalla a continuación.

### 1. El navegador envía automáticamente la cookie `access_token`

Gracias a la configuración de la cookie (`path="/"`, `sameSite="Strict"`), el navegador adjunta la cookie `access_token` en cada petición a cualquier ruta del backend, sin intervención del código JavaScript del frontend. El token viaja en la cabecera `Cookie` de la petición HTTP.

### 2. El filtro `JwtAuthenticationFilter` intercepta la petición

Por cada petición, Spring Security ejecuta el método `doFilterInternal` del filtro:

```java
@Override
protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

    // 2.1 Extraer el token de la cookie "access_token"
    String token = extractTokenFromCookie(request);

    // 2.2 Si hay token y es válido, autenticar
    if (token != null && jwtService.isTokenValid(token)) {
        UUID busId = jwtService.extractBusId(token);
        String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));

        var authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                busId, null, authorities);

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 2.3 Continuar siempre con la cadena de filtros
    filterChain.doFilter(request, response);
}
```

#### 2.1 Extracción de la cookie

El método `extractTokenFromCookie` recorre las cookies de la petición y devuelve el valor de aquella cuyo nombre sea `"access_token"`. Si no existe, retorna `null`.

#### 2.2 Validación del token y establecimiento del contexto de seguridad

La condición `token != null && jwtService.isTokenValid(token)` hace dos cosas:

- Verifica que exista un token.
- Llama a `jwtService.isTokenValid(token)`, que a su vez invoca `isTokenExpired(token)`.  
  `isTokenExpired` extrae la fecha de expiración mediante `extractExpiration` → `extractClaim` → `parseToken`.  
  `parseToken` es el método que realmente verifica la firma y decodifica el payload. Si el token ha expirado, la firma no coincide o el formato es inválido, se lanzará una excepción que `isTokenValid` captura y convierte en `false`.

Si el token es válido, el filtro extrae el `busId` y el rol (por ejemplo, `"DRIVER"`), construye una lista de autoridades con `ROLE_DRIVER` y crea un objeto `UsernamePasswordAuthenticationToken`. Este objeto se establece en el `SecurityContextHolder`, dejando al usuario autenticado para el resto de la petición.

#### 2.3 Continuación de la cadena de filtros

Independientemente de si el token era válido o no, se llama a `filterChain.doFilter(request, response)`. Esto permite que la petición siga fluyendo a través de los demás filtros de Spring Security hasta llegar al `FilterSecurityInterceptor`, que será el encargado de hacer cumplir las reglas de autorización.

### 3. La autorización ocurre en el `FilterSecurityInterceptor`

Después del filtro JWT, la petición pasa por varios filtros internos de Spring Security, hasta llegar al `FilterSecurityInterceptor`. Este interceptor consulta las reglas definidas en `SecurityConfig`:

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/auth/**").permitAll()
    .requestMatchers("/tracking/**").hasRole(RoleAuth.DRIVER.name())
    .anyRequest().authenticated()
)
```

Para la ruta `/tracking/**`, el interceptor inspecciona el `SecurityContextHolder` en busca de una autenticación que contenga la autoridad `ROLE_DRIVER`. Aquí hay dos escenarios posibles:

- **Token válido**: el contexto contiene un `Authentication` con `ROLE_DRIVER`. La comprobación de rol tiene éxito y la petición continúa hacia el controlador.
- **Token no válido o ausente**: el contexto permanece vacío. Al no haber autenticación (o al no tener el rol requerido), el `FilterSecurityInterceptor` lanza una excepción:
  - Si **no hay autenticación en absoluto**, se lanza una `AuthenticationException`, que es capturada por el `authenticationEntryPoint` configurado en `SecurityConfig`, devolviendo un **401 Unauthorized**.
  - Si **hay autenticación pero el rol no coincide** (p. ej., un administrador intenta acceder a `/tracking/**`), se lanza una `AccessDeniedException`, que es capturada por el `accessDeniedHandler` y devuelve un **403 Forbidden**.

Ambos manejadores fueron definidos en `SecurityConfig`:

```java
.exceptionHandling(exception -> exception
    .authenticationEntryPoint((request, response, authException) -> {
        response.setStatus(401);
        response.setContentType("application/json");
        response.getWriter().write(
            "{\"errorCode\":\"UNAUTHORIZED\",\"userMessage\":\"Requires authentication\"}"
        );
    })
    .accessDeniedHandler((request, response, accessDeniedException) -> {
        response.setStatus(403);
        response.setContentType("application/json");
        response.getWriter()
            .write("{\"errorCode\":\"FORBIDDEN\",\"userMessage\":\"Access denied\"}");
    })
)
```

Además, el `GlobalExceptionHandler` también está preparado para capturar estas mismas excepciones si llegaran a propagarse, proporcionando una capa adicional de seguridad.

### 4. Llegada al controlador y verificación adicional de identidad

Si la petición supera la autorización, el controlador correspondiente (por ejemplo, `DriverTripQueryController`, `BusLocationController`, etc.) recibe la solicitud. En ese punto, el controlador puede obtener el `busId` autenticado directamente del contexto de seguridad:

```java
private UUID getCurrentBusId() {
    return (UUID) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
}
```

Muchos de los controladores del módulo `tracking` utilizan este método para **asegurar que el conductor solo opere sobre su propio bus**, incluso si el frontend envía un `busId` diferente en la URL. Por ejemplo, en `BusLocationController`:

```java
@PostMapping("/{busId}/location")
public ResponseEntity<Void> updateBusLocation(
        @PathVariable UUID busId,
        @RequestBody UpdateBusLocationRequest request) {

    UUID currentBusId = getCurrentBusId();
    if (!currentBusId.equals(busId)) {
        throw new AccessDeniedException("Bus cannot update the location of another bus");
    }
    // ... lógica de negocio ...
}
```

Esta verificación adicional protege contra intentos de manipular el `busId` en la URL, añadiendo una capa de control de acceso a nivel de negocio, complementaria a la autorización basada en roles.

### Resumen de la Fase 2

1. El navegador adjunta automáticamente la cookie `access_token` en cada petición a `/tracking/**`.
2. El filtro `JwtAuthenticationFilter` extrae la cookie, valida el token (firma y expiración) y, si es correcto, establece el contexto de seguridad con el `busId` y el rol `DRIVER`.
3. La petición continúa por la cadena de filtros; el `FilterSecurityInterceptor` de Spring Security evalúa las reglas de `SecurityConfig`:
   - Si hay autenticación y el rol es `DRIVER`, permite el acceso.
   - Si no hay autenticación, devuelve un **401**.
   - Si hay autenticación pero el rol no es el adecuado, devuelve un **403**.
4. Los controladores obtienen el `busId` del contexto de seguridad y pueden realizar comprobaciones adicionales para evitar que un conductor acceda a recursos de otro bus.

Este diseño mantiene la aplicación **stateless**: cada petición porta su propia autenticación en el token, sin necesidad de sesiones en el servidor, y delega la autorización en la configuración de Spring Security.