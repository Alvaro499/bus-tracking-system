
## Épicas

|Épica|Descripción|
|---|---|
|E-01|Visualización pública de buses|
|E-02|Registro y autenticación|
|E-03|Gestión interna de la empresa|
|E-04|Seguimiento en tiempo real|
|E-05|Administración global del sistema|

---

## E-01 — Visualización pública


## E-02 — Registro y autenticación

---

**HU-08 — Registrar empresa**

> Como **dueño de una empresa de buses**, quiero **registrar mi empresa en la plataforma** para **poder gestionar mis buses y rutas.**

**Criterios de aceptación:**

- **CA-01** — Given lleno el formulario con datos válidos / When envío la solicitud / Then recibo un correo de verificación
- **CA-02** — Given uso un correo ya registrado / When intento registrarme / Then el sistema me avisa y no me deja continuar
- **CA-03** — Given verifico mi correo / When completo el proceso / Then mi cuenta queda pendiente de aprobación por el administrador
- **CA-04** — Given mi solicitud fue aprobada / When inicio sesión / Then puedo acceder al panel de gestión de mi empresa

---

**HU-09 — Iniciar sesión**

> Como **dueño de empresa o administrador**, quiero **iniciar sesión en la plataforma** para **acceder a las funciones de gestión.**

**Criterios de aceptación:**

- **CA-01** — Given ingreso credenciales válidas / When inicio sesión / Then accedo al panel según mi rol
- **CA-02** — Given ingreso una contraseña incorrecta / When intento iniciar sesión / Then veo un mensaje de error
- **CA-03** — Given mi cuenta no ha sido aprobada / When intento iniciar sesión / Then el sistema me informa que está pendiente de aprobación

---

## E-03 — Gestión interna de la empresa

---

**HU-10 — Gestionar buses de la flota**

> Como **administrador de empresa**, quiero **registrar y administrar los buses de mi flota** para **tener el control de mis unidades en el sistema.**

**Criterios de aceptación:**

- **CA-01** — Given completo el formulario de un bus / When lo registro / Then queda pendiente de aprobación del administrador global
- **CA-02** — Given el bus fue aprobado / When consulto mi flota / Then aparece disponible para asignarse a rutas
- **CA-03** — Given registro un bus / When indico si tiene rampa de accesibilidad / Then esa información queda visible para los usuarios
- **CA-04** — Given un bus ya no está en servicio / When lo desactivo / Then deja de aparecer en el mapa


---

**HU-11 — Gestionar rutas y variantes**

> Como **administrador de empresa**, quiero **registrar las rutas y sus variantes** para **que los usuarios vean correctamente los recorridos disponibles.**

**Criterios de aceptación:**

- **CA-01** — Given creo una ruta / When la guardo / Then puedo agregarle variantes con destinos distintos
- **CA-02** — Given una ruta tiene variantes / When el usuario consulta / Then ve cada variante como una opción separada
- **CA-03** — Given modifico una ruta existente / When guardo los cambios / Then la información actualizada se refleja para los usuarios

---

**HU-12 — Gestionar horarios**

> Como **administrador de empresa**, quiero **registrar y actualizar los horarios de salida** para **que los usuarios sepan a qué horas opera cada ruta.**

**Criterios de aceptación:**

- **CA-01** — Given agrego un horario con hora y origen / When lo guardo / Then aparece en la consulta pública de horarios
- **CA-02** — Given modifico un horario / When guardo los cambios / Then el horario anterior deja de mostrarse
- **CA-03** — Given elimino un horario / When el usuario consulta / Then ese horario ya no aparece en la lista

---

**HU-13 — Gestionar paradas**

> Como **administrador de empresa**, quiero **registrar y actualizar las paradas de mis rutas** para **que los usuarios siempre tengan información correcta sobre dónde pasa el bus.**

**Criterios de aceptación:**

- **CA-01** — Given agrego una parada con nombre y ubicación / When la guardo / Then aparece en el mapa de esa ruta
- **CA-02** — Given modifico o elimino una parada / When el usuario consulta / Then ve la información actualizada
- **CA-03** — Given las paradas tienen un orden / When se muestran en el mapa / Then aparecen en ese orden a lo largo de la ruta

---

**HU-14 — Definir precio informativo**

> Como **administrador de empresa**, quiero **registrar el precio del pasaje de cada ruta** para **que los usuarios tengan esa información antes de viajar.**

**Criterios de aceptación:**

- **CA-01** — Given registro un precio para una ruta / When el usuario consulta esa ruta / Then ve el precio como referencia
- **CA-02** — Given actualizo el precio / When el usuario consulta / Then ve el precio nuevo

---

**HU-15 — Gestionar administradores de la empresa**

> Como **dueño de empresa**, quiero **asignar uno o dos administradores adicionales** para **que puedan gestionar la información sin depender siempre de mí.**

**Criterios de aceptación:**

- **CA-01** — Given invito a un usuario como administrador / When acepta / Then puede gestionar buses, rutas y horarios de mi empresa
- **CA-02** — Given tengo ya dos administradores asignados / When intento agregar otro / Then el sistema no me lo permite
- **CA-03** — Given elimino a un administrador / When intenta acceder / Then ya no tiene permisos sobre mi empresa

---

## E-04 — Seguimiento en tiempo real

---

**HU-16 — Enviar ubicación desde el bus**

> Como **operador del dispositivo en el bus**, quiero **que el dispositivo envíe la ubicación automáticamente mientras la ruta está activa** para **que los usuarios puedan ver dónde está el bus en tiempo real.**

**Criterios de aceptación:**

- **CA-01** — Given el operador inicia la ruta en el dispositivo / When está en circulación / Then la ubicación se envía automáticamente de forma periódica
- **CA-02** — Given el operador termina la ruta / When la cierra en el dispositivo / Then el bus deja de aparecer en el mapa
- **CA-03** — Given el dispositivo pierde conexión / When la recupera / Then retoma el envío de ubicación automáticamente

---

**HU-17 — Autenticar el dispositivo del bus**

> Como **administrador de empresa**, quiero **que cada dispositivo instalado en un bus esté vinculado a esa unidad** para **evitar que cualquier dispositivo externo envíe ubicaciones falsas.**

**Criterios de aceptación:**

- **CA-01** — Given el dispositivo tiene credenciales válidas del bus / When envía su ubicación / Then el sistema la acepta y actualiza el mapa
- **CA-02** — Given el dispositivo no tiene credenciales válidas / When intenta enviar / Then el sistema rechaza la información

---

## E-05 — Administración global

---

**HU-18 — Aprobar o rechazar empresas**

> Como **administrador global**, quiero **revisar y aprobar o rechazar solicitudes de registro de empresas** para **evitar que cualquier persona publique información falsa en el sistema.**

**Criterios de aceptación:**

- **CA-01** — Given hay una solicitud pendiente / When la reviso / Then puedo aprobarla o rechazarla con una razón
- **CA-02** — Given apruebo una empresa / When el dueño inicia sesión / Then puede empezar a gestionar su información
- **CA-03** — Given rechazo una empresa / When el solicitante consulta su estado / Then ve el motivo del rechazo


---
   
**HU-20 — Gestionar administradores globales**

> Como **administrador global**, quiero **poder agregar o quitar otros administradores globales** para **mantener el control del sistema entre personas de confianza.**

**Criterios de aceptación:**

- **CA-01** — Given agrego un administrador global / When acepta la invitación / Then tiene acceso a todas las funciones de administración
- **CA-02** — Given elimino un administrador global / When intenta acceder / Then ya no tiene permisos

