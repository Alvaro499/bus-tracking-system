
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



## E-03 — Gestión interna de la empresa

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

