
## Fase 0 – Configuración inicial (arranque de la aplicación)

Cuando la aplicación Spring Boot se levanta, el contenedor de inversión de control construye y cablea todas las piezas que forman el sistema de seguridad. Esta fase no procesa ninguna petición, pero deja todo listo para que las siguientes fases funcionen. Los componentes principales que se instancian y cómo se relacionan se describen a continuación.

### 1. Propiedades de JWT (`JwtProperties`)

El módulo `shared` declara un *record* de configuración que se vincula automáticamente a las propiedades con prefijo `jwt` del archivo `application.properties`:

```java
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        long accessTokenExpiration,
        long refreshTokenExpiration
) {}
```

Los valores típicos son la clave secreta para firmar los tokens, el tiempo de vida del *access token* (15 minutos) y el del *refresh token* (7 días). Spring inyecta esta instancia en cualquier bean que la requiera.

### 2. Servicio de generación y validación de tokens (`JwtService`)

`JwtService` es un bean de servicio que se encarga de construir y verificar los JWT. En su constructor recibe `JwtProperties` y deriva la clave secreta:

```java
public JwtService(JwtProperties properties) {
    this.secretKey = Keys.hmacShaKeyFor(
            properties.secret().getBytes(StandardCharsets.UTF_8)
    );
    this.accessTokenExpiration = properties.accessTokenExpiration();
}
```

A partir de ese momento, `JwtService` ofrece métodos como `generateAccessToken(busId, role)`, `isTokenValid(token)`, `extractBusId(token)` y `extractClaim(...)`, que serán usados en las fases posteriores. La librería subyacente es `jjwt`, y la firma se realiza con HMAC-SHA256.

### 3. Servicio de *refresh tokens* con Redis (`RefreshTokenService`)

`RefreshTokenService` administra el ciclo de vida de los *refresh tokens* en Redis. Su constructor recibe una plantilla de acceso a Redis y el tiempo de expiración en milisegundos:

```java
public RefreshTokenService(
        StringRedisTemplate redis,
        @Value("${jwt.refresh-token-expiration}") long ttlMillis) {
    this.redis = redis;
    this.ttl = Duration.ofMillis(ttlMillis);
}
```

Internamente, cada *refresh token* se almacena como un hash SHA-256 del valor crudo, asociado a un JSON que contiene el `busId`, el rol y un estado (`ACTIVE`, `USED`, `REVOKED`). Este servicio se usará para guardar, validar, rotar y revocar tokens durante el *login*, el *refresh* y el *logout*.

### 4. Codificador de contraseñas (`PasswordEncoder`)

`SecurityConfig` expone un bean de tipo `BCryptPasswordEncoder`:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

Este bean se inyecta en `AuthenticateBusUseCase` para verificar la contraseña enviada por el conductor contra el hash almacenado en la base de datos.

### 5. Filtro de autenticación JWT (`JwtAuthenticationFilter`)

El filtro que intercepta cada petición HTTP es un componente de Spring anotado con `@Component`:

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    // ...
}
```

Spring lo instancia automáticamente y lo deja listo para ser registrado en la cadena de filtros.

### 6. Configuración de seguridad (`SecurityConfig`)

La clase `SecurityConfig` es una configuración de Spring Security (`@Configuration` + `@EnableWebSecurity`). En ella se define el `SecurityFilterChain` que establece las reglas de acceso, se agrega el filtro JWT y se declaran otros beans.

#### Cadena de filtros

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll()
            .requestMatchers("/tracking/**").hasRole(RoleAuth.DRIVER.name())
            .anyRequest().authenticated()
        )
        .exceptionHandling(exception -> exception
            .authenticationEntryPoint(...)
            .accessDeniedHandler(...)
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
}
```

Aquí se definen tres reglas fundamentales:
- `/auth/**` es público (login, refresh, logout).
- `/tracking/**` requiere el rol `DRIVER`.
- Cualquier otra ruta pide al menos autenticación.

Además, se deshabilita CSRF (porque la aplicación es stateless y no usa sesiones), se configura CORS para los orígenes de desarrollo, y se indica a Spring Security que no cree sesiones (`STATELESS`).

#### Registro del filtro JWT

La línea clave es:

```java
.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
```

Esto coloca el filtro personalizado **antes** del filtro estándar de autenticación, de modo que si el token es válido, el contexto de seguridad ya esté poblado cuando Spring Security evalúe las reglas de autorización.

#### Configuración de CORS

Otro bean en la misma clase define los orígenes permitidos:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    // ...
}
```

Esto permite que el frontend en desarrollo (Vite y Next.js) realice peticiones con cookies de forma segura.

### 7. Manejo centralizado de excepciones (`GlobalExceptionHandler`)

Aunque no participa en la autenticación, el `GlobalExceptionHandler` (anotado con `@RestControllerAdvice`) queda registrado en el contexto de Spring. Contiene manejadores para:

- `AuthenticationException` → 401
- `AccessDeniedException` → 403
- `BusinessRuleException` con `ErrorCode.INVALID_CREDENTIALS` → 401
- Otros errores de negocio → 422

Estos manejadores transforman las excepciones lanzadas por los filtros o los controladores en respuestas JSON estructuradas.

### 8. Repositorios y entidades JPA

En el módulo `tracking`, se levantan los repositorios necesarios para la autenticación:

- `BusCredentialJpaRepository` (Spring Data JPA)
- `BusCredentialRepositoryImpl` (implementación concreta del puerto del dominio)
- `BusLocationJpaRepository` (para ubicaciones)

Estos repositorios son utilizados por los casos de uso, pero su configuración se limita a la conexión con la base de datos `tracking`.

### 9. Redis

El contenedor de Spring Boot también configura la conexión a Redis a partir de las propiedades `spring.data.redis.*`. `RefreshTokenService` recibe un `StringRedisTemplate` listo para usar, con lo que la persistencia de los *refresh tokens* queda operativa.

### 10. Controladores y casos de uso

Los controladores como `AuthenticateDriverController`, `BusLocationController` y los `DriverTripCommandController`/`QueryController` son instanciados e inyectados con sus respectivos casos de uso. La inyección del `JwtService` y `RefreshTokenService` ya está resuelta, por lo que los puntos de entrada HTTP están preparados para recibir tráfico.

### Resumen

Al finalizar el arranque:

- El `SecurityFilterChain` está configurado con las reglas de acceso.
- El `JwtAuthenticationFilter` está en la posición correcta para interceptar cada petición.
- Los servicios `JwtService` y `RefreshTokenService` están listos para generar y validar tokens.
- El codificador de contraseñas puede verificar *hashes* BCrypt.
- Las excepciones de seguridad serán capturadas por el `GlobalExceptionHandler`.
- Redis y PostgreSQL están disponibles para almacenar *refresh tokens* y credenciales.

Con esta base, la aplicación puede proceder a la **Fase 1: Login**, donde se generarán los primeros tokens.


---
### Resumen Fase 0

Cuando Spring Boot inicia la aplicación, no crea los componentes al azar. El contenedor de Spring analiza todas las clases anotadas (`@Configuration`, `@Component`, `@Service`, `@Repository`, etc.) y construye cada objeto resolviendo primero sus dependencias. El resultado es un sistema completamente conectado antes de recibir la primera petición HTTP.

El flujo lógico puede entenderse así:

##### 1. Carga la configuración de la aplicación

El primer paso consiste en leer los archivos de configuración (`application.properties` o `application.yml`).

De ellos obtiene valores como:

- la clave secreta para firmar los JWT;
    
- el tiempo de vida del _access token_;
    
- el tiempo de vida del _refresh token_;
    
- la configuración de PostgreSQL;
    
- la configuración de Redis.
    

Estos valores se agrupan dentro del objeto `JwtProperties`, que actuará como fuente de configuración para cualquier componente que necesite trabajar con JWT.

---

##### 2. Construye los servicios que dependen de esa configuración

Una vez que existe `JwtProperties`, Spring ya puede crear los servicios que necesitan esa información.

Por ejemplo:

- `JwtService` utiliza la clave secreta y los tiempos de expiración para poder generar y validar tokens.
    
- `RefreshTokenService` necesita la conexión a Redis y el tiempo de expiración del _refresh token_ para administrar su almacenamiento.
    

En este punto todavía no existe ninguna petición HTTP; simplemente ya hay objetos preparados para trabajar cuando alguien los necesite.

---

##### 3. Configura la seguridad de toda la aplicación

Con los servicios anteriores disponibles, Spring procesa la clase `SecurityConfig`.

Esta clase funciona como el punto donde se define **cómo se protegerá toda la aplicación**.

Aquí se crean y configuran elementos como:

- el `PasswordEncoder`, que permitirá verificar contraseñas;
    
- la política de sesiones (`STATELESS`);
    
- la configuración CORS;
    
- las reglas que indican qué rutas son públicas y cuáles requieren autenticación o determinados roles.
    

Pero además ocurre algo muy importante: se registra el `JwtAuthenticationFilter` dentro de la cadena de filtros (`SecurityFilterChain`).

Ese filtro no podría registrarse si antes no hubiera sido creado junto con su dependencia (`JwtService`).

En otras palabras:

```
JwtProperties
        │
        ▼
 JwtService
        │
        ▼
JwtAuthenticationFilter
        │
        ▼
SecurityFilterChain
```

La cadena de seguridad queda construida utilizando componentes que fueron creados previamente.

---

##### 4. Se preparan el resto de componentes de la aplicación

Mientras la configuración de seguridad termina de construirse, Spring también crea el resto de los beans del proyecto.

Entre ellos:

- los repositorios JPA para acceder a PostgreSQL;
    
- los servicios del dominio;
    
- los casos de uso;
    
- los controladores REST;
    
- el `GlobalExceptionHandler`.
    

Cada uno recibe automáticamente las dependencias que necesita mediante inyección de dependencias.

Por ejemplo, un controlador no crea manualmente un caso de uso; Spring ya lo construyó previamente y simplemente lo inyecta en su constructor.

---

##### 5. Se establecen las conexiones con la infraestructura

Durante el arranque también se inicializan los componentes que permiten comunicarse con recursos externos.

En este proyecto destacan principalmente dos:

- PostgreSQL, donde se almacenan las credenciales y la información del dominio.
    
- Redis, donde se almacenan los _refresh tokens_.
    

Cuando `RefreshTokenService` necesite guardar un token, la conexión con Redis ya estará completamente disponible.

---

##### 6. La aplicación queda lista para atender peticiones

Una vez finalizado el proceso de inicialización, todos los componentes ya conocen con quién deben trabajar.

La relación completa puede visualizarse de la siguiente manera:

```text
application.properties
          │
          ▼
   JwtProperties
          │
          ▼
     JwtService
          │
          ▼
JwtAuthenticationFilter
          │
          ▼
 SecurityFilterChain
          │
          ▼
  Todas las peticiones HTTP
```

Al mismo tiempo, de forma paralela:

```text
PostgreSQL ─────► Repositorios ─────► Casos de uso ─────► Controladores

Redis ─────────► RefreshTokenService ───────────────────► Controladores

PasswordEncoder ───────────────────────────────────────► AuthenticateBusUseCase

GlobalExceptionHandler ◄──────────── Excepciones lanzadas durante el proceso
```

De esta forma, cuando llegue la primera petición de **login** (Fase 1), no será necesario crear ningún componente nuevo. Todo el sistema ya está conectado:

- `AuthenticateBusUseCase` puede utilizar `PasswordEncoder` para verificar la contraseña.
    
- `JwtService` puede generar y validar JWT utilizando la configuración cargada al inicio.
    
- `RefreshTokenService` puede almacenar el _refresh token_ en Redis.
    
- `SecurityFilterChain` ya sabe que el `JwtAuthenticationFilter` debe ejecutarse antes que el resto de filtros de Spring Security.
    
- Si ocurre algún error de autenticación o autorización, `GlobalExceptionHandler` lo convertirá en la respuesta HTTP correspondiente.
    

En otras palabras, **la fase de inicialización no autentica usuarios ni procesa solicitudes; su objetivo es construir una red de componentes donde cada pieza conoce exactamente de quién depende y quién la utilizará cuando comiencen a llegar las peticiones HTTP.**