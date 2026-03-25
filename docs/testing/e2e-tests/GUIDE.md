# End-to-End (E2E) Tests Guide

**Versión:** 1.0

---

## 1. Propósito

Este documento define los lineamientos para la implementación de **End-to-End (E2E) Tests** dentro del proyecto *Bus Tracking System*.

Su objetivo es validar que el sistema completo funciona correctamente desde la perspectiva del usuario final, incluyendo la interacción entre frontend, backend y cualquier componente involucrado.

---

## 2. Alcance

Los E2E Tests:

* Validan flujos completos del sistema desde la interfaz de usuario
* Simulan el comportamiento real de un usuario
* Ejecutan el sistema en un entorno lo más cercano posible a producción
* Verifican la integración entre frontend, backend y servicios externos

No sustituyen:

* Unit Tests
* Integration Tests
* Functional Tests
* Acceptance Tests

---

## 3. Ubicación en el Proyecto

Ruta establecida:

```id="m2lq8s"
src/test/java/com/bustracking/e2e/
```

Cada test debe representar un flujo completo del sistema desde la perspectiva del usuario.

---

## 4. Principios Generales

### 4.1 Enfoque en el Usuario

Los E2E Tests deben validar escenarios reales de uso del sistema, tal como los ejecutaría un usuario final a través de la interfaz.

---

### 4.2 Sistema Completo

Estas pruebas deben involucrar:

* Interfaz de usuario
* Backend
* Base de datos
* Servicios externos (cuando aplique)

El sistema debe tratarse como una unidad completa.

---

### 4.3 Independencia de Tecnología

La implementación de los E2E Tests no está acoplada a una herramienta específica.

Se pueden utilizar herramientas como:

* Automatización de navegadores
* Frameworks de testing frontend
* Herramientas de simulación de usuario

La elección dependerá de las necesidades del proyecto.

---

### 4.4 Validación de Comportamiento

Los tests deben validar lo que el usuario percibe:

* Navegación
* Acciones disponibles
* Resultados visibles en pantalla

No deben centrarse en detalles internos del sistema.

---

## 5. Convenciones de Nombres

### 5.1 Clases

Formato recomendado:

```id="xk2j9a"
[Flujo][Accion][Resultado]E2ETest
```

Las clases deben representar un flujo completo del sistema.

---

### 5.2 Métodos

Se recomienda el uso del patrón BDD:

```id="g7w3pz"
given[Contexto]_when[Accion]_then[Resultado]
```

El nombre debe describir claramente el escenario completo desde la perspectiva del usuario.

---

## 6. Estructura del Test

Los E2E Tests deben seguir una estructura basada en tres fases:

* **ARRANGE**: preparación del entorno y estado inicial
* **ACT**: ejecución de acciones del usuario en la interfaz
* **ASSERT**: validación del resultado visible

### Esqueleto base:

```java id="v9c1rn"
class ExampleE2ETest {

    @Test
    void givenContext_whenUserPerformsAction_thenExpectedOutcome() {

        // ARRANGE
        // Configuración del entorno y estado inicial

        // ACT
        // Simulación de acciones del usuario (navegación, interacción)

        // ASSERT
        // Validación del comportamiento observable en la interfaz
    }
}
```

---

## 7. Criterios de Validación

Un E2E Test debe validar:

### 7.1 Flujos completos del usuario

Escenarios reales que recorren múltiples capas del sistema.

---

### 7.2 Interacción con la interfaz

Acciones como navegación, ingreso de datos y ejecución de operaciones.

---

### 7.3 Resultados visibles

Cambios en la interfaz que confirman que la acción fue exitosa.

---

### 7.4 Integración entre componentes

Comunicación correcta entre frontend, backend y otros servicios.

---

## 8. Exclusiones

No deben incluirse en E2E Tests:

* Validaciones de lógica interna
* Verificación de estructuras internas de datos
* Pruebas unitarias o de componentes aislados
* Detalles técnicos no visibles para el usuario

---

## 9. Cantidad Recomendada

Se recomienda mantener un número limitado de E2E Tests debido a su costo de ejecución.

* Cubrir únicamente flujos críticos
* Evitar duplicación de escenarios ya cubiertos en otros niveles de testing

---

## 10. Relación con Otros Tipos de Pruebas

| Tipo        | Enfoque                          |
| ----------- | -------------------------------- |
| Unit        | Lógica aislada                   |
| Integration | Interacción entre componentes    |
| Functional  | Casos de uso del sistema         |
| Acceptance  | Validación del negocio (backend) |
| E2E         | Flujo completo con interfaz      |

---

## 11. Buenas Prácticas

* Mantener los tests enfocados en flujos críticos
* Priorizar estabilidad sobre cantidad
* Reducir dependencias innecesarias
* Diseñar tests resistentes a cambios menores en la interfaz
* Asegurar entornos de prueba consistentes

---

## 12. Anti-Patrones

* Tests frágiles dependientes de detalles visuales irrelevantes
* Cobertura excesiva de escenarios no críticos
* Uso de E2E para validar lógica interna
* Falta de aislamiento entre pruebas
* Dependencia fuerte de datos no controlados

---

## 13. Consideraciones de Ejecución

Los E2E Tests:

* Tienen mayor tiempo de ejecución que otros tipos de pruebas
* Son más propensos a fallos por factores externos
* Deben ejecutarse en entornos controlados

Se recomienda su ejecución en pipelines de integración continua en etapas avanzadas.

---

## 14. Rol dentro del Proyecto

Los E2E Tests cumplen un rol de validación integral:

* Verifican que el sistema funciona correctamente como producto final
* Detectan fallos en la integración entre capas
* Garantizan que el usuario puede completar flujos críticos

---