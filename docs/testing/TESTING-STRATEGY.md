# Estrategia de Testing - Bus Tracking System

## Por que existe esta estrategia

Esta documentacion existe para que los miembros del proyecto, presentes y futuros, entiendan como se deben escribir y organizar los tests. Un proyecto sin estrategia de testing clara es un proyecto que va a gastar tiempo valioso buscando donde van las pruebas, como escribirlas, o peor aun, duplicando trabajo.

## Estructura de carpetas

La estructura de tests refleja exactamente la estructura del codigo fuente. Si existe una clase en `src/main/java/com/bustracking/admin/application/usecase/` entonces el test de esa clase va en `src/test/java/com/bustracking/admin/unit/application/usecase/`.

### Por que simetria

- No se pierde tiempo buscando donde va un test
- El test vive cerca del codigo que prueba
- Agregar modulos nuevos es facil, la estructura se expande naturalmente

### Por que es importante la claridad

Cuando una persona nueva llega al proyecto, debe entender en minutos donde buscar un test. Si la estructura es confusa, se pierde tiempo y se cometen errores. La claridad hace que todos escriban tests de la misma manera.

### Por que se puede mantener

Tests organizados no se vuelven un caos. Todo tiene su lugar. Cuando se necesita cambiar un test, se sabe exactamente donde buscarlo. No hay sorpresas.

## Piramide de tests

```
        Acceptance (5%)
       /              \
      /  E2E Tests    \
     /      (10%)      \
    /                   \
   / Functional Tests    \
  /        (20%)          \
 /                         \
/ Integration Tests (20%)   \
/                           \
----Test unitarios (45%)----
```

## Tipos de tests

### Unit Tests (Pruebas Unitarias)

Prueban una clase de forma aislada. No tocan la base de datos, no hacen llamadas HTTP. Usan mocks para aislar la clase.

**Ubicacion:** `src/test/java/[modulo]/unit/[capa]/`

**Velocidad:** Muy rapidos (milisegundos)

**Cobertura:** Alta (95% o mas)

**Cuando escribir:** Siempre. Toda clase debe tener tests unitarios.

### Integration Tests (Pruebas de Integracion)

Prueban un modulo completo. Tocan la base de datos (usando TestContainers), activan Spring Boot, pero NO hacen peticiones HTTP.

**Ubicacion:** `src/test/java/[modulo]/integration/`

**Velocidad:** Lentos (100-500 milisegundos por test)

**Cobertura:** Media (70-80%)

**Cuando escribir:** Cuando se necesita probar que la base de datos se comporta bien, que las transacciones funcionan, que los repositorios guardan datos correctamente.

### Functional Tests (Pruebas Funcionales)

Prueban un flujo completo de negocio. Multiples servicios trabajan juntos. La base de datos es real (TestContainers), pero aun NO son APIs completas.

**Ubicacion:** `src/test/java/functional/`

**Velocidad:** Lentos (500-1000 milisegundos por test)

**Cobertura:** Media (60-70%)

**Cuando escribir:** Cuando se necesita probar que 2 o mas servicios funcionan bien juntos. Por ejemplo, registrar una empresa Y asignarle buses.

### E2E Tests (Pruebas End-to-End)

Prueban APIs completas como si un cliente HTTP real estuviera haciendo peticiones. La respuesta debe ser correcta, el status HTTP debe ser correcto.

**Ubicacion:** `src/test/java/e2e/`

**Velocidad:** Lentos (500-1000 milisegundos por test)

**Cobertura:** Baja (30-40%)

**Cuando escribir:** Para los endpoints mas importantes. Aquellos que los clientes (frontend, aplicaciones externas) usan directamente.

### Acceptance Tests (Pruebas de Aceptacion)

Prueban requisitos del negocio en lenguaje del negocio. No son pruebas tecnicas. Son pruebas de "el usuario puede hacer esto".

**Ubicacion:** `src/test/java/acceptance/`

**Velocidad:** Lentos (500-1000 milisegundos por test)

**Cobertura:** Baja (10-20%)

**Cuando escribir:** Cuando se necesita validar comportamientos de negocio especificos. Por ejemplo, "un conductor puede actualizar su ubicacion y esta aparece en el mapa en menos de 2 segundos".

## Proporcion recomendada

- Unit Tests: 45%
- Integration Tests: 20%
- Functional Tests: 20%
- E2E Tests: 10%
- Acceptance Tests: 5%

## Patron AAA (Arrange-Act-Assert)

Todos los tests, sin excepcion, siguen este patron:

1. **Arrange (Preparar):** Se preparan los datos iniciales. Se crean mocks, se configuran valores.

2. **Act (Actuar):** Se ejecuta el codigo que se quiere probar.

3. **Assert (Verificar):** Se valida que el resultado es el esperado.

Este patron hace tests legibles y consistentes. Cualquiera que lea un test entiende que hace en segundos.

## Organizacion dentro de test unitarios

Si una clase tiene 3 metodos, entonces la clase de test tiene 3 secciones separadas:

```
ApproveCompanyUseCaseTest
  - Seccion 1: Tests del metodo execute()
    - Test 1: caso feliz
    - Test 2: validacion
    - Test 3: error
  - Seccion 2: Tests del metodo validate()
    - Test 1: dato valido
    - Test 2: dato invalido
```

Cada seccion agrupa todos los tests de ese metodo. Dentro de cada seccion se prueban todos los casos: exito, validaciones, excepciones.

## Organizacion dentro de servicios

Los servicios orquestan multiples Use Cases. Los tests del servicio deben verificar que:

1. El servicio llama los Use Cases correctos
2. Pasa los parametros correctos
3. Maneja excepciones
4. Los datos se persisten en la base de datos

Los Use Cases estan mockeados en Unit Tests, pero son reales en Integration Tests.

## Test Containers

Para Integration, Functional, E2E y Acceptance tests se usa TestContainers. Esto quiere decir que cada test levanta una base de datos de verdad (en Docker), hace el test, y la destruye.

Esto garantiza que los tests son confiables y no interfieren unos con otros.

## Locales vs CI/CD

Esta estrategia funciona tanto en la maquina local como en el CI/CD (GitHub Actions, GitLab CI, etc).

Los tests deben pasar siempre. Si pasan localmente pero no en CI/CD, hay un bug. Si un test es flaky (a veces pasa, a veces no), es un mal test.

## Referencias

Esta estrategia se basa en principios establecidos en:

- "The Pragmatic Programmer" de David Thomas y Andrew Hunt
- "Clean Code" de Robert C. Martin
- "The Test Pyramid" de Mike Cohn
- Documentacion oficial de Spring Boot Testing
