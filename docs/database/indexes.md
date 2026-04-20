## Índices de base de datos

### Reglas para crear y determinar índices

Los índices se crean únicamente cuando existe una consulta concreta que los justifica. Cada índice adicional incrementa el costo de cada `INSERT`, `UPDATE` y `DELETE` en esa tabla, por lo que la sobre-indexación es tan problemática como la ausencia de índices.

**¿Sobre qué columnas crear un índice?**

Columnas en `WHERE`, cuando una columna filtra filas frecuentemente, el motor necesita ubicarlas sin escanear la tabla completa:

```sql
WHERE route.name = ?
-- → índice en route.name
```

Columnas en `JOIN`, cuando dos tablas se relacionan frecuentemente, el motor necesita encontrar las filas relacionadas sin escanear la tabla entera. PostgreSQL no crea índices sobre claves foráneas automáticamente:

```sql
JOIN schedule ON route.id = schedule.route_id
-- → índice en schedule.route_id
```

Columnas combinadas, cuando dos columnas se filtran juntas de forma constante, un índice compuesto resuelve ambas condiciones en una sola búsqueda. Dos índices separados no logran el mismo nivel de eficiencia:

```sql
WHERE status = 'IN_PROGRESS' AND trip_date = CURRENT_DATE
-- → índice compuesto en (status, trip_date)
```

**Convención de nombres**

```
idx_{Tabla}_{Columna(s)}

idx_CompanyUser_UserId       → tabla: company_user, columna: user_id
idx_Trip_StatusDate          → tabla: trip, columnas: status + trip_date
idx_Route_Name_GIN           → tabla: route, tipo especial: GIN
```

**Tipos de índice utilizados**

B-Tree es el tipo por defecto y cubre la mayoría de los casos: igualdad, rangos y JOINs. GIN con `pg_trgm` se usa exclusivamente para búsqueda parcial de texto (`ILIKE '%texto%'`), ya que un B-Tree no puede optimizar patrones con comodín al inicio.

---

### Índices por contexto de uso

#### Autenticación y verificación de permisos

Cada request autenticado verifica a qué empresa pertenece el usuario y qué rol tiene. Estas consultas ocurren en prácticamente todas las operaciones del panel de gestión, por lo que son las más críticas en frecuencia.

```sql
-- "¿A qué empresa(s) pertenece este usuario?"
CREATE INDEX idx_CompanyUser_UserId ON companies.company_user(user_id);

-- "¿Qué usuarios tiene esta empresa y con qué roles?"
CREATE INDEX idx_CompanyUser_CompanyId ON companies.company_user(company_id);
```

Consultas asociadas:

```sql
SELECT * FROM companies.company_user
WHERE user_id = 'uuid-user';

SELECT * FROM companies.company_user
WHERE company_id = 'uuid-company';
```

---

#### Panel de administración de empresa

Cuando un administrador accede a su panel, el sistema filtra todos los recursos por `company_id` para garantizar el aislamiento entre empresas. Sin estos índices, cada consulta haría un escaneo completo de la tabla, revisando datos de todas las empresas para devolver solo los de una.

```sql
-- Listar rutas de la empresa autenticada
CREATE INDEX idx_Route_CompanyId ON companies.route(company_id);

-- Listar paradas del catálogo de la empresa
CREATE INDEX idx_Stop_CompanyId ON companies.stop(company_id);

-- Listar la flota de buses de la empresa
CREATE INDEX idx_Bus_CompanyId ON companies.bus(company_id);
```

Consulta típica (aplica el mismo patrón para las tres):

```sql
SELECT * FROM companies.route
WHERE company_id = 'uuid-company';
```

---

#### Búsqueda pública de rutas

Los usuarios buscan rutas desde la app pública. Se soportan dos tipos de búsqueda: por origen y destino exactos, y por nombre parcial de la ruta cuando el usuario escribe texto libre.

```sql
-- Búsqueda exacta: usuario selecciona origen y destino desde listas
CREATE INDEX idx_Route_OriginDestination ON companies.route(origin, destination);

-- Búsqueda parcial: usuario escribe "Cartago" y el sistema sugiere coincidencias
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX idx_Route_Name_GIN ON companies.route USING GIN (name gin_trgm_ops);
```

Consultas asociadas:

```sql
-- Búsqueda exacta
SELECT * FROM companies.route
WHERE origin = 'San José' AND destination = 'Cartago';

-- Búsqueda parcial
SELECT * FROM companies.route
WHERE name ILIKE '%cartago%';
```

El índice compuesto `(origin, destination)` resuelve ambas condiciones en una sola búsqueda. El índice GIN permite el comodín al inicio del patrón, algo que B-Tree no puede optimizar. Ambos índices coexisten porque responden a dos flujos de búsqueda distintos.

---

#### Mapa y seguimiento en tiempo real

La consulta más crítica del sistema, para encontrar los viajes activos para el día actual y relacionarlos con sus buses. Se ejecuta cada vez que un usuario abre el mapa y se repite periódicamente por el llamado desde el frontend.

```sql
-- "¿Qué viajes están IN_PROGRESS hoy?" — consulta principal del mapa
CREATE INDEX idx_Trip_StatusDate ON companies.trip(status, trip_date);

-- "¿Qué viaje está realizando este bus en este momento?"
CREATE INDEX idx_Trip_BusId ON companies.trip(bus_id);
```

Consultas asociadas:

```sql
-- Consulta principal del mapa
SELECT * FROM companies.trip
WHERE status = 'IN_PROGRESS'
AND trip_date = CURRENT_DATE;

-- Relacionar bus con su viaje activo
SELECT * FROM companies.trip
WHERE bus_id = 'uuid-bus'
AND status = 'IN_PROGRESS';
```

El índice compuesto `(status, trip_date)` es más eficiente que dos índices separados porque las consultas siempre filtran por ambas columnas juntas. Con índices separados, PostgreSQL tendría que combinar dos resultados intermedios antes de devolver la respuesta.

---

#### Detalle de rutas y paradas

Cuando el usuario activa las paradas en el mapa, el sistema carga todas las paradas de esa ruta en orden de recorrido.

```sql
-- "¿Cuáles son las paradas de esta ruta y en qué orden?"
CREATE INDEX idx_RouteStop_RouteId ON companies.route_stop(route_id);
```

Consulta asociada:

```sql
SELECT * FROM companies.route_stop
WHERE route_id = 'uuid-route'
ORDER BY order_index;
```

---

#### Gestión de horarios

Usada por el panel de administración al listar los horarios de una ruta, y por el proceso automático que genera los trips diarios a medianoche.

```sql
-- "¿Qué horarios activos tiene esta ruta?"
CREATE INDEX idx_Schedule_RouteId ON companies.schedule(route_id);
```

Consulta asociada:

```sql
SELECT * FROM companies.schedule
WHERE route_id = 'uuid-route'
AND is_active = TRUE;
```

---

#### Gestión de viajes

Usada por el panel administrativo y por la app del bus. Los conductores buscan los viajes disponibles para su turno a partir del schedule asignado.

```sql
-- "¿Qué viajes se generaron para este horario?"
CREATE INDEX idx_Trip_ScheduleId ON companies.trip(schedule_id);
```

Consulta asociada:

```sql
SELECT * FROM companies.trip
WHERE schedule_id = 'uuid-schedule'
AND trip_date = CURRENT_DATE;
```

---

#### Búsqueda de paradas por nombre

Los administradores buscan paradas por nombre al asignarlas a una ruta o editarlas desde el panel de gestión.

```sql
-- "¿Existe una parada llamada 'Terminal San José'?"
CREATE INDEX idx_Stop_Name ON companies.stop(name);
```

Consulta asociada:

```sql
SELECT * FROM companies.stop
WHERE name = 'Terminal San José'
AND company_id = 'uuid-company';
```

---

#### Tarifas por parada

Al mostrar el precio de cada parada de una ruta, el sistema busca la tarifa activa para ese `route_stop_id`. El índice compuesto evita que PostgreSQL recorra tarifas históricas o vencidas antes de encontrar la vigente.

```sql
-- "¿Cuál es el precio vigente para esta parada?"
CREATE INDEX idx_RouteStopFare_RouteStopId
    ON companies.route_stop_fare(route_stop_id, is_active);
```

Consulta asociada:

```sql
SELECT price
FROM companies.route_stop_fare
WHERE route_stop_id = 'uuid-route-stop'
AND is_active = TRUE;
```

Con un índice solo sobre `route_stop_id`, PostgreSQL llegaría a todas las tarifas de esa parada (históricas incluidas) y luego filtraría las activas. Con el índice compuesto `(route_stop_id, is_active)`, llega directamente al resultado sin pasos intermedios.

---


### Tablas sin índices adicionales

`tracking.bus_location` no tiene índices adicionales porque su único patrón de acceso es por `bus_id`, que ya es la PK de esa tabla. Agregar índices extra penalizaría las escrituras frecuentes sin ningún beneficio.

`tracking.bus_credential` tampoco los necesita. La autenticación siempre busca por `bus_id`, cubierto por el `UNIQUE` existente.

`admin.audit_log` no tiene índices por ahora. Sus consultas son infrecuentes y administrativas. Si en el futuro se construye un módulo de auditoría con reportes frecuentes, se evaluarán índices sobre `occurred_at` y `entity_type` en ese momento.

---
