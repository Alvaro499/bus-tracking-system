# Estrategia de Testing — Bus Tracking System

## Justificación

Sin estrategias claras, los tests se vuelven un caos, porque cada persona los escribe diferente, nadie sabe dónde van, y el proyecto termina con pruebas duplicadas o sin pruebas donde importan. Este documento define cómo se organizan y escriben los tests en este proyecto.

---

## Estructura de carpetas

La estructura de tests es un espejo exacto del código fuente.

```
src/
├── main/java/com/bustracking/
│   ├── tracking/
│   │   ├── application/usecase/
│   │   ├── domain/
│   │   └── infrastructure/
│   └── shared/
│
└── test/java/com/bustracking/
    ├── tracking/
    │   ├── unit/
    │   │   ├── usecase/
    │   │   └── domain/
    │   ├── integration/
    │   │   ├── repository/
    │   │   └── controller/
    │   └── e2e/
    └── shared/
        ├── unit/
        └── testinfrastructure/   ← clases base compartidas
```

Si existe `GetBusLocationUseCase` en `tracking/application/usecase/`, su test vive en `tracking/unit/usecase/`. Sin excepciones.

---

## Los tres tipos de test

Este proyecto usa tres tipos de test. No más.

Términos como *functional test*, *acceptance test*, *smoke test* o *component test* existen en la literatura pero cada autor los define diferente. Para evitar confusión, todo cabe en estas tres categorías:

### Unit Tests

Prueban una sola clase en aislamiento. Sin base de datos, sin HTTP, sin Spring. Las dependencias externas se reemplazan con mocks.

Hay dos variantes:

**Sin mocks** — cuando la clase no tiene dependencias externas:
```
GpsCoordinateTest   → valida lat/lng, no depende de nada
BusLocationTest     → valida el modelo de dominio
```

**Con mocks** — cuando la clase depende de repositorios u otros servicios:
```
GetBusLocationUseCaseTest    → mockea BusLocationRepository y BusExistsById
UpdateBusLocationUseCaseTest → ídem
```

La regla: si el método es público, se testea. Si es privado, no se toca directamente.

**Ubicación:** `[modulo]/unit/[capa]/`
**Velocidad:** milisegundos

---

### Integration Tests

Prueban que dos o más capas funcionan juntas. Se dividen en dos subcategorías según qué se está integrando:

**Repository integration** — BD real con TestContainers, sin HTTP:
```
BusLocationRepositoryTest → verifica que el UPSERT funciona contra PostgreSQL real
```

**Controller integration** — HTTP real con MockMvc, use cases mockeados, sin BD:
```
BusLocationControllerTest → verifica status codes, JSON de respuesta, casos de error HTTP
```

Nota técnica: el BusLocationControllerTest es estrictamente un slice test, no un integration test en el sentido clásico del término. Un integration test real implica un componente externo real como una base de datos. El controller test usa @WebMvcTest que carga solo la capa web de Spring con el use case mockeado, sin BD ni componente externo. Se agrupa bajo integration/controller/ por convención y simplicidad organizativa, para evitar introducir una cuarta categoría que agregaría complejidad sin beneficio práctico en un equipo pequeño. Quien llegue al proyecto debe saber que los tests en integration/controller/ no prueban integración real con infraestructura, sino el comportamiento HTTP de la capa web.

**Ubicación:** `[modulo]/integration/repository/` y `[modulo]/integration/controller/`
**Velocidad:** segundos

---

### E2E Tests

Prueban un flujo completo de negocio de punta a punta. BD real con TestContainers, HTTP real con MockMvc, sin mocks en ninguna capa. Un test E2E por historia de negocio, no por endpoint.

La pregunta para decidir si algo es E2E: *¿puedo describir este test como una historia de negocio en una oración?*

```
"Un bus envía su ubicación y un usuario puede consultarla"
→ POST /tracking/buses/{busId}/location
→ GET  /tracking/buses/{busId}/location
```

Los casos de error no van en E2E. Ya están cubiertos en los integration tests de controller. El E2E solo prueba el camino feliz.

**Ubicación:** `[modulo]/e2e/`
**Velocidad:** segundos (más lento que los anteriores porque levanta el contexto completo)

---

## Proporción recomendada

Basado en la regla 80/20: la mayoría del valor está en los tests más rápidos y baratos.

```
Unit Tests        → 80%
Integration Tests → 15%
E2E Tests         →  5%
```

---

## Infraestructura compartida

Las clases base viven en `shared/testinfrastructure/` y no contienen tests propios:

| Clase | Extienden | Para qué |
|---|---|---|
| `RepositoryIntegrationTest` | `@DataJpaTest` + TestContainers | Base para repository integration tests |
| `ControllerIntegrationTest` | `@WebMvcTest` | Base para controller integration tests |
| `E2EIntegrationTest` | `@SpringBootTest` + TestContainers | Base para E2E tests |

El contenedor de PostgreSQL es `static` en las clases base que lo usan. Un solo contenedor se levanta por suite de tests y se destruye al final. Levantar un contenedor por clase sería innecesariamente lento.

---

## Fixtures SQL

Los datos de prueba se cargan con `@Sql` antes de cada test y se deshacen automáticamente con el ROLLBACK de `@DataJpaTest`.

Los fixtures están separados por módulo para que cada test cargue solo lo que necesita:

```
src/test/resources/test-data/
├── fixtures-shared.sql    → company (necesaria por todos los módulos como FK)
└── tracking-fixtures.sql  → buses del módulo tracking
```

Un `CompanyRepositoryTest` solo necesita `fixtures-shared.sql`. Un `BusLocationRepositoryTest` necesita ambos porque los buses referencian una company.

---

## Patrón AAA

Todos los tests siguen Arrange-Act-Assert sin excepción:

```java
@Test
void shouldReturnBusLocationWhenBusExists() {
    // Arrange
    when(getBusLocationUseCase.execute(validBusId)).thenReturn(validBusLocation);

    // Act & Assert
    mockMvc.perform(get("/tracking/buses/{busId}/location", validBusId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.busId").value(validBusId.toString()));
}
```

---

## Decisiones de diseño

**¿Por qué no H2?**
H2 no reproduce el comportamiento de PostgreSQL. Tipos como `UUID`, `JSONB` o constraints específicos fallan de formas distintas. TestContainers usa la misma imagen de PostgreSQL que producción.

**¿Por qué no `@MockBean` de Spring Boot?**
Spring Boot 4.0 lo reemplazó con `@MockitoBean` de Spring Framework 6.2. El paquete cambió de `org.springframework.boot.test.mock.mockito` a `org.springframework.test.context.bean.override.mockito`.

**¿Por qué `@DataJpaTest` y no `@SpringBootTest` para repositorios?**
`@DataJpaTest` carga solo la capa JPA, es más rápido y más enfocado. `@SpringBootTest` carga todo el contexto y es necesario solo para E2E.

---

## Nota sobre terminología: Functional y Acceptance Tests

En muchos libros y proyectos aparecen estos términos adicionales. Se documentan aquí para evitar confusión si alguien los encuentra en otra fuente.

**Functional Tests** son un término ambiguo. Dependiendo del autor pueden referirse a:
- Tests de integración entre múltiples servicios
- Tests de API completos (lo que este proyecto llama E2E)
- Tests de casos de uso sin HTTP

En este proyecto todo lo que sería un *functional test* cae en integration o E2E según corresponda.

**Acceptance Tests** son pruebas escritas en lenguaje de negocio que validan criterios de aceptación de una historia de usuario. Herramientas como Cucumber los implementan en formato Given/When/Then. En este proyecto los criterios de aceptación se validan a través de los E2E tests sin una capa adicional de abstracción. Si el proyecto crece y se necesita comunicación más formal con stakeholders no técnicos, esta categoría podría agregarse usando Cucumber sobre la infraestructura de E2E existente.


## Nota sobre terminología: Pruebas pequeñas, medianas y grandes

Esta estrategia surgió como respuesta al problema común en equipos de desarrollo, que es la inconsistencia en la definición y el objetivo de cada tipo de prueba. Distintos autores, libros y equipos usan nombres diferentes para referirse a las mismas cosas, o el mismo nombre para referirse a cosas distintas, lo que genera confusión y malas prácticas.

Se tomó como referencia principal la documentación de Google (Software Engineering at Google, Capítulo 11) y los contenidos del curso de Arquitectura de Software de la carrera de Informática Empresarial, donde las pruebas se clasifican de forma más simple por tamaño: pequeñas, medianas y grandes. Y paara hacer un acercamiento más cercano al lenguaje usado en la industria actual, se mapearon esas categorías a los términos unit, integration y E2E:*

| Google | Este proyecto | Característica |
|---|---|---|
| Pequeña | Unit test | Un proceso, sin red, sin disco, solo lógica y los multiples "inputs" y "outputes" que puedan haber |
| Mediana | Integration test | Puede tocar BD local o stack HTTP de Spring |
| Grande | E2E test | App completa levantada, múltiples capas reales |

Dentro de las pruebas medianas, la comunidad usa términos como slice test, repository test o controller test según qué capa se está probando. Se decidió agruparlos bajo integration para evitar introducir una cuarta categoría que agregaría complejidad sin beneficio práctico.

A nivel de E2E existe una distinción adicional que este proyecto no implementa aún: E2E de backend puro, que es lo que se tiene actualmente, versus E2E con frontend incluido, que agrega complejidad de CORS, seguridad de tokens y comportamiento del navegador.

---

## Convención de nombres para métodos de test

### Patrón: "should"

Todos los métodos de test siguen el patrón `should[resultado]When[condición]`:

```java
@Test
void shouldReturnBusLocationWhenBusExistsAndHasLocation() {
    // Arrange, Act, Assert
}

@Test
void shouldThrowNotFoundExceptionWhenBusDoesNotExist() {
    // Arrange, Act, Assert
}

@Test
void shouldCreateCoordinateWithValidValues() {
    // Arrange, Act, Assert
}
```

### Por qué este patrón

1. **Sin prefijo "test"**: La anotación `@Test` de JUnit5, el nombre de la clase (`XyzTest`) y la ubicación en `src/test/java/` ya lo hacen explícito. Agregar `test` al inicio es redundante.

2. **Convención Java**: El patrón usa `camelCase` puro, que es el estándar en Java. No usar `snake_case` (`test_como_esto`) porque va contra las convenciones idiomáticas del lenguaje.

3. **Legibilidad**: Leer "should return bus location when bus exists" es más natural que "test_notificationApprovedWhenProjectHasUserUnitsIsBiggerThanZeroAnd..." (ilegible).

4. **CamelCase**: Los IDEs, refactoring tools y desarrolladores Java están acostumbrados a este formato.

### Cuándo el nombre es demasiado largo

Si un método de test necesita un nombre muy largo (>80 caracteres), es **señal de que el test está probando demasiado**. En lugar de agregar más condiciones al nombre, refactoriza en múltiples tests:

```java
// ❌ Demasiadas condiciones
void shouldCalculateDiscountWhenCustomerTypeIsPremiumAndPurchaseAmountIsGreaterThan1000AndReferralCodeIsValidAndPromoIsActive() {
}

// ✅ Refactorizado
void shouldCalculateDiscountWhenCustomerIsPremium() { }
void shouldApplyReferralBonus() { }
void shouldCombinePromoCodes() { }
```

### DisplayName para contexto adicional

Si necesitas proporcionar contexto humano más detallado (sin cambiar el nombre del método), usa `@DisplayName` de JUnit5:

```java
@Test
@DisplayName("Should return bus location when bus exists and has location in the database")
void shouldReturnBusLocationWhenBusExists() {
    // Arrange, Act, Assert
}
```

El `@DisplayName` aparece en reportes de test y en el IDE sin hacer el nombre del método ilegible.

---
