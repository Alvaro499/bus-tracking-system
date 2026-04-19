
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

