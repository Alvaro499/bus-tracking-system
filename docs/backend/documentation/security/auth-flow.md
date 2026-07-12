##  Flujo completo de JWT + credenciales + Redis

Te lo cuento cronológicamente para que recuperes el hilo:

**a) Login (`POST /auth/login`)**  
El conductor envía `busId` y `password`.  
`AuthenticateBusUseCase`:

- Busca las credenciales del bus en la BD.
    
- Verifica que no estén revocadas y que la contraseña coincida (con BCrypt).
    
- Si todo bien, crea dos tokens:
    
    - **Access token** (JWT firmado con HMAC-SHA256, incluye `busId` y `role`, expira en 15 min).
        
    - **Refresh token** (string aleatorio de 32 bytes).
        
- **Guarda el refresh token en Redis** usando `RefreshTokenService.saveRefreshToken(busId, refreshToken)`. Internamente, Redis almacena una clave `rt:<hash>` con un JSON que contiene `busId`, `issuedAt` y `status=ACTIVE`, con TTL de 7 días.
    
- Devuelve ambos tokens en cookies `access_token` y `refresh_token` al conductor.
    

**b) Peticiones protegidas (ej: `GET /tracking/trips/today`)**  
El conductor envía la cookie `access_token`.  
`JwtAuthenticationFilter` extrae el token, lo valida (firma, expiración) y, si es correcto, autentica al usuario en el contexto de seguridad.  
Spring Security evalúa las reglas de autorización (`.hasRole(RoleAuth.DRIVER.name())`) y, si el rol coincide, permite el acceso al controlador.

**c) Refresco de tokens (`POST /auth/refresh` – aún no implementado, Paso 6)**  
Cuando el access token expira, el conductor envía la cookie `refresh_token`.  
`RefreshTokenUseCase` (próximo paso) tomará ese refresh token, lo validará contra Redis (hash, estado activo, no reusado) y, si es válido, **rotará** el refresh token (marcará el viejo como usado y creará uno nuevo) y emitirá un nuevo access token.  
Esto mantiene la sesión viva sin pedir usuario/contraseña otra vez.

**d) Logout (`POST /auth/logout` – pendiente)**  
El conductor envía el refresh token; el sistema lo marca como revocado en Redis y borra las cookies.  
Así el refresh token ya no podrá usarse para generar nuevos access tokens.

**¿Dónde entra Redis?**  
Redis almacena el refresh token con un TTL igual a su expiración (7 días). Es rápido, permite rotación segura (detección de reúso), y sus claves se autodestruyen sin intervención manual.