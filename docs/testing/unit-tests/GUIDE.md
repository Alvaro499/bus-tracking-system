Perfecto, mantengo tu base, con tu estilo (claro y práctico), integro lo que pediste sin tocarlo y lo alineo con los otros documentos.

Lista para `docs/testing/unit-tests/GUIDE.md`:

---

# Unit Tests Guide

**Versión:** 1.0
**Última actualización:** 2026-03-25

---

## 1. Propósito

Un Unit Test prueba una **clase de forma individual**.

* No toca la base de datos
* No hace peticiones HTTP
* No depende de implementaciones reales
* Aísla dependencias usando mocks

El objetivo es validar la **lógica interna de una clase** de forma rápida y determinística.

---

## 2. Alcance

Un Unit Test:

* Tiene como foco una sola clase
* Aísla completamente sus dependencias
* No levanta el contexto de Spring
* No interactúa con infraestructura externa

No sustituye:

* Integration Tests (componentes con dependencias reales)
* Functional Tests (flujos internos)
* Acceptance Tests (validación vía API)
* E2E Tests (flujo completo con UI)

---

## 3. Ubicación en el Proyecto

Ruta establecida:

```id="u1a9kd"
src/test/java/com/bustracking/[modulo]/unit/[capa]/
```

Ejemplo:

```id="m4z7qx"
admin/unit/application/usecase/
admin/unit/domain/
admin/unit/infrastructure/
```

Cada test debe reflejar la misma estructura que el código fuente.

---

## 4. Estructura General

Un Unit Test utiliza mocks para aislar dependencias y no requiere Spring.

### Esqueleto base:

```java id="n8v2pl"
@ExtendWith(MockitoExtension.class)
class ExampleUnitTest {

    @Test
    void testMethod_Condition_ExpectedResult() {

        // ARRANGE
        // Preparación de datos y mocks

        // ACT
        // Ejecución del método

        // ASSERT
        // Validación del resultado
    }
}
```

---

## 5. Uso de Mocks

Los mocks permiten aislar la clase bajo prueba:

* Simulan el comportamiento de dependencias
* Evitan efectos secundarios
* Permiten controlar los escenarios de prueba

El objetivo es probar únicamente la lógica de la clase, no su integración.

---

## 6. Patrón AAA

Todos los tests deben seguir:

* **Arrange:** preparación de datos y mocks
* **Act:** ejecución del método
* **Assert:** validación del resultado

---

## 7. Nombrado de tests

Los nombres de los tests deben ser claros y explicar:

* Que se esta probando
* Con que datos
* Que resultado se espera

Forma recomendada: `test[MetodoQuePrueba]_[ConQueDatos]_[QueDebeOcurrir]`

Ejemplos:

* `testRegistrarEmpresa_DatosValidos_GuardaEnBaseDatos`
* `testValidarEmail_EmailInvalido_LanzaExcepcion`
* `testCalcularPrecio_ClienteEstudiante_AplicaDescuento`

---

## 8. Qué validar

Un Unit Test debe validar:

* La lógica interna de la clase
* Comportamientos esperados
* Manejo de errores y excepciones
* Condiciones límite relevantes

---

## 9. Qué siempre se debe hacer

1. **Mockejar dependencias** para aislar la clase
2. **Seguir el patron AAA**
3. **Una sola assertion o verificacion por test** (a menos que sea complejo)
4. **Nombres claros** que expliquen que hace el test
5. **Un test por comportamiento**, no por linea de codigo

---

## 10. Qué nunca se debe hacer

1. **Tocar la base de datos** en unit tests. Para eso estan los integration tests.
2. **Mezclar la logica de prueba** con el setup de datos. Si el setup es complejo, ponerlo en un metodo aparte.
3. **Usar valores aleatorios** sin razon. Los tests deben ser deterministicos.
4. **Probar dos cosas** en el mismo test. Si falla, no se sabra cual fue.
5. **Ignorar o skipear tests** sin razon. Si un test no sirve, borrarlo.

---

## 11. Si una clase tiene muchas dependencias

Si una clase tiene 5 o 6 dependencias, probablemente está haciendo demasiadas cosas.

Sin embargo:

* Se pueden usar fixtures o builders para simplificar el setup
* Mientras todo esté mockeado, el test sigue siendo unitario

Este tipo de situación suele indicar que la clase podría dividirse.

---

## 12. Velocidad

Los Unit Tests deben ser muy rápidos.

* Si un test tarda más de ~100ms, hay un problema
* Posiblemente se está usando infraestructura real o lógica innecesaria

Los Unit Tests forman la base de la pirámide:

* Deben ser muchos
* Deben ser rápidos
* Deben ejecutarse constantemente

---

## 13. Diferencia con Integration Tests

| Aspecto   | Unit Test     | Integration Test    |
| --------- | ------------- | ------------------- |
| BD        | No            | Sí                  |
| Spring    | No            | Sí                  |
| Mocks     | Sí            | No                  |
| Velocidad | Muy alta      | Media/Baja          |
| Alcance   | Clase aislada | Componente completo |

---

## 14. Rol dentro del Proyecto

Los Unit Tests permiten:

* Validar la lógica de forma rápida
* Detectar errores temprano
* Facilitar refactorización segura
* Reducir dependencia de tests más costosos

Son la base de la estrategia de testing del sistema.

---

Con esto ya tienes los 5 niveles definidos de forma consistente.
El siguiente paso importante sería unificar todo en `TESTING-STRATEGY.md`, que es donde realmente se vuelve útil para el equipo.
