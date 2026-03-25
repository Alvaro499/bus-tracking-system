# Guia de Acceptance Tests

## Que son

Un Acceptance Test valida que la aplicacion cumple con los requisitos del negocio. Se escribe en lenguaje del negocio, no en lenguaje tecnico. Un gerente deberia entender que prueba un acceptance test.

Ejemplos:
- "Un conductor puede actualizar su ubicacion y esta aparece en el mapa en menos de 2 segundos"
- "Un administrador puede aprobar una solicitud de empresa"
- "Un usuario puede ver todos los buses en su ruta"

## Donde van

`src/test/java/acceptance/[RequisitoDNegocioTest].java`

Ejemplo: `src/test/java/acceptance/BusDriverCanTrackLocationTest.java`

## Estructura general

Un Acceptance test se ve como cualquier otro test, pero el nombre y los comentarios explican el requisito de negocio:

```
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class BusDriverCanTrackLocationTest {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    
    @Test
    void givenBusDriver_whenSubmitsLocationUpdate_thenLocationAppearsInMapWithin2Seconds() {
        // ARRANGE - Configurar escenario: conductor, autobus, ubicacion
        String busId = "BUS-001";
        GPSCoordinateDTO newLocation = new GPSCoordinateDTO(10.5, 20.3);
        
        // ACT - Conductor envia actualizacion de ubicacion
        long startTime = System.currentTimeMillis();
        restTemplate.postForEntity(
            "http://localhost:" + port + "/api/buses/" + busId + "/location",
            newLocation,
            Void.class
        );
        
        // ASSERT - Ubicacion debe aparecer en el mapa rapidamente
        BusDTO busInMap = restTemplate.getForObject(
            "http://localhost:" + port + "/api/map/buses/" + busId,
            BusDTO.class
        );
        
        long elapsedTime = System.currentTimeMillis() - startTime;
        
        assertEquals(10.5, busInMap.getLatitude());
        assertTrue(elapsedTime < 2000, "Debe aparecer en menos de 2 segundos");
    }
}
```

## Diferencia en el nombre

Los Acceptance tests usan nombres diferentes. Vienen de BDD (Behavior Driven Development):

Patron: `given[Contexto]_when[Accion]_then[ResultadoEsperado]`

Ejemplos:
- `givenAdminLoggedIn_whenApprovesCompany_thenCompanyStatusChangesToApproved`
- `givenBusWithValidGPS_whenLocationUpdated_thenMapRefreshesImmediately`
- `givenEmployeeAccess_whenViewsRoute_thenSeesAllBusesOnRoute`

## Lenguaje de negocio

Los tests de aceptacion usan palabras del negocio:

- "conductor" en lugar de "usuario"
- "autobus" en lugar de "entidad Bus"
- "mapa" en lugar de "endpoint de visualizacion"
- "aparecer" en lugar de "retornar respuesta"

Esto es importante porque el negocio (gerentes, product owners) debe entender qué se está probando.

## Que validar

En un Acceptance test se valida:

1. **El usuario puede hacer lo que necesita**
2. **El resultado es el esperado desde la perspectiva del negocio**
3. **Los tiempos son razonables** (si es importante para el negocio)
4. **Los datos son correctos** desde el punto de vista del negocio

## Que siempre se debe hacer

1. **Usar nombres tipo BDD** (given/when/then)
2. **Lenguaje del negocio**, no lenguaje tecnico
3. **Validar requisitos**, no detalles de implementacion
4. **Documentar el contexto** en los comentarios ARRANGE

## Que nunca se debe hacer

1. **Probar detalles tecnicos** (eso es para otros tests)
2. **Usar nombres tecnicos** (eso confunde al negocio)
3. **Hacer tests que solo entienden programadores**
4. **Ignorar requisitos de performance** si son importantes

## Cuando escribir

Se escribe un Acceptance test cuando:
- Es un requisito importante del negocio
- El usuario deberia poder verificar que funciona
- Es algo que el gerente o product owner pidio

Ejemplos:
- Rastrear ubicacion del autobus
- Registrar una empresa
- Aprobar una solicitud
- Ver horarios de rutas

No se escribe para:
- Validaciones menores
- Detalles de implementacion
- Edge cases sin importancia para el negocio

## Velocidad

Son lentos. 500ms - 1s por test. Esto es normal.

Pero no deben ser muchos. Típicamente 5-10 Acceptance tests por proyecto pequeño. Representan los requisitos principales.

## Diferencia con E2E Tests

| Aspecto | E2E Test | Acceptance Test |
|---------|----------|-----------------|
| Lenguaje | Tecnico | Negocio |
| Que valida | API techicamente correcta | Requisito del negocio |
| Quien entiende | Solo programadores | Todos (negocios + dev) |
| Que prueba | Status HTTP, campos | Comportamiento visible |

Ambos hacen peticiones HTTP, pero con propositos diferentes.

## Documentacion

Acceptance tests son autodocumentacion. Alguien que lee el test entiende:
- Cual es el requisito
- Como se valida
- Que deberia pasar

Por eso los nombres y comentarios son tan importantes.

## Referencias

Basado en BDD (Behavior Driven Development) y principios de "The Pragmatic Programmer".
