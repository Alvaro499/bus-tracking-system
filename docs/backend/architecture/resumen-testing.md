Searched for files matching `**/backend/src/test/**/*Test.java`, 13 matches

Read [](file:///c%3A/Users/AlvaroSQ/Documents/BusTrackingProject/bus-tracking-system/backend/pom.xml#1-1), lines 1 to 150

Read [](file:///c%3A/Users/AlvaroSQ/Documents/BusTrackingProject/bus-tracking-system/backend/src/test/java/com/bustracking/tracking/unit/domain/BusLocationTest.java#1-1), lines 1 to 50

Read [](file:///c%3A/Users/AlvaroSQ/Documents/BusTrackingProject/bus-tracking-system/backend/src/test/java/com/bustracking/tracking/integration/repository/BusLocationRepositoryTest.java#1-1), lines 1 to 50

Read [](file:///c%3A/Users/AlvaroSQ/Documents/BusTrackingProject/bus-tracking-system/backend/src/test/java/com/bustracking/shared/testinfrastructure/RepositoryIntegrationTest.java#1-1), lines 1 to 50

Read [](file:///c%3A/Users/AlvaroSQ/Documents/BusTrackingProject/bus-tracking-system/backend/src/test/java/com/bustracking/tracking/integration/controller/BusLocationControllerTest.java#1-1), lines 1 to 50

Read [](file:///c%3A/Users/AlvaroSQ/Documents/BusTrackingProject/bus-tracking-system/backend/src/test/java/com/bustracking/tracking/e2e/BusLocationE2ETest.java#1-1), lines 1 to 50

Read [](file:///c%3A/Users/AlvaroSQ/Documents/BusTrackingProject/bus-tracking-system/backend/src/test/java/com/bustracking/shared/testinfrastructure/E2EIntegrationTest.java)

Aquí va el resumen de librerías y tecnologías de testing:

---

## 📦 Stack de Testing - BusTrack CR

### **Dependencias principales (pom.xml)**

```xml
<!-- JUnit 5 (Jupiter) - incluido en spring-boot-starter-test -->
<!-- Usado para: @Test, @BeforeEach, assertions, parametrizadas -->

<!-- MockitoBean - Spring Boot 4.0+ lo cambió -->
<!-- ⚠️ Antes: @MockBean (deprecado)
     Ahora: @MockitoBean (nuevo) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- DataJpaTest - Spring Boot 4.0 lo separó -->
<!-- ⚠️ Antes: incluido en spring-boot-starter-test
     Ahora: en su propio módulo -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- WebMvcTest - Spring Boot 4.0 lo separó -->
<!-- ⚠️ Antes: incluido en spring-boot-starter-test
     Ahora: en su propio módulo -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Testcontainers: Docker + PostgreSQL en tests -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.20.6</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.20.6</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.20.6</version>
    <scope>test</scope>
</dependency>
```

---

## 🧪 Tipos de tests

### **1. UNIT TESTS** (sin BD, sin Spring)
```java
// Ejemplo: BusLocationTest.java
public class BusLocationTest {
    @Test
    void shouldCreateBusLocationWithValidData() { ... }
}

// Import mínimos:
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
```
- **¿Qué usa?** JUnit 5 (Jupiter)
- **¿Qué moquea?** Nada (lógica pura)
- **Velocidad:** ⚡ Muy rápido (< 1 seg total)

---

### **2. INTEGRATION TESTS** (@DataJpaTest + Testcontainers)
```java
// Ejemplo: BusLocationRepositoryTest.java
@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BusLocationRepositoryTest extends RepositoryIntegrationTest {
    @Autowired
    private BusLocationJpaRepository repo;
}

// Imports:
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;
```
- **¿Qué usa?** Testcontainers + PostgreSQL real en Docker
- **¿Qué prueba?** Persistencia (mappings, constraints, queries)
- **Velocidad:** 🐢 Más lento (levanta DB)
- **¿Carga fixtures?** Sí (@Sql automático)

---

### **3. CONTROLLER TESTS** (@WebMvcTest + Mockito)
```java
// Ejemplo: BusLocationControllerTest.java
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

// ⚠️ Import actualizado para Spring Boot 4.0:
// Antes: import org.springframework.boot.test.mock.mockito.MockBean;
// Ahora: import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(controllers = {BusLocationController.class})
class BusLocationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private GetBusLocationUseCase getBusLocationUseCase;
    
    @Test
    void shouldReturnOkWhenLocationExists() {
        when(getBusLocationUseCase.execute(any())).thenReturn(...);
        mockMvc.perform(get("/tracking/buses/{id}/location", busId))
            .andExpect(status().isOk());
    }
}

// Imports:
import org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
```
- **¿Qué usa?** MockMvc + Mockito (NO BD real)
- **¿Qué prueba?** HTTP responses, status codes, JSON
- **Mockea:** Use cases (inyectados con @MockitoBean)
- **Velocidad:** ⚡ Rápido (no toca DB)

---

### **4. E2E TESTS** (@SpringBootTest + Testcontainers)
```java
// Ejemplo: BusLocationE2ETest.java
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@Sql({
    "/test-data/fixtures-shared.sql",
    "/test-data/tracking-fixtures.sql"
})
class BusLocationE2ETest extends E2EIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldPostAndGetBusLocation() {
        mockMvc.perform(post("/tracking/buses/{id}/location", busId)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""{"lat": 9.9, "lng": -84.0}"""))
            .andExpect(status().isCreated());
    }
}

// Imports:
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
```
- **¿Qué usa?** Todo: Docker + PostgreSQL + Spring Context completo
- **¿Qué prueba?** Flujo completo (controller → service → repository → BD)
- **Mockea:** Nada (todo real excepto external APIs)
- **Velocidad:** 🐢 Lento pero representa el sistema real
- **Usa fixtures:** Sí (@Sql carga datos)

---

## 🐳 Testcontainers (Docker)

```java
// Base class reutilizable
@Testcontainers
public abstract class RepositoryIntegrationTest {
    
    @Container  // ← Un solo contenedor por suite de tests
    protected static final PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("bustracking_db_test")
            .withUsername("bustracking_test_user")
            .withPassword("test_password_random");
    
    @DynamicPropertySource  // ← Inyecta la URL en spring.datasource.url
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
```

**¿Qué hace?**
1. Levanta PostgreSQL 16 Alpine en Docker
2. Inyecta JDBC URL dinámica en Spring
3. Un contenedor para TODOS los tests (eficiencia)
4. Se destruye automáticamente al terminar

---

## 🎯 Resumen por tipo de test

| Tipo | Librerías | Mock? | BD Real? | Velocidad | Scope |
|------|-----------|-------|----------|-----------|-------|
| **Unit** | JUnit 5 | No | No | ⚡ < 1s | Lógica pura |
| **Integration (Repo)** | DataJpaTest + Testcontainers | No | ✅ Docker | 🐢 5-10s | Persistencia |
| **Controller** | WebMvcTest + Mockito | ✅ Use Cases | No | ⚡ < 1s | HTTP |
| **E2E** | SpringBootTest + Testcontainers | No | ✅ Docker | 🐢 5-10s | Flujo completo |

---

## ⚠️ Cambios Spring Boot 4.0 (comparado con versiones anteriores)

| Libería | Antes | Ahora |
|---------|-------|-------|
| `@MockBean` | `@MockBean` | `@MockitoBean` (deprecado el anterior) |
| DataJpaTest | Incluido en `spring-boot-starter-test` | Módulo separado: `spring-boot-starter-data-jpa-test` |
| WebMvcTest | Incluido en `spring-boot-starter-test` | Módulo separado: `spring-boot-starter-webmvc-test` |
| Testcontainers | v1.17 | v1.20.6 (más estable) |

Similar code found with 1 license type