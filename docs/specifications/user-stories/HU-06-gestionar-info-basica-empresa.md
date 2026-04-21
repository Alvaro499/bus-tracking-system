
---

## **HU-22 — Gestionar información básica de la empresa**

> Como administrador de una empresa de transporte, quiero actualizar la información básica de mi empresa para mantener los datos visibles a los usuarios correctos y actualizados.

---

### **Descripción:**

El administrador de la empresa accede al panel de gestión y puede visualizar y editar la información básica de la empresa, como nombre, correo electrónico, teléfono y estado.
Los cambios realizados se guardan en el sistema y se reflejan inmediatamente en las vistas públicas donde aplique.

---

### **Supuestos:**

* Solo usuarios con rol **OWNER** o **ADMIN** pueden gestionar la información de la empresa.
* Cada administrador solo puede modificar la información de su propia empresa.
* El campo `status` permite activar o desactivar la empresa en la plataforma.
* El correo electrónico y teléfono deben ser únicos en el sistema.
* Los cambios quedan registrados en el sistema (auditoría opcional según HU futura).
* No se permite eliminar la empresa desde esta funcionalidad.

---

### **Criterios de aceptación:**

* **CA-01** — Al ingresar al panel de empresa, el administrador visualiza la información actual: nombre, correo, teléfono y estado.
* **CA-02** — Al editar los datos con valores válidos y guardar, el sistema actualiza la información correctamente.
* **CA-03** — Si el administrador intenta guardar un correo o teléfono ya existente, el sistema muestra un error y no permite continuar.
* **CA-04** — Al cambiar el estado a **INACTIVE**, la empresa deja de estar disponible para usuarios públicos.
* **CA-05** — Solo usuarios con rol **OWNER** o **ADMIN** pueden acceder a esta funcionalidad.
* **CA-06** — Si un usuario sin permisos intenta acceder, el sistema bloquea el acceso.

---

### **Definition of Done:**

* [ ] Formulario de edición de empresa implementado con validaciones.
* [ ] Persistencia de cambios en la base de datos funcionando correctamente.
* [ ] Validaciones de unicidad para correo y teléfono implementadas.
* [ ] Los criterios CA-01 a CA-06 están cubiertos por pruebas automatizadas.
* [ ] Interfaz funcional en móvil y escritorio.
* [ ] Sin bugs críticos abiertos relacionados a esta HU.

---

### **Mockup**

```
─────────────────────────────────────────────
  Información de la empresa
─────────────────────────────────────────────

  Nombre de la empresa
  [ Autotransportes Moravia SA         ]

  Correo electrónico
  [ contacto@moravia.com               ]

  Teléfono
  [ +506 2222-3333                     ]

  Estado
  [ ACTIVA  ▼ ]

─────────────────────────────────────────────
  [ Guardar cambios ]
─────────────────────────────────────────────
```

---
