
## Fase 1 – Login (`POST /auth/login`)

En esta fase, el conductor envía su identificador de autobús y contraseña al backend, el sistema verifica las credenciales y, si son correctas, devuelve un *access token* y un *refresh token* dentro de cookies seguras. A continuación se detalla cada paso involucrado.

### 1. Recepción de la petición

El punto de entrada es el controlador `AuthenticateDriverController`, mapeado a `/auth/login`:

```java
@PostMapping("/login")
public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request) {
    TokensDTO tokens = authenticateBusUseCase.execute(
        UUID.fromString(request.busId()), request.password()
    );
    return buildTokenResponse(tokens);
}
```

El `LoginRequest` es un *record* que recoge el `busId` y la `password` enviados en el cuerpo JSON. La anotación `@Valid` activa las validaciones de formato, aunque la validación de las credenciales en sí ocurre dentro del caso de uso.

### 2. Verificación de credenciales (`AuthenticateBusUseCase`)

El controlador llama al método `execute` del servicio `AuthenticateBusUseCase`. Este caso de uso es el núcleo de la fase de login y orquesta varias comprobaciones:

```java
public TokensDTO execute(UUID busId, String rawPassword) {
    // 2.1 Buscar credenciales por busId
    Optional<BusCredential> optionalCredential =
        credentialRepository.findByBusId(busId);
    if (optionalCredential.isEmpty()) {
        throw new BusinessRuleException(
            ErrorCode.INVALID_CREDENTIALS,
            "Invalid credentials",
            "Bus not found or invalid credentials"
        );
    }

    BusCredential credential = optionalCredential.get();

    // 2.2 Verificar que no estén revocadas
    if (credential.isRevoked()) {
        throw new BusinessRuleException(
            ErrorCode.INVALID_CREDENTIALS,
            "Invalid credentials",
            "The credentials for this bus have been revoked"
        );
    }

    // 2.3 Comparar la contraseña
    if (!passwordEncoder.matches(rawPassword, credential.getPasswordHash())) {
        throw new BusinessRuleException(
            ErrorCode.INVALID_CREDENTIALS,
            "Invalid credentials",
            "The password does not match"
        );
    }

    // 2.4 Generar access token
    String accessToken = jwtService.generateAccessToken(busId, RoleAuth.DRIVER);

    // 2.5 Generar refresh token (valor aleatorio)
    String refreshToken = generateRefreshToken();

    // 2.6 Almacenar refresh token en Redis
    refreshTokenService.saveRefreshToken(busId, RoleAuth.DRIVER.name(), refreshToken);

    return new TokensDTO(accessToken, refreshToken);
}
```

Cada paso merece una explicación más detallada.

#### 2.1 Búsqueda de credenciales

El repositorio `BusCredentialRepository` se comunica con la base de datos a través de `BusCredentialJpaRepository`. La consulta busca una fila en la tabla `bus_credential` donde `bus_id` coincida. Si no existe, se lanza una excepción de negocio que el `GlobalExceptionHandler` convertirá en un HTTP 401.

#### 2.2 Estado de revocación

El modelo de dominio `BusCredential` tiene un campo `revokedAt`. Si no es nulo, significa que las credenciales fueron revocadas (por ejemplo, porque el autobús fue retirado). En ese caso también se lanza `BusinessRuleException` con `INVALID_CREDENTIALS`.

#### 2.3 Comparación de contraseñas

La contraseña enviada por el conductor (`rawPassword`) se compara con el hash almacenado usando el `PasswordEncoder` inyectado (BCrypt). Si no coinciden, igualmente se lanza `BusinessRuleException` con `INVALID_CREDENTIALS`. En todos estos casos, el sistema **no revela cuál fue el motivo exacto** (usuario no encontrado, revocado o contraseña incorrecta); siempre responde con un mensaje genérico "Invalid credentials", lo que evita filtrar información a un posible atacante.

#### 2.4 Generación del access token

`JwtService.generateAccessToken` construye un JWT con:
- Sujeto (`sub`): el `busId` en formato UUID.
- Claim `role`: `"DRIVER"` (valor del enum `RoleAuth.DRIVER`).
- Fecha de emisión y fecha de expiración (15 minutos después).
- Firma HMAC-SHA256 usando la clave secreta leída de las propiedades.

El token resultante es una cadena de tres secciones codificadas en Base64 (header, payload, firma).

#### 2.5 Generación del refresh token

El método privado `generateRefreshToken()` crea un valor aleatorio de 32 bytes, codificado en Base64 URL sin relleno. Este token no contiene información del usuario; es un simple identificador opaco.

```java
private String generateRefreshToken() {
    byte[] bytes = new byte[32];
    new SecureRandom().nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
}
```

#### 2.6 Almacenamiento del refresh token en Redis

`RefreshTokenService.saveRefreshToken` calcula el hash SHA-256 del token crudo, crea un objeto JSON con los datos del usuario (`busId`, `role`, estado `"ACTIVE"`) y lo guarda en Redis con clave `rt:{hash}` y un TTL de 7 días. La clave se deriva del hash para que, incluso si un atacante obtuviera acceso a la base de datos Redis, no pueda relacionar los tokens con usuarios concretos.

### 3. Construcción de la respuesta con cookies

De vuelta en el controlador, el método `buildTokenResponse` construye dos cookies seguras:

```java
private ResponseEntity<Void> buildTokenResponse(TokensDTO tokens) {
    ResponseCookie accessCookie = ResponseCookie.from("access_token", tokens.accessToken())
        .httpOnly(true)       // no accesible desde JavaScript
        .secure(false)        // true en producción (requiere HTTPS)
        .sameSite("Strict")   // protege contra CSRF
        .path("/")            // se envía en todas las peticiones al dominio
        .maxAge(900)          // 15 minutos
        .build();

    ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", tokens.refreshToken())
        .httpOnly(true)
        .secure(false)
        .sameSite("Strict")
        .path("/auth/refresh") // solo se envía a /auth/refresh
        .maxAge(604800)        // 7 días
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
        .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
        .build();
}
```

La respuesta HTTP no tiene cuerpo; solo incluye las cabeceras `Set-Cookie` que el navegador almacenará y adjuntará automáticamente en futuras peticiones según las reglas de `path` y `sameSite`.

### 4. Manejo de errores

Si el login falla por cualquier motivo (credenciales inválidas, bus revocado, etc.), el `GlobalExceptionHandler` captura la `BusinessRuleException` con código `INVALID_CREDENTIALS` y devuelve:

- Código HTTP: **401 Unauthorized**
- Cuerpo JSON:  
  ```json
  { "code": "INVALID_CREDENTIALS", "message": "Invalid credentials" }
  ```

El uso de `BusinessRuleException` con `ErrorCode.INVALID_CREDENTIALS` permite diferenciar este error de otras violaciones de reglas de negocio (que reciben 422), lo que hace que el frontend pueda actuar en consecuencia.

---

### Resumen de la Fase 1

Cuando el conductor (u otro tipo de usuario) inicia sesión por primera vez, envía sus credenciales de forma normal (busId y contraseña) al endpoint `POST /auth/login`. Esas credenciales son verificadas por el caso de uso `AuthenticateBusUseCase`, que consulta la base de datos, comprueba que las credenciales no estén revocadas y compara la contraseña con el hash almacenado mediante BCrypt.

Una vez validadas, se generan dos tokens:

- **Access token**: no es simplemente una cadena cualquiera, sino un **JWT (JSON Web Token)**. Está compuesto por tres partes (cabecera, cuerpo y firma) codificadas en Base64URL, pero **no está encriptado**. Su contenido (el busId y el rol, entre otros) es legible por cualquiera que lo decodifique; la seguridad reside en la **firma HMAC-SHA256** que se calcula usando una clave secreta del servidor. Si alguien intentara modificar el token (por ejemplo, cambiar el busId), la firma dejaría de coincidir y el servidor lo rechazaría. Este token tiene una vida corta (15 minutos) y se utiliza para autorizar cada petición a los endpoints protegidos.

- **Refresh token**: no es un JWT, sino un **valor aleatorio de 32 bytes codificado en Base64URL**. Es completamente opaco, no contiene información del usuario. Se guarda en Redis **como un hash SHA-256** del valor original, junto con el busId y el rol. La idea de «rotar» el refresh token significa que cada vez que se usa para obtener un nuevo access token, el antiguo se invalida y se genera uno nuevo. Así, si un atacante roba un refresh token, solo puede usarlo una vez; el legítimo dueño, al ver que su token ya no funciona, sabrá que hubo un problema. Redis aplica un tiempo de expiración (7 días) para que los tokens no vivan indefinidamente.

Los dos tokens se devuelven al controlador, pero **no se envían en el cuerpo de la respuesta**. El método `buildTokenResponse()` construye dos **cookies seguras** (`access_token` y `refresh_token`) con atributos como `HttpOnly`, `SameSite=Strict` y rutas específicas. De esta forma, el navegador las almacena y las adjunta automáticamente en las siguientes peticiones sin que el código JavaScript del frontend pueda acceder a ellas, lo que protege contra ataques XSS.

En las peticiones posteriores (Fase 2), el frontend no tiene que hacer nada especial: el navegador incluye la cookie `access_token` automáticamente. El filtro `JwtAuthenticationFilter` extrae esa cookie, el servicio `JwtService` valida el token (verifica la firma, la fecha de expiración y extrae el busId y el rol), y a continuación se establece el contexto de seguridad de Spring. Finalmente, la configuración de `SecurityConfig` comprueba que el rol sea el adecuado para la ruta solicitada (por ejemplo, `ROLE_DRIVER` para `/tracking/**`). De este modo, en cada acción del usuario **no se vuelven a enviar las credenciales**; el token actúa como una identificación temporal.

La gran ventaja de este diseño es que **evita que la contraseña viaje repetidamente** —ni en la URL, ni en el cuerpo, ni en las cabeceras— cada vez que el conductor consulta sus viajes, confirma una parada o actualiza su ubicación. Además, el servidor no necesita mantener sesiones en memoria: la información necesaria (quién eres y qué rol tienes) ya está dentro del access token firmado, lo que permite que el backend sea **estadeless** (sin estado) y escale más fácilmente.

Por último, cada vez que se hace login **sí se deben enviar las credenciales** de nuevo. El proceso comienza desde cero: se validan usuario y contraseña, se generan tokens nuevos y se almacena un nuevo refresh token en Redis. El refresh token simplemente permite extender la sesión sin pedir la contraseña otra vez, pero cuando expira o se revoca, el conductor tendrá que autenticarse otra vez con sus credenciales.

Este flujo establece la sesión del conductor sin mantener estado en el servidor (más allá del refresh token en Redis) y sienta las bases para que el resto de peticiones a endpoints protegidos de `/tracking/**` sean autorizadas mediante el access token (Fase 2).


---



### Posibles Aclaraciones sobre el flujo de autenticación y autorización

###### 1. ¿Quién verifica que la firma del token sea correcta?

La verificación de la firma la realiza la librería **jjwt** internamente, pero es invocada desde el método `parseToken()` del servicio `JwtService`:

```java
public Claims parseToken(String token) {
    return Jwts.parser()
            .verifyWith(secretKey)   // aquí se establece la clave para verificar la firma
            .build()
            .parseSignedClaims(token) // aquí se comprueba la firma y se decodifica el payload
            .getPayload();
}
```

Cuando el filtro necesita validar el token, llama a `isTokenValid(token)`, que a su vez invoca `parseToken`. Si la firma no coincide, la librería lanza una excepción (por ejemplo, `SignatureException`) que `isTokenValid` captura y convierte en `false`:

```java
public boolean isTokenValid(String token) {
    try {
        return !isTokenExpired(token);
    } catch (Exception e) {
        return false;
    }
}
```

Por tanto, la clase responsable es `JwtService`, y el método clave es `parseToken`.


###### 2. ¿Cómo es el flujo completo desde que la petición entra hasta que llega a los controladores?

El flujo sigue estos pasos en orden:

1. **El navegador envía automáticamente la cookie `access_token`** junto a la petición HTTP.
2. **El filtro `JwtAuthenticationFilter`** (hereda de `OncePerRequestFilter`) intercepta la petición. Su método `doFilterInternal` extrae la cookie, y si el token es válido (`jwtService.isTokenValid`), extrae el `busId` y el rol, y construye un objeto `Authentication` que se coloca en el `SecurityContextHolder`.
3. **La petición continúa por la cadena de filtros** gracias a `filterChain.doFilter(request, response)`. Después del filtro JWT, Spring Security procesa la petición en sus propios filtros (entre ellos el que decide si la solicitud está autorizada).
4. **La autorización basada en roles ocurre en el `SecurityFilterChain`** definido en `SecurityConfig`. Spring Security evalúa las reglas configuradas:
   ```java
   .authorizeHttpRequests(auth -> auth
       .requestMatchers("/auth/**").permitAll()
       .requestMatchers("/tracking/**").hasRole(RoleAuth.DRIVER.name())
       .anyRequest().authenticated()
   )
   ```
   En este momento, el `SecurityContextHolder` ya contiene el `Authentication` con las autoridades (`ROLE_DRIVER`). Spring Security comprueba si el usuario tiene el rol exigido para la ruta. Si no, lanza `AccessDeniedException` y el `GlobalExceptionHandler` devuelve un 403.
5. **Si la autorización es correcta, la petición llega al controlador** (por ejemplo, `BusLocationController`). El controlador puede obtener el `busId` directamente del contexto de seguridad sin volver a leer el token.

Es decir: el filtro JWT no "pasa el token" a SecurityConfig, sino que **establece la autenticación en el contexto de seguridad**. Luego, la propia infraestructura de Spring Security (ya configurada) se encarga de verificar los roles contra las reglas definidas.


###### 3. ¿Qué hace esta línea `filterChain.doFilter(request, response);`?

Esta línea invoca el siguiente filtro en la cadena de filtros de Spring Security (y eventualmente el servlet que maneja la petición).

- `request`: la solicitud HTTP que llega al servidor.
- `response`: la respuesta HTTP que se enviará de vuelta al cliente.
- `filterChain`: es un objeto proporcionado por el contenedor de filtros que representa la secuencia de filtros que deben ejecutarse. Al llamar a `doFilter`, le estamos diciendo al contenedor: «He terminado mi trabajo, pasa la petición al siguiente filtro».

Si un filtro no invoca `filterChain.doFilter(...)`, la petición se detiene y nunca llega al controlador. Por eso en el filtro JWT, independientemente de si el token es válido o no, siempre se llama a `doFilter` al final del método, para que la petición pueda continuar y sea Spring Security quien decida si se permite o se deniega el acceso.


###### 4. ¿Qué hace exactamente `doFilterInternal` en `JwtAuthenticationFilter` y por qué es importante?

El método `doFilterInternal` es el corazón del filtro. Se ejecuta **una vez por cada petición HTTP** (porque `OncePerRequestFilter` garantiza que no se ejecute múltiples veces en una misma solicitud). Su implementación es:

```java
@Override
protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

    // 1. Extraer el token de la cookie "access_token"
    String token = extractTokenFromCookie(request);

    // 2. Si hay token y es válido, autenticar
    if (token != null && jwtService.isTokenValid(token)) {

        UUID busId = jwtService.extractBusId(token);
        String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));

        var authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                busId,      // principal (el identificador del usuario)
                null,       // credenciales (no se necesitan porque ya está autenticado)
                authorities // lista de permisos/roles
        );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));

        // Establecer la autenticación en el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 3. Continuar con la cadena de filtros (siempre)
    filterChain.doFilter(request, response);
}
```

**Importancia:**
- Es el punto de entrada de la autenticación JWT. Transforma un token (cadena de texto) en una identidad reconocida por Spring Security (`Authentication`).
- Si el token es válido, el resto de la aplicación (controladores, servicios) puede confiar en que el usuario está autenticado y obtener su `busId` y rol desde `SecurityContextHolder`.
- Si el token no es válido o no existe, el contexto permanece vacío y será `SecurityConfig` quien rechace la petición si la ruta requiere autenticación. El filtro nunca rechaza peticiones directamente; siempre deja que la cadena continúe, delegando la decisión de denegar el acceso a los mecanismos de autorización de Spring Security. Esto mantiene el filtro simple y desacoplado de las reglas de negocio.



###### 5. ¿Deberíamos delegar la excepción de `isTokenValid` al `GlobalExceptionHandler`?

No, no es necesario ni recomendable. El diseño actual de `isTokenValid` captura cualquier excepción y devuelve `false`. Esa elección es deliberada y mantiene el filtro simple:

- El método `parseToken` lanza excepciones si la firma no es válida, el formato es incorrecto o el token expiró. `isTokenValid` envuelve esas excepciones y devuelve `false`, evitando que el filtro se llene de lógica de manejo de errores.
    
- Si en lugar de devolver `false` se dejara que las excepciones se propagaran, el filtro tendría que capturarlas y convertirlas en una respuesta HTTP 401, mezclando responsabilidades (autenticación con autorización). Además, el `GlobalExceptionHandler` no podría capturarlas fácilmente porque las excepciones en filtros ocurren antes de que el `DispatcherServlet` entre en acción (el `@RestControllerAdvice` actúa sobre controladores, no sobre filtros). Por tanto, la estrategia actual es correcta: el filtro solo intenta autenticar; si falla, deja el contexto vacío y deja que Spring Security autorice o rechace según las reglas de acceso.



###### 6. Si el filtro siempre hace `filterChain.doFilter`, ¿cómo sabe Spring Security que la ruta requiere autenticación?

Aunque el filtro JWT no rechaza la petición directamente, la cadena de filtros continúa. **Después** de `JwtAuthenticationFilter`, la petición pasa por otros filtros de Spring Security, en particular el `FilterSecurityInterceptor`. Este interceptor es el encargado de hacer cumplir las reglas definidas en `SecurityFilterChain`.

Cuando la petición llega a ese interceptor, ocurre lo siguiente:

- Se inspecciona el `SecurityContextHolder` para ver si hay una `Authentication` presente.
    
- Se compara la petición con las reglas configuradas: para `/tracking/**` se requiere `hasRole('DRIVER')`, para otras rutas `authenticated()`, etc.
    
- Si **no hay autenticación** (contexto vacío) y la ruta requiere autenticación, se lanza una `AuthenticationException` o `AccessDeniedException`.
    
- Esas excepciones son capturadas por los `exceptionHandling` configurados en `SecurityConfig` (el `authenticationEntryPoint` para 401 y el `accessDeniedHandler` para 403), que escriben la respuesta JSON de error.
    

Por tanto, `SecurityConfig` “sabe” que la ruta requiere autenticación porque las reglas están declaradas en el `SecurityFilterChain`. El filtro JWT solo prepara el contexto; la decisión de denegar o permitir el acceso la toma Spring Security más adelante en la cadena.


El `SecurityContextHolder` estará vacío (sin autenticación) a menos que el filtro JWT encuentre un token válido y ejecute explícitamente:

```java
SecurityContextHolder.getContext().setAuthentication(authentication);
```

Como la política de sesiones es `STATELESS`, Spring Security no guarda ninguna autenticación previa entre peticiones. Por tanto, la única fuente posible de autenticación en cada petición es ese bloque condicional dentro de `doFilterInternal`. Si no se cumple la condición (`token != null && jwtService.isTokenValid(token)`), el contexto permanece vacío. Luego, cuando la cadena de filtros llega al `FilterSecurityInterceptor`, Spring Security detecta que no hay autenticación y, como la ruta `/tracking/**` la requiere, deniega el acceso lanzando una excepción que se traduce en un 401.