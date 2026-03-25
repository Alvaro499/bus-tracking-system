
# Integration Tests Guide

**Versión:** 1.0

---

## 1. Propósito

Un Integration Test prueba un **componente principal del sistema junto con sus dependencias reales**.

A diferencia de los Unit Tests:

* Sí se utiliza base de datos real (aislada con contenedores)
* Sí se levanta el contexto de Spring Boot
* Sí se usan repositorios y servicios reales

El objetivo no es probar lógica aislada, sino verificar que una parte del sistema funciona correctamente cuando está conectada a su entorno real.

---

## 2. Alcance

Un Integration Test:

* Tiene un **componente principal** (generalmente un service o use case)
* Incluye sus dependencias reales (repositorios, base de datos, etc.)
* No cubre flujos completos del sistema
* No utiliza HTTP ni interfaz de usuario

No sustituye:

* Unit Tests (lógica aislada)
* Functional Tests (flujo entre múltiples componentes)
* Acceptance Tests (validación vía API)
* E2E Tests (flujo completo con UI)

---

## 3. Ubicación en el Proyecto

Ruta establecida:

```id="8x2mkl"
src/test/java/com/bustracking/[modulo]/integration/
```

Ejemplo:

```id="p4d9hs"
admin/integration/
companies/integration/
tracking/integration/
```

Cada test debe estar asociado a un módulo específico.

---

## 4. Estructura General

Un Integration Test levanta el contexto de Spring y utiliza dependencias reales.

### Esqueleto base:

```java id="r9v2qw"
@SpringBootTest
@Testcontainers
class ExampleIntegrationTest {

    @Test
    void givenValidData_whenActionExecuted_thenExpectedResult() {

        // ARRANGE
        // Preparación de datos

        // ACT
        // Ejecución de la operación

        // ASSERT
        // Validación del resultado y/o persistencia
    }
}
```

---

## 5. Uso de Testcontainers

Testcontainers permite levantar una base de datos real en un entorno aislado para cada ejecución.

Durante el test:

1. Se inicia un contenedor de base de datos
2. Se ejecuta el test
3. El contenedor se descarta

Esto asegura:

* Aislamiento entre tests
* Consistencia en los resultados
* Un entorno similar a producción

---

## 6. Patrón AAA

Todos los tests deben seguir el patrón:

* **Arrange:** preparación de datos y contexto
* **Act:** ejecución de la operación principal
* **Assert:** validación del resultado

---

## 7. Convenciones de Nombres

Formato recomendado:

```id="f3k8zt"
test[Accion]_[Condicion]_[ResultadoEsperado]
```

Los nombres deben dejar claro:

* Qué se está probando
* En qué condiciones
* Qué se espera que ocurra

---

## 8. Qué validar

Un Integration Test debe validar:

* Que el componente principal funciona correctamente
* Que la interacción con la base de datos es correcta
* Que los datos se persisten como se espera
* Que las reglas básicas del flujo se cumplen

---

## 9. Qué siempre se debe hacer

1. **Usar `@SpringBootTest`** para activar el contexto completo de Spring
2. **Usar `@Testcontainers`** para levantar la base de datos
3. **Inyectar con `@Autowired`** los servicios y repositorios reales
4. **Verificar datos en la BD** con repositorios
5. **Asegurar independencia entre tests** (datos limpios o aislados)

---

## 10. Qué nunca se debe hacer

1. **Dejar basura en la BD** entre tests
2. **Depender de otros tests** (cada test debe ser independiente)
3. **Mockear dependencias** (para eso están los Unit Tests, aquí todo es real)
4. **Hacer pruebas muy complejas** (si es necesario, dividir en múltiples tests)

---

## 11. Velocidad

Los Integration Tests son más lentos que los Unit Tests porque:

* Levantan el contexto de Spring
* Utilizan base de datos real

Esto es esperado. Por esa razón:

* Deben ser menos que los Unit Tests
* Deben enfocarse en lo importante

---

## 12. Cuándo escribirlos

Se recomienda escribir Integration Tests cuando:

* Se necesita validar que un servicio funciona correctamente
* Se quiere verificar persistencia en base de datos
* Se desea asegurar el comportamiento de transacciones
* Se necesita comprobar integración real entre componentes cercanos

No se deben usar para probar lógica pura o cálculos simples.

---

## 13. Diferencia con Unit Tests

| Aspecto    | Unit Test           | Integration Test      |
| ---------- | ------------------- | --------------------- |
| BD         | No toca             | Toca (TestContainers) |
| Spring     | No necesita         | Necesita              |
| Mocks      | Sí, muchos          | No, nada mockeado     |
| Velocidad  | Muy rápida          | Lenta                 |
| Cantidad   | Muchos              | Menos                 |
| Qué prueba | Lógica de una clase | Componente completo   |

---

## 14. Diferencia con Functional Tests

| Aspecto     | Integration Test        | Functional Test    |
| ----------- | ----------------------- | ------------------ |
| Enfoque     | Un componente principal | Flujo completo     |
| Servicios   | Uno principal           | Múltiples          |
| Complejidad | Baja/Media              | Media/Alta         |
| Flujo       | Una operación           | Secuencia de pasos |

---

## 15. Rol dentro del Proyecto

Los Integration Tests permiten:

* Detectar problemas entre lógica y persistencia
* Validar que los componentes funcionan en un entorno real
* Reducir riesgos antes de pruebas de mayor nivel

Son un punto intermedio clave entre Unit Tests y pruebas más completas del sistema.

---