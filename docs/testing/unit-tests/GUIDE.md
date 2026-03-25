# Guia de Unit Tests

## Que son

Un Unit Test prueba una clase de forma individual. No toca la base de datos, no hace peticiones HTTP, no depende de otras clases. Filtra todo con mocks. Solo se prueba la logica de una clase.

## Donde van

`src/test/java/[modulo]/unit/[capa]/[NombreClaseTest].java`

Ejemplo: Si la clase esta en `src/main/java/com/bustracking/admin/application/usecase/ApproveCompanyUseCase.java`, el test va en:
`src/test/java/com/bustracking/admin/unit/application/usecase/ApproveCompanyUseCaseTest.java`

## Estructura general

Un test unitario tiene esta forma:

```
@ExtendWith(MockitoExtension.class)
class MiClaseTest {
    
    private MiClase miClase;
    
    @Mock
    private Dependencia dependencia;
    
    @BeforeEach
    void setup() {
        // Crear instancia de la clase con mocks
        miClase = new MiClase(dependencia);
    }
    
    // ========== TESTS PARA: metodo1() ==========
    @Test
    void testMetodo1_CasoFeliz_DebeRetornarValorEsperado() { }
    
    @Test
    void testMetodo1_DatoInvalido_DebeLatarExcepcion() { }
    
    // ========== TESTS PARA: metodo2() ==========
    @Test
    void testMetodo2_OtroCaso_DebeHacerOtraCosa() { }
}
```

## Patron AAA

Cada test sigue siempre este orden:

1. **Arrange (Preparar):** Se crean los datos de entrada, se configuran los mocks, se prepara el escenario.

2. **Act (Actuar):** Se llama el metodo que se quiere probar.

3. **Assert (Verificar):** Se valida que el resultado es correcto.

Ejemplo:
```
@Test
void testCalcular_Enteros_SumaCorrectamente() {
    // ARRANGE
    int a = 5;
    int b = 3;
    
    // ACT
    int resultado = miClase.calcular(a, b);
    
    // ASSERT
    assertEquals(8, resultado);
}
```

## Nombrado de tests

Los nombres de los tests deben ser claros y explicar:
- Que se esta probando
- Con que datos
- Que resultado se espera

Forma recomendada: `test[MetodoQuePrueba]_[ConQueDatos]_[QueDebeOcurrir]`

Ejemplos:
- `testRegistrarEmpresa_DatosValidos_GuardaEnBaseDatos`
- `testValidarEmail_EmailInvalido_LanzaExcepcion`
- `testCalcularPrecio_ClienteEstudiante_AplicaDescuento`

## Que siempre se debe hacer

1. **Mockejar dependencias** para aislar la clase
2. **Seguir el patron AAA**
3. **Una sola assertion o verificacion por test** (a menos que sea complejo)
4. **Nombres claros** que expliquen que hace el test
5. **Un test por comportamiento**, no por linea de codigo

## Que nunca se debe hacer

1. **Tocar la base de datos** en unit tests. Para eso estan los integration tests.
2. **Mezclar la logica de prueba** con el setup de datos. Si el setup es complejo, ponerlo en un metodo aparte.
3. **Usar valores aleatorios** sin razon. Los tests deben ser deterministicos.
4. **Probar dos cosas** en el mismo test. Si falla, no se sabra cual fue.
5. **Ignorar o skipear tests** sin razon. Si un test no sirve, borrarlo.

## Si una clase tiene muchas dependencias

Si una clase tiene 5 o 6 dependencias, probablemente esta haciendo demasiadas cosas. Pero si es necesario, se pueden crear fixtures o builders para simplificar el setup.

El test sigue siendo unit porque cada dependencia esta mockeada.

## Velocidad

Los unit tests deben ser muy rapidos. Si toman mas de 100ms cada uno, algo esta mal. Probablemente se esta tocando la base de datos o se esta haciendo algo expensive.

Los unit tests son la base de la piramide. Deben ser muchos y rapidos.

## Referencias

Basado en principios de "The Pragmatic Programmer" y documentacion oficial de JUnit5.
