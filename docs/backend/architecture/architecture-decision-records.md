### BusTrack CR — Backend Service

> Documento de decisiones arquitectónicas del sistema BusTrack CR, desarrollado como proyecto  de portafolio.

---

## Estilo arquitectónico (Arquitectura Externa)

### Monolito modular

Se optó por un monolito modular dado que el sistema es de tamaño pequeño-mediano, es desarrollado por una sola persona y se encuentra en etapa de MVP. Esta decisión prioriza la simplicidad operativa y la velocidad de desarrollo sin sacrificar la organización interna del código.

Internamente, el sistema se divide en módulos bien delimitados: `tracking`, `companies` y `admin`. Cada módulo encapsula su propia lógica de dominio, aplicación e infraestructura, siguiendo Clean Architecture. Esta separación permite extraer un módulo como servicio independiente en el futuro sin necesidad de reescribir el sistema desde cero.

Los microservicios fueron descartados por las siguientes razones:

- No se identifican componentes que necesiten escalar de forma independiente en esta etapa.
- El equipo de desarrollo es de una sola persona, lo que hace que la complejidad operativa de los microservicios no se justifique.
- La geolocalización, aunque tiene naturaleza más dinámica, no requiere una arquitectura orientada a eventos completa. Se maneja dentro del mismo monolito mediante actualización periódica de coordenadas.

A futuro, si se incorporan funcionalidades como notificaciones push o un módulo de demanda dinámica de rutas, se evaluaría extraerlos como servicios independientes en ese momento.

#### Comparación: monolito clásico vs monolito modular

El monolito clásico organiza el código por tipo de componente (todos los controladores juntos, todos los servicios juntos), lo que genera acoplamiento entre dominios distintos a medida que el sistema crece. El monolito modular organiza el código por dominio de negocio, donde cada módulo es autónomo internamente.

```
Monolito clásico          Monolito modular
─────────────────         ─────────────────
src/                      src/
├── controllers/          ├── tracking/
├── services/             │   ├── controller/
└── repositories/         │   ├── service/
                          │   └── repository/
                          ├── companies/
                          └── admin/
```

---


## Arquitectura interna

### Clean Architecture

Cada módulo aplica Clean Architecture internamente, organizando el código en tres capas con dependencias que apuntan siempre hacia adentro:

```
Infrastructure  →  Application  →  Domain
```

- **Domain** — contiene los modelos de negocio, interfaces de repositorio y reglas del dominio. No depende de ninguna otra capa ni de frameworks externos.
- **Application** — contiene los casos de uso que orquestan la lógica de negocio. Depende únicamente del dominio.
- **Infrastructure** — contiene los adaptadores concretos: controladores REST, entidades JPA, repositorios, configuración de seguridad. Depende de las capas internas pero nunca al revés.

Esta separación garantiza que la base de datos, el framework web y cualquier dependencia externa sean detalles intercambiables sin afectar la lógica de negocio.

---

## Separación de módulos

### Governance vs operations

La separación en módulos responde no solo a criterios técnicos sino también a responsabilidades de negocio distintas entre sí.

El módulo `admin` representa la **gobernanza de la plataforma**: controla quién puede operar en el sistema, aprueba o rechaza empresas y tiene visibilidad global. Sus acciones son puntuales y de alto impacto.

El módulo `companies` representa la **operación distribuida**: cada empresa gestiona sus propios datos (buses, rutas, horarios, paradas) sin visibilidad ni acceso a los datos de otras empresas. Sus acciones son frecuentes y de alcance limitado a su propio contexto.

El módulo `tracking` representa la **capa de tiempo real**: consume datos maestros de `companies` (buses, rutas, paradas) y agrega lógica de ejecución (trips activos, ubicación actual). Su naturaleza de lectura optimizada y escritura frecuente lo diferencia de los otros dos módulos.

Mantener estos módulos separados reduce el riesgo de errores de seguridad producidos por la convivencia de código con distintos niveles de acceso y responsabilidad.

---

## Comunicación entre componentes

### REST y polling

La comunicación del sistema se divide en dos patrones según la naturaleza de cada módulo.

Los módulos de gestión (`companies` y `admin`) utilizan **REST**. Las operaciones de estos módulos son puntuales y no requieren inmediatez: registrar una ruta, aprobar una empresa o editar un horario son acciones donde una respuesta en milisegundos no es crítica.

El módulo de tracking utiliza **polling HTTP** desde el frontend, consultando el servidor cada N segundos para obtener la última posición conocida de cada bus. Este mecanismo es suficiente para mostrar la ubicación de un bus en movimiento sin requerir infraestructura adicional.

|Característica|Polling|WebSockets|
|---|---|---|
|Complejidad|Baja|Alta|
|Infraestructura|REST estándar|Servidor con soporte WS|
|Latencia|Igual al intervalo elegido|Mínima|
|Caso de uso|Ubicación de buses|Chat, trading en tiempo real|

WebSockets se considera una evolución futura si la latencia del polling resulta insuficiente en producción.

---

## Distribución del sistema

### Componentes y su entorno de ejecución

El sistema se compone de tres piezas que corren en entornos distintos:

|Componente|Entorno|Descripción|
|---|---|---|
|App pública|Navegador|Mapa y consulta de rutas para usuarios finales|
|Panel de gestión|Navegador|Administración de empresa y plataforma|
|Backend|Servidor|API REST, lógica de negocio y base de datos|
|App del bus|Navegador (PWA)|Transmisión de ubicación GPS desde el bus|

### App del bus: PWA sobre app nativa

La app del bus se implementará como PWA por las siguientes razones:

|Criterio|PWA|App nativa|
|---|---|---|
|Publicación|No requiere tienda|Requiere Google Play / App Store|
|Compatibilidad|Cualquier dispositivo con navegador|Depende del SO|
|Complejidad de desarrollo|Baja|Alta|
|Adecuada para este proyecto|Sí|No justificada|

---


## Base de datos

### PostgreSQL como base de datos principal

El sistema gestiona datos altamente estructurados con relaciones estrictas entre sí: empresas, buses, rutas, paradas, horarios y viajes. Este modelo relacional hace de PostgreSQL la opción natural para el MVP.

El único módulo con escrituras frecuentes es `dispatch`, donde los buses transmiten su ubicación cada N segundos. Sin embargo, el volumen real no justifica una base de datos adicional en esta etapa. Considerando un escenario realista para Costa Rica de aproximadamente 250 buses transmitiendo simultáneamente cada 5 segundos, se obtienen alrededor de 1.500 escrituras por minuto, carga que PostgreSQL maneja sin problema. Además, no se almacena historial de coordenadas: solo se sobreescribe la última posición conocida de cada bus, manteniendo siempre una sola fila por unidad.

#### Por qué no Oracle ni SQL Server

Ambas son opciones válidas técnicamente pero están orientadas a entornos corporativos con licencias costosas. PostgreSQL ofrece las mismas garantías de integridad, transacciones ACID y soporte para relaciones complejas, con la ventaja de ser open source y ampliamente adoptado en la industria moderna.

#### Ventajas aplicadas a este sistema

- Datos estructurados con relaciones claras entre entidades.
- Integridad referencial estricta: un bus no puede existir sin empresa, un viaje no puede existir sin horario.
- Transacciones ACID necesarias para cambios de estado en viajes y horarios.
- Modelo de datos predecible y sin variaciones dinámicas de estructura.

### Evolución futura: Polyglot Persistence

Si se incorpora un módulo de analytics para empresas (historial de retrasos, tiempos de llegada, cumplimiento de horarios), se evaluará agregar MongoDB bajo el patrón Polyglot Persistence:

```
PostgreSQL   →  datos estructurados
                empresas, buses, rutas, horarios, paradas

MongoDB      →  datos históricos de operaciones
                coordenadas por recorrido, tiempos entre paradas,
                analytics de retrasos y llegadas
```

Esta migración es viable sin reescribir el sistema gracias a Clean Architecture, donde la base de datos es un detalle de infraestructura. Solo se reemplazaría el adaptador del módulo `operations`, sin afectar el resto del sistema.

---

## Stack tecnológico

### Frontend: Next.js + TypeScript

Next.js con TypeScript como framework principal del frontend. Next.js está basado en React y agrega manejo de rutas por estructura de carpetas, layouts independientes por sección y middleware de autenticación, lo que se adapta naturalmente a un sistema con tres contextos aislados: app pública, panel de empresa y panel de administración. TypeScript agrega tipado estático que reduce errores en tiempo de desarrollo.

### Backend: Java + Spring Boot

Spring Boot como framework backend. Es el estándar en la industria empresarial para APIs REST, con soporte nativo para inyección de dependencias, seguridad y persistencia. Su estructura explícita facilita la aplicación de Clean Architecture sin requerir decisiones de diseño adicionales.

| Framework | Razón de descarte |
|---|---|
| Django (Python) | Su arquitectura MTV entra en conflicto con Clean Architecture |
| ASP.NET (C#) | Despliegue gratuito más limitado que Java en Render y Railway |
| Node.js (Express) | Sin estructura definida, requiere más decisiones manuales de arquitectura |

### Mapas: Leaflet + OpenStreetMap

Leaflet.js sobre datos de OpenStreetMap. Ambas herramientas son open source, gratuitas y no requieren API key ni tarjeta de crédito, a diferencia de Google Maps o Mapbox que tienen planes gratuitos limitados y requieren método de pago registrado.

---

## Testing

### Estrategia general

El backend aplica cuatro niveles de prueba organizados en un espejo de la estructura de módulos del proyecto:

```
src/test/java/com/bustracking/
├── operations/
│   ├── unit/
│   ├── integration/
│   └── e2e/
├── companies/
│   ├── unit/
│   ├── integration/
│   └── e2e/
├── admin/
│   ├── unit/
│   ├── integration/
│   └── e2e/
└── shared/
    └── testinfrastructure/
        ├── RepositoryIntegrationTest.java
        ├── ControllerIntegrationTest.java
        └── E2EIntegrationTest.java
```

### Niveles de prueba

| Nivel | Herramientas | BD real | Mockeo | Alcance |
|---|---|---|---|---|
| Unit | JUnit 5 | No | No | Lógica de dominio y casos de uso |
| Integration (repositorio) | DataJpaTest + Testcontainers | Docker | No | Persistencia y constraints |
| Integration (controlador) | WebMvcTest + Mockito | No | Use cases | HTTP, status codes, JSON |
| E2E | SpringBootTest + Testcontainers | Docker | No | Flujo completo |

### Herramientas y librerías

- **JUnit 5** — framework base de pruebas unitarias.
- **Mockito / @MockitoBean** — mockeo de dependencias en pruebas de controlador.
- **Testcontainers** — levanta una instancia real de PostgreSQL en Docker durante las pruebas de integración y E2E.
- **MockMvc** — pruebas de controladores HTTP sin levantar servidor real.
- **@Sql** — carga de fixtures de datos para pruebas de integración y E2E.

### Nota sobre Spring Boot 4.0

Spring Boot 4.0 separó algunos módulos que antes venían incluidos en `spring-boot-starter-test`. Las dependencias de `DataJpaTest` y `WebMvcTest` ahora requieren módulos independientes, y `@MockBean` fue reemplazado por `@MockitoBean`.

### Frontend

Cypress con TypeScript para pruebas de integración del frontend. Usa el mismo lenguaje del proyecto, lo que evita introducir un segundo stack de testing.

---

## Despliegue

### Free tier con integración continua

El sistema se despliega en plataformas con planes gratuitos, suficientes para un proyecto de portafolio. Todas ofrecen integración con GitHub para despliegue automático con cada cambio en el repositorio.

| Componente | Plataforma |
|---|---|
| Frontend + App del bus (PWA) | Vercel |
| Backend (Spring Boot) | Render |
| Base de datos (PostgreSQL) | Supabase |
| Base de datos futura (MongoDB) | MongoDB Atlas |

Docker encapsula cada componente garantizando que el comportamiento sea idéntico en desarrollo local y en producción.

---