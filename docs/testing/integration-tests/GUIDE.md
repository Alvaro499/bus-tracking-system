# Guia de Integration Tests

## Que son

Un Integration Test prueba un modulo completo de la aplicacion. A diferencia de unit tests, aqui SI se toca la base de datos real (pero en un contenedor aislado), SI se activa Spring Boot, SI se usan todas las dependencias reales.

No se mockejan cosas. Todo es del verdad.

## Donde van

`src/test/java/[modulo]/integration/[NombreTest].java`

Ejemplo: `src/test/java/com/bustracking/admin/integration/AdminServiceIntegrationTest.java`

## Estructura general

Un test de integracion se ve asi:

```
@SpringBootTest
@Testcontainers
class MiServicioIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("test_db")
        .withUsername("test")
        .withPassword("test");
    
    @Autowired
    private MiServicio miServicio;
    
    @Autowired
    private MiRepository miRepository;
    
    @Test
    void testGuardarDato_DatoValido_DebePersistitEnBD() {
        // ARRANGE - crear datos
        Empresa empresa = new Empresa("Test", "Descripcion");
        
        // ACT - hacer la operacion
        Empresa guardada = miServicio.guardar(empresa);
        
        // ASSERT - verificar que quedo en BD
        Empresa obtenida = miRepository.findById(guardada.getId());
        assertEquals("Test", obtenida.getNombre());
    }
}
```

## TestContainers

TestContainers es una libreria que levanta contenedores Docker con bases de datos reales. Cada test:

1. Levanta un contenedor de PostgreSQL
2. Corre el test
3. Destruye el contenedor

Esto es importante porque:
- La base de datos no interfiere entre tests
- Tests no se afectan unos con otros
- Es exactamente igual que produccion (misma base de datos)

## Patron AAA

Exactamente igual que en unit tests:

1. **Arrange:** Se preparan los datos iniciales
2. **Act:** Se ejecuta lo que se quiere probar
3. **Assert:** Se valida el resultado

## Nombrado de tests

Igual que unit tests:
`test[QueHace]_[ConQueDatos]_[QueDebeOcurrir]`

Ejemplos:
- `testGuardarEmpresa_EmpresaValida_DebeCrearRegistroEnBD`
- `testActualizarEstado_EstadoRechazado_DebeGuardarMotivo`

## Que siempre se debe hacer

1. **Usar @SpringBootTest** para activar el contexto completo de Spring
2. **Usar @Testcontainers** para levantar la base de datos
3. **Inyectar con @Autowired** los servicios y repositorios reales
4. **Verificar datos en la BD** con repositorios
5. **Limpiar datos entre tests** si es necesario (aunque TestContainers lo hace)

## Que nunca se debe hacer

1. **Dejar basura en la BD** entre tests
2. **Depender de otros tests** (cada test debe ser independiente)
3. **Mockejar dependencias** (para eso estan unit tests, aqui todo es real)
4. **Hacer pruebas muy complejas** (si es muy complejo, dividir en multiples tests)

## Velocidad

Los integration tests son lento porque levantan Spring Boot y la base de datos. Esto es normal. Un test puede tardar 100-500ms. No es problema si hay pocos.

Por eso la piramide de tests dice: 45% unit (rapidos), 20% integration (lentos).

## Cuando escribir

Se escriben cuando:
- Se necesita validar que el servicio funciona bien
- Se necesita validar que la base de datos persiste datos correctamente
- Se necesita validar que las transacciones son atomicas

No se escriben para probar logica pura. Eso es Unit Test.

## Diferencia con Unit Tests

| Aspecto | Unit Test | Integration Test |
|---------|-----------|-------------------|
| BD | No toca | Toca (TestContainers) |
| Spring | No necesita | Necesita |
| Mocks | Si, muchos | No, nada mockeado |
| Velocidad | Muy rapida | Lenta |
| Cantidad | Muchos | Menos |
| Que prueba | Logica de una clase | Todo un modulo |

## Referencias

Basado en Spring Boot Documentation y principios de "Clean Code" de Robert C. Martin.
