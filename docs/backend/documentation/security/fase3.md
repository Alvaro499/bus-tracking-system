## Fase 3 – Renovación del access token (`POST /auth/refresh`)

Cuando el *access token* expira (a los 15 minutos), el frontend no puede seguir accediendo a los endpoints protegidos porque el filtro `JwtAuthenticationFilter` lo rechazaría. Sin embargo, en lugar de obligar al conductor a volver a escribir su contraseña, el sistema utiliza el *refresh token* para emitir un nuevo *access token* (y un nuevo *refresh token*) de forma transparente. Esta fase describe ese proceso.

### 1. Petición del frontend

El frontend realiza una petición `POST` al endpoint `/auth/refresh`. El navegador adjunta automáticamente la cookie `refresh_token` porque en la fase de login se configuró con `path="/auth/refresh"`. La petición no necesita cuerpo; la única información requerida es el *refresh token* que viaja en la cookie.

### 2. Controlador (`AuthenticateDriverController`)

El método `refresh` del controlador extrae el token de la cookie y lo pasa al caso de uso correspondiente:

```java
@PostMapping("/refresh")
public ResponseEntity<Void> refresh(@CookieValue("refresh_token") String refreshToken) {
    TokensDTO tokens = refreshTokenUseCase.execute(refreshToken);
    return buildTokenResponse(tokens);
}
```

El resultado es un `TokensDTO` con el nuevo *access token* y un *refresh token* rotado, que se envían al navegador mediante el mismo método `buildTokenResponse` explicado en la Fase 1. Las cookies se sobrescriben con los nuevos valores y tiempos de expiración.

### 3. Caso de uso `RefreshTokenUseCase`

Este caso de uso, ubicado en el módulo `shared`, coordina la validación del *refresh token* y la generación del nuevo *access token*:

```java
@Service
public class RefreshTokenUseCase {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public RefreshTokenUseCase(RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    public TokensDTO execute(String rawRefreshToken) {
        // 3.1 Validar y rotar el refresh token en Redis
        RefreshTokenResult result = refreshTokenService.validateAndRotateRefreshToken(rawRefreshToken);

        // 3.2 Convertir el rol almacenado a RoleAuth y generar un nuevo access token
        RoleAuth role = RoleAuth.valueOf(result.role());
        String newAccessToken = jwtService.generateAccessToken(result.busId(), role);

        // 3.3 Devolver ambos tokens (el nuevo access token y el nuevo refresh token)
        return new TokensDTO(newAccessToken, result.newRawToken());
    }
}
```

El método `execute` se apoya completamente en `RefreshTokenService` para la validación y rotación, y en `JwtService` para crear el *access token*.

### 4. Servicio `RefreshTokenService` – validación y rotación

El núcleo de la lógica de refresco está en el método `validateAndRotateRefreshToken`:

```java
public RefreshTokenResult validateAndRotateRefreshToken(String rawToken) {
    // 4.1 Calcular el hash SHA-256 del token recibido
    String hash = hashToken(rawToken);
    String key = PREFIX + hash;   // PREFIX = "rt:"

    // 4.2 Buscar en Redis
    String json = redis.opsForValue().get(key);
    if (json == null) {
        throw new BusinessRuleException(
                ErrorCode.UNAUTHORIZED,
                "Refresh token invalid or expired");
    }

    // 4.3 Deserializar los datos del token
    TokenData tokenData;
    try {
        tokenData = objectMapper.readValue(json, TokenData.class);
    } catch (JsonProcessingException e) {
        throw new RuntimeException("Error during serialization token", e);
    }

    // 4.4 Verificar que el token esté en estado ACTIVE
    if (!"ACTIVE".equals(tokenData.status)) {
        // Si ya fue USED, significa que alguien está intentando reutilizar un token
        throw new BusinessRuleException(
                ErrorCode.UNAUTHORIZED,
                "Refresh token ya fue utilizado");
    }

    // 4.5 Rotación: marcar el token actual como USED
    tokenData.status = "USED";
    try {
        redis.opsForValue().set(key, objectMapper.writeValueAsString(tokenData), ttl);
    } catch (JsonProcessingException e) {
        throw new RuntimeException("Error al actualizar token", e);
    }

    // 4.6 Generar un nuevo refresh token (raw) y guardarlo en Redis
    UUID userId = UUID.fromString(tokenData.userId);
    String role = tokenData.role;
    String newRawToken = generateRawToken();
    saveRefreshToken(userId, role, newRawToken);

    // 4.7 Devolver los datos del usuario y el nuevo token raw
    return new RefreshTokenResult(userId, role, newRawToken);
}
```

A continuación se detalla cada paso:

#### 4.1 Hash del token

El *refresh token* nunca se almacena en Redis en su forma original. Primero se calcula su hash SHA-256, y se usa ese hash como clave (`rt:{hash}`). Así, incluso si un atacante obtuviera acceso a la base de datos Redis, no podría relacionar los tokens con los usuarios directamente, y menos aún obtener el valor original necesario para llamar al endpoint de refresco.

#### 4.2 Búsqueda en Redis

Si la clave no existe en Redis, el token es inválido o ya ha expirado. Se lanza una `BusinessRuleException` con `ErrorCode.UNAUTHORIZED`. El `GlobalExceptionHandler` la traduce a un **401 Unauthorized**.

#### 4.3 Deserialización de los datos

El valor almacenado en Redis es un JSON con la siguiente estructura interna (clase privada `TokenData`):

```java
private static class TokenData {
    public String userId;
    public String role;
    public String issuedAt;
    public String status;
    // ...
}
```

Contiene el identificador del bus, el rol y el estado actual del token (`ACTIVE`, `USED` o `REVOKED`).

#### 4.4 Verificación del estado

Solo los tokens en estado `ACTIVE` pueden usarse para refrescar. Si el estado es `USED`, significa que ya fue empleado anteriormente. Esto es un indicio de que el token podría haber sido robado y reutilizado. Por seguridad, se lanza una excepción y no se emite un nuevo token.

#### 4.5 Rotación: marcar como USED

Antes de emitir un nuevo *refresh token*, el token actual se marca como `USED` en Redis. Esta técnica se conoce como **rotación de refresh tokens**. De esta forma, cada *refresh token* solo es válido para una única operación de refresco; el token anterior queda invalidado inmediatamente. Si un atacante interceptara un *refresh token*, solo podría utilizarlo una vez, y el conductor legítimo se daría cuenta al siguiente intento de refresco fallido.

#### 4.6 Generación y almacenamiento del nuevo refresh token

Se genera un nuevo *refresh token* aleatorio (32 bytes en Base64URL) mediante el mismo método que en el login:

```java
private String generateRawToken() {
    byte[] bytes = new byte[32];
    new java.security.SecureRandom().nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
}
```

A continuación, se almacena en Redis con los mismos `userId` y `role`, estado `ACTIVE` y TTL de 7 días, usando el método `saveRefreshToken`.

#### 4.7 Resultado

El método devuelve un `RefreshTokenResult` (record interno) con el `busId`, el rol y el nuevo *refresh token* en texto plano. Este último será el que se envíe al navegador en la nueva cookie.

### 5. Generación del nuevo access token

De vuelta en `RefreshTokenUseCase`, se convierte el rol (`String`) a `RoleAuth` y se llama a `jwtService.generateAccessToken(busId, role)`. El nuevo *access token* se genera con una fecha de emisión actual y expiración a 15 minutos.

### 6. Respuesta al navegador

El controlador construye de nuevo las cookies con `buildTokenResponse`:

- La cookie `access_token` se actualiza con el nuevo JWT.
- La cookie `refresh_token` se actualiza con el nuevo *refresh token* rotado.
- Los atributos de seguridad (`HttpOnly`, `SameSite`, `path`, etc.) se mantienen idénticos a los de la fase de login.

A partir de ese momento, el navegador usará el nuevo *access token* para las siguientes peticiones, y el *refresh token* anterior ya no será válido.

### Manejo de errores

Si el *refresh token* falta, no está en Redis, o su estado no es `ACTIVE`, se lanza una `BusinessRuleException` con `ErrorCode.UNAUTHORIZED`. El `GlobalExceptionHandler` la convierte en una respuesta **401 Unauthorized**, con un cuerpo JSON estándar.

---

### Resumen de la Fase 3

1. El frontend llama a `POST /auth/refresh`; el navegador adjunta la cookie `refresh_token` (gracias a su `path` restringido).
2. El controlador `AuthenticateDriverController` extrae el token y lo pasa a `RefreshTokenUseCase`.
3. `RefreshTokenUseCase` delega en `RefreshTokenService.validateAndRotateRefreshToken()`:
   - Calcula el hash SHA-256 del token y lo busca en Redis.
   - Verifica que el estado sea `ACTIVE`.
   - Marca el token actual como `USED` (rotación).
   - Genera un nuevo *refresh token* aleatorio, lo guarda en Redis con estado `ACTIVE` y TTL de 7 días.
   - Retorna el `busId`, el rol y el nuevo *refresh token* en texto plano.
4. Con el rol recuperado, `RefreshTokenUseCase` genera un nuevo *access token* mediante `JwtService`.
5. El controlador envía las nuevas cookies al navegador, sobrescribiendo las anteriores.
6. Si el *refresh token* es inválido, expirado o ya fue usado, se responde con un **401 Unauthorized**.

Este mecanismo permite que la sesión del conductor se mantenga activa durante 7 días sin necesidad de volver a introducir la contraseña, a la vez que protege contra robos de tokens gracias a la rotación.







### Posibles Dudas

###### 1. ¿Qué implica que el refresh token no exista?

Si el refresh token no existe en Redis, el método `validateAndRotateRefreshToken` lanza una `BusinessRuleException` con `ErrorCode.UNAUTHORIZED`. Esto se traduce en un HTTP 401 Unauthorized para el frontend. Las causas posibles:

- El token nunca fue emitido (alguien envió un valor inventado).
    
- El token expiró (el TTL de 7 días se cumplió y Redis lo eliminó automáticamente).
    
- El token fue revocado explícitamente durante un logout (se marcó como `REVOKED` y eventualmente expira, o se eliminó).
    
- Se produjo una rotación anterior que ya consumió el token original.
    

En cualquiera de estos casos, el usuario debe volver a iniciar sesión con sus credenciales.


###### 2. Estados del refresh token y cómo puede estar inactivo

El `RefreshTokenService` define tres estados posibles en el campo `status` del objeto `TokenData` almacenado en Redis:

- **ACTIVE**: El token está listo para ser usado una vez. Es el estado que se asigna al crear un nuevo refresh token (en el login o tras una rotación exitosa). Solo los tokens en estado `ACTIVE` pueden usarse para refrescar.
    
- **USED**: Después de que un token es utilizado exitosamente para refrescar, se marca como `USED`. Este token ya no puede volver a usarse. Si alguien intenta usarlo de nuevo, el método `validateAndRotateRefreshToken` detecta que el estado no es `ACTIVE` y lanza una excepción. Esto previene ataques de reutilización.
    
- **REVOKED**: Se asigna durante el logout. El método `revokeRefreshToken` busca el token y cambia su estado a `REVOKED`. Un token revocado tampoco puede usarse para refrescar; cualquier intento resulta en un 401.
    

El token puede estar "inactivo" (no `ACTIVE`) por las siguientes razones:

- Ya fue usado (estado `USED`).
    
- Fue revocado manualmente al hacer logout (estado `REVOKED`).
    
- Nunca fue `ACTIVE` (por ejemplo, un token inventado no tendrá entrada en Redis).
    

---

###### 3. ¿Cómo sabe el frontend que el access token expiró y debe llamar a `/auth/refresh`? ¿Es el backend quien lo detecta?

La detección se hace en el frontend, aunque el backend proporciona la información necesaria.

- El backend no redirige automáticamente ni notifica proactivamente. Simplemente, cuando un access token expira o es inválido, cualquier petición a un endpoint protegido (`/tracking/**`) obtiene una respuesta **401 Unauthorized** (o a veces 403 si el token es válido pero el rol no coincide).
    
- El frontend debe implementar un mecanismo de interceptación de respuestas (por ejemplo, con Axios interceptors o similar en el caso de aplicaciones JavaScript). Cuando recibe un 401, asume que el access token expiró y procede a llamar a `POST /auth/refresh` con la cookie `refresh_token`. Si el refresco es exitoso (200 con nuevas cookies), reintenta la petición original. Si el refresco también falla (401), redirige al usuario al login.
    

Por tanto, el backend no "sabe" que el access token expiró; simplemente responde con 401. Es el frontend quien reacciona a ese código de estado.

---

###### 4. ¿Se debería utilizar `@Transactional` en un futuro test de flujo que incluya métodos de `RefreshTokenService`?

No es necesario, porque `RefreshTokenService` no utiliza JPA ni bases de datos relacionales. Su almacenamiento es Redis, que no participa en transacciones de Spring gestionadas con `@Transactional`. Las operaciones sobre Redis son atómicas en sí mismas (como `SET`, `GET`). Por lo tanto, en los tests de integración o de flujo, no se necesita `@Transactional` para probar los métodos de `RefreshTokenService`. Si el test combina operaciones de base de datos y Redis, la anotación `@Transactional` podría seguir usándose para la parte de base de datos, pero no tendrá efecto sobre Redis.

En los tests del flujo de refresh, basta con levantar el contexto de Spring Boot con Redis de prueba (Testcontainers) y verificar las respuestas HTTP, sin preocuparse por transacciones.

---

###### 5. ¿Cómo es que un string aleatorio se convierte en un `TokenData` con estado y demás?

Efectivamente, el refresh token generado es un simple string aleatorio (32 bytes en Base64URL). Lo que ocurre es que, después de generarlo en `AuthenticateBusUseCase`, se llama a:

java

refreshTokenService.saveRefreshToken(busId, RoleAuth.DRIVER.name(), refreshToken);

El método `saveRefreshToken` crea un objeto `TokenData` con los campos `userId`, `role`, `issuedAt` y `status = "ACTIVE"`, lo serializa a JSON y lo guarda en Redis con la clave `rt:{hash}`. De modo que el valor aleatorio **actúa como identificador o clave**, y los datos asociados (busId, rol, estado) se almacenan en Redis vinculados al hash de ese identificador.

Cuando después el frontend envía el refresh token en la cookie, el backend:

1. Calcula el hash del string recibido.
    
2. Busca en Redis la clave `rt:{hash}`.
    
3. Si existe, deserializa el JSON y obtiene el `TokenData` con toda la información (busId, rol, estado).
    
4. Si el estado es `ACTIVE`, procede a la rotación y devuelve los datos necesarios.
    

Así, un string sin significado se convierte en un "token" con información asociada únicamente por el almacenamiento en Redis. La relación es: **el string aleatorio es la llave, y el valor en Redis es el contenido estructurado.**
