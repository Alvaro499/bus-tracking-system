# Modelo ER - Teoría

---

### User y Company — CompanyRequest

Los administradores de la plataforma son responsables de aprobar o rechazar las solicitudes de registro de empresas. Esto es necesario para evitar que cualquier persona pueda registrarse y publicar información falsa en el sistema. Solo se aprueban empresas reales luego de que el administrador investigue la solicitud.

La relación entre un administrador de plataforma y una empresa no es una asociación directa, sino un proceso con ciclo de vida propio. Por eso se modela como una entidad independiente llamada CompanyRequest, que contiene información como la fecha de solicitud, la fecha de revisión, el estado de la solicitud y el motivo de rechazo en caso de que aplique.

Un administrador puede revisar muchas solicitudes, y una empresa genera una solicitud que es atendida por un administrador. La entidad CompanyRequest guarda la FK del administrador que la revisó y la FK de la empresa solicitante.

Supuesto: se podría distribuir solicitudes por región para evitar condiciones de carrera entre administradores, por ejemplo que el admin de Cartago y el admin de Alajuela tomen la misma solicitud al mismo tiempo. Esto se manejaría en lógica de negocio, no en el modelo.

Relación: CompanyRequest como entidad propia con FK a User y FK a Company.

---

### User — roles dentro del sistema

Todos los usuarios del sistema, tanto administradores de plataforma como administradores de empresa, se representan con una sola entidad User. Esta entidad contiene un campo global_role que indica si el usuario pertenece a la plataforma o a una empresa.

Un usuario con global_role igual a PLATFORM_ADMIN tiene acceso al panel de administración global y puede aprobar o rechazar solicitudes de empresas. Un usuario con global_role igual a COMPANY_USER pertenece a una empresa y su rol específico dentro de ella se gestiona en la entidad CompanyUser.

Supuesto: al aprobar una empresa se crea automáticamente un usuario con rol OWNER asignado a esa empresa.

---

### Company y CompanyUser — N:1

Una empresa puede tener múltiples administradores, y un administrador pertenece a una sola empresa. La entidad CompanyUser actúa como tabla de roles entre User y Company, con un campo role que puede ser OWNER o ADMIN.

El OWNER es el dueño de la empresa, tiene acceso completo y es el único que puede agregar o quitar administradores. El ADMIN puede gestionar buses, rutas, horarios y paradas, pero no puede modificar otros administradores.

Relación: N:1 entre CompanyUser y Company, con campo role para diferenciar permisos.

---

### Company y Bus — 1:N

Una empresa puede tener múltiples buses, y un bus pertenece a una sola empresa. El bus es un activo de la empresa, no de un administrador en particular. La gestión de quién modificó qué bus se maneja a través de AuditLog, no mediante relaciones directas entre administradores y buses.

Cada bus tiene datos estáticos como placa, si tiene rampa de accesibilidad y su estado. La ubicación GPS se separa en una entidad BusLocation para evitar modificar datos estáticos con cada actualización de coordenadas, que ocurre cada varios segundos mientras el bus está en operación.

Relación: 1:N entre Company y Bus. Relación 1:1 entre Bus y BusLocation.

---

### Company, Route y Schedule — estructura de rutas

Una empresa opera múltiples rutas. Cada ruta tiene un nombre comercial y agrupa los horarios y paradas de ese recorrido. Los horarios pertenecen a una ruta específica y definen a qué hora y qué día opera esa ruta.

Un horario tiene una hora de salida y un día de la semana. Para manejar cambios sin perder historial, cada horario puede tener una fecha de inicio y una fecha de fin opcionales. Un horario sin fecha de fin está vigente indefinidamente. Esto permite agregar horarios temporales, por ejemplo para Semana Santa, sin necesidad de modificar ni borrar los existentes.

Supuesto: dos buses pueden hacer la misma ruta en el mismo horario. Por ejemplo, un sábado con alta demanda puede requerir un segundo bus en el mismo recorrido y hora. Esto se maneja en la entidad Trip, no en Schedule.

Relación: 1:N entre Company y Route. 1:N entre Route y Schedule.

---

### Route, Stop y RouteStop — paradas

Una ruta tiene múltiples paradas y una parada puede pertenecer a múltiples rutas. La relación entre rutas y paradas se maneja con la entidad intermedia RouteStop, que además del stop_id y el route_id guarda el order_index, que indica la posición de la parada dentro de la ruta.

El order_index es necesario porque la base de datos no garantiza el orden de inserción. Sin ese campo no sería posible dibujar el recorrido en el mapa en el orden correcto ni calcular tiempos estimados por parada.

RouteStop también puede guardar un estimated_time_offset, que representa cuántos minutos después de la salida el bus debería llegar a esa parada. Esto permite mostrar al usuario horarios estimados por parada sin necesidad de cálculos complejos.

Relación: N:M entre Route y Stop, resuelta con la entidad intermedia RouteStop.

---

### Schedule, Bus y Trip — viajes reales

Un Trip representa la instancia concreta de un bus ejecutando una ruta en una fecha específica. Es la entidad que conecta un horario con un bus real en un día real.

Sin Trip no sería posible responder preguntas como qué bus está actualmente haciendo la ruta Cartago-Orosi a las 9:00pm, ni llevar registro de si el viaje se realizó, si hubo retraso o a qué hora salió y llegó realmente.

Un Trip tiene la FK del horario que lo origina, la FK del bus asignado, la fecha del viaje, el estado, la hora real de salida, la hora real de llegada y los minutos de retraso. Estos últimos campos son opcionales para el MVP pero abren la posibilidad de analíticas futuras como métricas de puntualidad por empresa.

Supuesto: los buses no están casados con un horario fijo. Si el bus asignado a un horario no está disponible, se puede asignar otro bus al mismo Trip. Esto se maneja en lógica de negocio.

Relación: N:M entre Schedule y Bus, resuelta con la entidad Trip.

---

### AuditLog — auditoría

Para registrar quién modificó qué y cuándo, se utiliza una tabla de auditoría centralizada llamada AuditLog. En lugar de crear relaciones N:M entre administradores y cada entidad gestionable, AuditLog recibe eventos de todas las entidades del sistema.

Cada fila registra el usuario que ejecutó la acción, el tipo de entidad afectada, el id del registro, la acción realizada, el estado anterior y el estado nuevo en formato JSON, y la fecha y hora del evento.

Esto permite responder preguntas como qué cambios se hicieron sobre un bus específico, qué acciones realizó un administrador en particular, o qué tenía un registro antes de ser modificado. Además, si se agrega una nueva entidad al sistema en el futuro, no es necesario crear una nueva tabla de auditoría, solo empezar a registrar eventos con el nuevo entity_type.



---
