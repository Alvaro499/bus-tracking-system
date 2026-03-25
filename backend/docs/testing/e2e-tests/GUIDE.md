# Guia de E2E Tests

## Que son

Un E2E Test prueba una API completa como si un cliente HTTP real estuviera haciendo peticiones. Se valida que el endpoint responde correctamente, que el status HTTP es el esperado, que los datos vuelven en el formato correcto.

## Donde van

`src/test/java/e2e/[NombreAPITest].java`

Ejemplo: `src/test/java/e2e/BusTrackingE2ETest.java`

## Estructura general

Un E2E test se ve asi:

```
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class MiAPIE2ETest {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    
    @Test
    void testActualizarUbicacion_DatosValidos_RetornaOKYDatosGuardados() {
        // ARRANGE
        String url = "http://localhost:" + port + "/api/buses/BUS-001/location";
        GPSCoordinateDTO dto = new GPSCoordinateDTO(10.5, 20.3);
        
        // ACT
        ResponseEntity<BusDTO> response = restTemplate.postForEntity(url, dto, BusDTO.class);
        
        // ASSERT
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10.5, response.getBody().getLatitude());
    }
}
```

## Por que TestRestTemplate

TestRestTemplate es un cliente HTTP que simula peticiones reales. Hace un POST, GET, PUT, DELETE como lo haria cualquier aplicacion externa.

Esto es importante porque todo el sistema debe funcionar correctamente "desde afuera". No basta que internamente funcione.

## Patron AAA

Exactamente igual:

1. **Arrange:** Se preparan los datos y la URL
2. **Act:** Se hace la peticion HTTP
3. **Assert:** Se valida status, cuerpo de respuesta, etc

## Nombrado de tests

El nombre explica el endpoint y que deberia suceder:

Ejemplos:
- `testActualizarUbicacion_DatosValidos_Retorna200YActualizaBD`
- `testRegistrarEmpresa_DatosIncompletos_Retorna400`
- `testObtenerBuses_SinAutenticacion_Retorna401`

## Que validar

En un E2E test se valida:

1. **Status HTTP** (200, 400, 401, 500, etc)
2. **Estructura de la respuesta** (tiene los campos esperados)
3. **Valores en la respuesta** (son los valores correctos)
4. **Headers** (Content-Type, etc)
5. **Datos en la BD** (si fue una creacion o actualizacion)

## Que siempre se debe hacer

1. **Usar TestRestTemplate** para hacer peticiones
2. **Validar el status HTTP**
3. **Seguir el patron AAA**
4. **Probar casos de error** (no solo el caso feliz)
5. **Validar datos en BD** si la operacion guarda datos

## Que nunca se debe hacer

1. **Mockejar los servicios** (todo debe ser real)
2. **Testear logica interna** (eso es unit test)
3. **Hacer tests tan complejos** que sean dificiles de mantener
4. **Olvidar validar status HTTP** (es lo mas importante)

## Casos a probar

Para cada endpoint se deben probar:

1. **Caso feliz:** Datos validos, todo sale bien
2. **Validaciones:** Datos invalidos, el endpoint rechaza
3. **Errores de autenticacion:** Sin token, con token invalido
4. **Errores de autorizacion:** Usuario sin permisos
5. **Recursos no encontrados:** ID que no existe

Ejemplo para DELETE /api/buses/123:
- `testEliminarBus_IDValido_Retorna204`
- `testEliminarBus_IDNoExiste_Retorna404`
- `testEliminarBus_SinAutenticacion_Retorna401`

## Velocidad

Son lentos. 500ms - 1s por test. Es porque hacen la peticion HTTP completa y tocan la BD.

No se debe hacer un E2E test para cada endpoint. Solo para los mas importantes.

## Diferencia con Functional Tests

| Aspecto | Functional Test | E2E Test |
|---------|-----------------|----------|
| Peticiones HTTP | No | Si |
| TestRestTemplate | No | Si |
| Se prueba | Logica interna | API desde afuera |
| Cliente | Codigo interno | Cliente HTTP real |
| Que revisa | Datos en BD | Status HTTP + datos |

## Diferencia con Integration Tests

Un Integration Test prueba un servicio con su BD.

Un E2E Test prueba un endpoint (controller + servicio + BD).

E2E es mas "externo". Va desde el endpoint hasta la BD.

## Referencias

Basado en Spring Boot Documentation y principios de testing de APIs.
