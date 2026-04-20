## Schemas de la base de datos

Los schemas del proyecto se definen con base en cuatro criterios principales:


#### **Responsabilidad de negocio**

Cada schema agrupa tablas que pertenecen al mismo dominio. El objetivo es que cada módulo del backend tenga una frontera clara de qué datos posee y cuáles solo consulta.

#### **Acceso y seguridad** 

En un entorno productivo se crearía un usuario de base de datos por schema con permisos específicos de lectura y escritura. En el contexto actual de free tier esto no se implementa, pero la separación por schemas deja la puerta abierta para aplicarlo sin reestructurar la base de datos.

#### **Frecuencia de cambio**

Las tablas que cambian juntas y por las mismas razones deben vivir en el mismo schema. Mezclar tablas de naturaleza distinta (por ejemplo, datos estáticos de configuración con datos volátiles de tiempo real) complica el mantenimiento y el razonamiento sobre el sistema.

#### **Dependencias entre tablas**

Cuando una tabla referencia frecuentemente a otra, conviene que ambas vivan en el mismo schema. Las dependencias entre schemas deben ser explícitas y unidireccionales para evitar acoplamientos circulares.

### Distribución de tablas por schema

```
admin        → user, company_request, audit_log
companies    → company, company_user, route, stop, route_stop,
               route_stop_fare, schedule, bus, trip
tracking   → bus_location, bus_credential
```

### Justificación por schema

#### Admin

El schema `admin` agrupa todo lo relacionado a la gobernanza de la plataforma. La tabla `user` vive aquí porque el ciclo de vida de un usuario lo controla el administrador de plataforma, no la empresa. La aprobación de empresas y el registro de auditoría son igualmente responsabilidad de este nivel.

#### Companies

El schema `companies` concentra toda la operación interna de cada empresa: su flota, rutas, paradas, horarios y viajes. Es el schema más grande del sistema porque gestiona la mayor parte de la lógica de negocio. El acceso está siempre limitado al contexto de la empresa autenticada, nunca hay visibilidad cruzada entre empresas.

#### Tracking

El schema `tracking` contiene únicamente las dos tablas de tiempo real. Su naturaleza es fundamentalmente distinta al resto del sistema: escrituras frecuentes, datos volátiles que se sobreescriben constantemente, y un patrón de acceso optimizado para latencia baja. Es el schema que consume exclusivamente el módulo `operations` del backend.

#### Dualidad de tabla "trip"

La tabla `trip` merece una aclaración. Es el puente entre `companies` (schedule, bus) y `operations` (bus_location), por lo que podría argumentarse que pertenece a cualquiera de los dos schemas. Se decidió mantenerla en `companies` porque su ciclo de vida completo (creación, asignación de bus, estados) lo gestiona la empresa. El módulo `tracking` únicamente lee su estado para determinar si el bus debe transmitir ubicación, lo que no justifica moverla.