
# Functional Tests Guide

**Versión:** 1.0
**Última actualización:** 2026-03-25

---

## 1. Propósito

Este documento define los lineamientos para la implementación de **Functional Tests** dentro del proyecto *Bus Tracking System*.

Su objetivo es validar que los **flujos de negocio** funcionan correctamente mediante la interacción de múltiples componentes del sistema.

---

## 2. Alcance

Los Functional Tests:

* Validan flujos completos de negocio a nivel de backend
* Involucran múltiples servicios y componentes trabajando en conjunto
* Utilizan una base de datos real para verificar persistencia
* No utilizan HTTP ni interfaz de usuario

No sustituyen:

* Unit Tests
* Integration Tests
* Acceptance Tests
* End-to-End Tests

---

## 3. Ubicación en el Proyecto

Ruta establecida:

```id="r4k9xp"
src/test/java/com/bustracking/functional/
```

Cada test debe representar un flujo de negocio relevante que involucre más de un componente del sistema.

---

## 4. Principios Generales

### 4.1 Enfoque en Flujos de Negocio

Los Functional Tests deben validar procesos completos que representen acciones reales del sistema, no operaciones aisladas.

---

### 4.2 Integración Interna

Se espera que múltiples servicios, repositorios y componentes colaboren durante la ejecución del test.

El objetivo es verificar que estas interacciones funcionan correctamente en conjunto.

---

### 4.3 Uso de Componentes Reales

Todos los componentes deben ser reales:

* Servicios
* Repositorios
* Base de datos

No se deben utilizar mocks.

---

### 4.4 Nivel Intermedio de Abstracción

Los Functional Tests operan en un nivel intermedio:

* Más alto que Integration Tests
* Más bajo que Acceptance o E2E Tests

No prueban el sistema desde afuera, pero tampoco prueban unidades aisladas.

---

## 5. Convenciones de Nombres

### 5.1 Clases

Formato recomendado:

```id="c8v2mz"
[Flujo][Accion][Resultado]Test
```

Las clases deben representar un flujo de negocio completo.

---

### 5.2 Métodos

Se recomienda el uso del patrón BDD:

```id="y3n7ql"
given[Contexto]_when[Accion]_then[Resultado]
```

El nombre debe describir claramente el flujo ejecutado.

---

## 6. Estructura del Test

Los Functional Tests deben seguir el patrón:

* **ARRANGE**: preparación del estado inicial
* **ACT**: ejecución de los pasos del flujo
* **ASSERT**: validación del estado final

### Esqueleto base:

```java id="w6t1zs"
@SpringBootTest
@Testcontainers
class ExampleFunctionalTest {

    @Test
    void givenContext_whenBusinessFlowExecuted_thenExpectedState() {

        // ARRANGE
        // Preparación del escenario inicial

        // ACT
        // Ejecución de múltiples pasos del flujo

        // ASSERT
        // Validación del estado final del sistema
    }
}
```

---

## 7. Criterios de Validación

Un Functional Test debe validar:

### 7.1 Flujo completo

Ejecución de múltiples pasos que representen un proceso del negocio.

---

### 7.2 Interacción entre componentes

Colaboración correcta entre servicios, repositorios y otras capas.

---

### 7.3 Estado final del sistema

Persistencia y consistencia de los datos después del flujo.

---

## 8. Exclusiones

No deben incluirse en Functional Tests:

* Pruebas de un solo servicio
* Validaciones aisladas de lógica
* Llamadas HTTP
* Interacción con interfaz de usuario

---

## 9. Cantidad Recomendada

Se recomienda mantener una cantidad controlada de Functional Tests:

* Cubrir únicamente flujos relevantes
* Evitar duplicar pruebas ya cubiertas en otros niveles

---

## 10. Relación con Otros Tipos de Pruebas

| Tipo        | Enfoque                           |
| ----------- | --------------------------------- |
| Unit        | Lógica aislada                    |
| Integration | Un componente con dependencias    |
| Functional  | Flujo entre múltiples componentes |
| Acceptance  | Validación del negocio (API)      |
| E2E         | Flujo completo con interfaz       |

---

## 11. Buenas Prácticas

* Mantener los tests enfocados en flujos claros
* Evitar complejidad innecesaria
* Verificar siempre el estado final del sistema
* Documentar pasos cuando el flujo sea complejo
* Asegurar consistencia de datos entre ejecuciones

---

## 12. Anti-Patrones

* Uso de mocks
* Tests que prueban un solo componente
* Flujos excesivamente largos o difíciles de mantener
* Dependencia de estados no controlados
* Falta de validación del resultado final

---

## 13. Consideraciones de Ejecución

Los Functional Tests:

* Tienen mayor tiempo de ejecución que los Integration Tests
* Requieren inicialización de múltiples componentes
* Deben ejecutarse en entornos controlados

---

## 14. Rol dentro del Proyecto

Los Functional Tests permiten:

* Validar que los componentes internos trabajan correctamente en conjunto
* Detectar fallos en la lógica de negocio distribuida
* Asegurar la consistencia de flujos críticos del sistema

---

