## Levantamiento de Contextos

```plaintext
@DataJpaTest    → solo JPA + repositorios
@WebMvcTest     → solo web + controllers
@SpringBootTest → todo: web + JPA + services + security + configuraciones
```
 ## SpringBoot Test vs MockMVC:

@SpringBootTest y MockMvc son dos cosas distintas.:
@SpringBootTest levanta el contexto de Spring, o sea los beans, servicios, repositorios, configuraciones. Pero no configura ningún mecanismo para simular requests HTTP. Solo tiene la app "viva".
MockMvc es el cliente HTTP de prueba que intercepta requests antes de que salgan por la red. No es parte de Spring Boot en sí, es una herramienta de Spring Test que hay que configurar por separado.

Una analogía: es como encender un servidor pero no tener ningún cliente para hablarle. El servidor está corriendo, pero nadie le habla.
@SpringBootTest solo:
  → App levantada ✅
  → Cliente HTTP para tests ❌

@SpringBootTest + @AutoConfigureMockMvc:
  → App levantada ✅
  → Cliente HTTP para tests ✅


## mOCKmvc VS TestResTemplate:

No exactamente. Los dos siguen siendo backend, la diferencia es más sutil:

---

**`MockMvc`** intercepta el request **dentro** del proceso Java, sin pasar por la red:
```
Test → MockMvc → DispatcherServlet → Controller → Service → Repository → BD
```

**`TestRestTemplate`** levanta un servidor HTTP real en un puerto aleatorio y hace requests reales por la red:
```
Test → HTTP real → puerto 8081 → Controller → Service → Repository → BD
```

---

Con `TestRestTemplate` el request pasa por toda la pila de red igual que si fuera un cliente real. En ese sentido es más realista. Un frontend real o Postman harían exactamente lo mismo.

Pero no incluye el frontend, sigue siendo solo backend. La diferencia es si el HTTP es simulado o real.

---

Para un E2E de backend puro, `MockMvc` es suficiente y más rápido. `TestRestTemplate` tiene más sentido cuando querés probar cosas específicas de la red como filtros de seguridad a nivel de servlet, CORS, o comportamiento real del servidor HTTP. Para tu caso actual `MockMvc` es la elección correcta.



##  Spring Boot 4.0  reorganizó los paquetes de testing.
En Spring Boot 3.x todo vivía en un solo lugar:
org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
En Spring Boot 4.0 lo separaron en módulos distintos:
org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest

El patrón es consistente en Boot 4.0:
Qué esPaquete nuevoTodo lo de WebMvcorg.springframework.boot.webmvc.test.*Todo lo de JPAorg.springframework.boot.data.jpa.test.*Todo lo de JDBCorg.springframework.boot.jdbc.test.*