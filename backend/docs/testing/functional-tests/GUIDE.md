# Guia de Functional Tests

## Que son

Un Functional Test prueba un flujo completo de negocio. Multiples servicios trabajan juntos. La base de datos es real (TestContainers), pero no son peticiones HTTP. Se prueban comportamientos que el usuario o el negocio necesita.

## Donde van

`src/test/java/functional/[NombreFlujoTest].java`

Ejemplo: `src/test/java/functional/CompanyRegistrationWorkflowTest.java`

## Estructura general

Un test funcional se ve asi:

```
@SpringBootTest
@Testcontainers
class MiFlujoWorkflowTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("test_db");
    
    @Autowired
    private Servicio1 servicio1;
    
    @Autowired
    private Servicio2 servicio2;
    
    @Autowired
    private Repository1 repo1;
    
    @Autowired
    private Repository2 repo2;
    
    @Test
    void testFlujoCompleto_RegistrarYAsignar_TodosLosServiciosTrabajanJuntos() {
        // ARRANGE
        Empresa empresa = new Empresa("Test");
        
        // ACT - Paso 1: Registrar empresa
        Empresa registrada = servicio1.registrar(empresa);
        
        // ACT - Paso 2: Asignar buses
        servicio2.asignarBuses(registrada.getId(), 5);
        
        // ASSERT - Verificar que todo quedo bien
        Empresa completa = repo1.findById(registrada.getId());
        assertEquals(5, completa.getBuses().size());
    }
}
```

## Diferencia con Integration Tests

Un Integration Test prueba un modulo (servicio + BD).

Un Functional Test prueba varios modulos trabajando juntos.

| Aspecto | Integration Test | Functional Test |
|---------|------------------|-----------------|
| Modulos | 1 modulo | 2 o mas modulos |
| Servicios | 1 servicio | Multiples servicios |
| Complejidad | Preparacion simple | Flujo paso a paso |
| Que prueba | Modulo aislado | Modulos interactuando |

## Patron AAA

Exactamente igual que los otros tests:

1. **Arrange:** Se preparan datos iniciales
2. **Act:** Se ejecutan los pasos del flujo
3. **Assert:** Se valida que el resultado es correcto

## Nombrado de tests

El nombre debe explicar el flujo completo:

Ejemplos:
- `testRegistrarEmpresaYAsignarBuses_DatosValidos_TodosPersistenEnBD`
- `testAprobarSolicitudYNotificarAllAdministrador_FlujoCompleto_TodoSaleBien`
- `testActualizarUbicacionYCalcularETA_GPSValido_MapaActualizado`

## Que siempre se debe hacer

1. **Usar multiples servicios** que trabajen juntos
2. **Inyectar repositorios** para validar datos guardados
3. **Seguir el patron AAA**
4. **Documentar cada paso** con comentarios si el flujo es complejo
5. **Verificar el estado final** de la BD

## Que nunca se debe hacer

1. **Probar un solo servicio** (eso es Integration Test)
2. **Hacer tests tan complejos** que sean dificiles de debugguear
3. **Olvidar limpiar datos** entre tests
4. **Mockejar servicios** (aqui todo debe ser real)

## Cuando escribir

Se escribe un Functional Test cuando:
- El flujo requiere 2 o mas servicios
- Es un flujo importante del negocio
- Necesita validar que los servicios trabajan bien juntos

Ejemplos reales:
- Registrar empresa, crear buses, asignarlo a ruta
- Conducir GPS, calcular ETA, actualizar mapa
- Crear solicitud, aprobarla, notificar empresa

## Velocidad

Son lentos. Pueden tardar 500ms - 1s por test. Esto es normal porque tocan la BD y activan multiples servicios.

Por eso no hay que exagerar. Se escriben solo para flujos realmente importantes.

## Diferencia con E2E Tests

Un Functional Test no hace peticiones HTTP.

Un E2E Test si hace peticiones HTTP, como si fuera un cliente real.

Un Functional Test es mas "interno", prueba la logica. Un E2E Test es mas "externo", prueba la API.

## Referencias

Basado en principios de "The Pragmatic Programmer" y BDD (Behavior Driven Development).
