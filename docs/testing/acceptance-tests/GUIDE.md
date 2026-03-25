
# Acceptance Tests Guide

**Versión:** 1.0

---

## 1. Propósito

Este documento define las convenciones y lineamientos para la implementación de **Acceptance Tests** dentro del proyecto *Bus Tracking System*.

Su objetivo es asegurar que los requisitos del negocio:

* Sean verificables de forma automática
* Sean comprensibles por perfiles no técnicos
* Funcionen como documentación viva del sistema

---

## 2. Alcance

Los Acceptance Tests:

* Validan comportamientos completos del sistema desde una perspectiva externa
* Se ejecutan sobre la aplicación en funcionamiento (Spring Boot levantado)
* Utilizan HTTP como medio de interacción principal
* Cubren exclusivamente flujos críticos del negocio

No sustituyen otros tipos de pruebas como:

* Unit Tests
* Integration Tests
* Functional Tests
* End-to-End Tests

---

## 3. Ubicación en el Proyecto

Ruta establecida:

```
src/test/java/com/bustracking/acceptance/
```

Cada test debe representar un requisito de negocio claramente identificable.

---

## 4. Principios Generales

### 4.1 Lenguaje de Negocio

Los Acceptance Tests deben utilizar terminología propia del dominio, evitando lenguaje técnico interno.

Se prioriza que cualquier persona del negocio pueda comprender qué se está validando sin necesidad de conocimiento técnico.

---

### 4.2 Enfoque en Comportamiento

Las pruebas deben centrarse en validar resultados observables del sistema, no detalles de implementación.

El interés principal es verificar qué puede hacer el usuario y qué resultado obtiene.

---

### 4.3 Independencia de la Implementación

Los tests deben tratar el sistema como una caja negra.
No deben depender de clases internas, estructuras o decisiones de diseño.

---

### 4.4 Legibilidad

Un Acceptance Test debe ser autoexplicativo.
El nombre del test y su estructura deben permitir entender el escenario sin necesidad de documentación adicional.

---

## 5. Convenciones de Nombres

### 5.1 Clases

Formato recomendado:

```
[Actor][Accion][Resultado]Test
```

Las clases deben representar una capacidad del sistema desde la perspectiva del negocio.

---

### 5.2 Métodos

Se debe utilizar el patrón BDD:

```
given[Contexto]_when[Accion]_then[Resultado]
```

El nombre del método debe describir claramente el escenario completo.

---

## 6. Estructura del Test

Todos los Acceptance Tests deben seguir una estructura clara basada en tres fases:

* **ARRANGE**: configuración del escenario
* **ACT**: ejecución de la acción principal
* **ASSERT**: validación del resultado observable

### Esqueleto base:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ExampleAcceptanceTest {

    @Test
    void givenContext_whenAction_thenExpectedResult() {

        // ARRANGE
        // Preparación del escenario de negocio

        // ACT
        // Ejecución de la acción del usuario

        // ASSERT
        // Validación del resultado esperado
    }
}
```

---

## 7. Criterios de Validación

Un Acceptance Test debe validar:

### 7.1 Flujos principales del negocio

Escenarios que representan el uso real del sistema por parte de los usuarios.

---

### 7.2 Resultados observables

Cambios visibles o verificables desde el exterior del sistema.

---

### 7.3 Reglas de negocio críticas

Restricciones o condiciones relevantes para el dominio.

---

### 7.4 Requisitos no funcionales (cuando aplique)

Principalmente tiempos de respuesta u otros criterios definidos por el negocio.

---

## 8. Exclusiones

No deben incluirse en Acceptance Tests:

* Detalles internos del sistema (servicios, repositorios, etc.)
* Validaciones técnicas específicas
* Casos borde sin impacto en el negocio
* Verificaciones de estructura interna de datos

---

## 9. Cantidad Recomendada

Se recomienda mantener un número reducido de Acceptance Tests.

* Proyecto pequeño: entre 5 y 10 tests
* Cada test debe representar un flujo crítico del negocio

El objetivo es calidad y claridad, no cobertura exhaustiva.

---

## 10. Relación con Otros Tipos de Pruebas

| Tipo        | Enfoque                       |
| ----------- | ----------------------------- |
| Unit        | Lógica aislada                |
| Integration | Interacción entre componentes |
| Functional  | Casos de uso del sistema      |
| E2E         | Flujo completo con interfaz   |
| Acceptance  | Validación del negocio        |

---

## 11. Buenas Prácticas

* Utilizar nombres claros y descriptivos
* Mantener los tests simples y enfocados
* Evitar lógica compleja dentro de los tests
* Priorizar la comprensión sobre la optimización

---

## 12. Anti-Patrones

* Uso de lenguaje técnico en lugar de lenguaje de negocio
* Tests difíciles de interpretar
* Cobertura excesiva con tests de aceptación
* Validación de aspectos irrelevantes para el negocio

---

## 13. Rol dentro del Proyecto

Los Acceptance Tests cumplen un rol estratégico:

* Validan que el sistema cumple los requisitos del negocio
* Funcionan como documentación ejecutable
* Permiten detectar desviaciones entre implementación y expectativas

---

