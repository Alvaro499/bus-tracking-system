# ADR-001: Estrategia de comunicación entre módulos

**Fecha:** 2025-05-10  
**Estado:** Aceptada  

---

## Contexto

El sistema está organizado en tres módulos independientes: `tracking`, 
`companies` y `admin`. Durante la implementación de HU-17 surgió la 
necesidad de que el módulo `tracking` acceda a datos de viajes y buses 
que pertenecen al módulo `companies`.

Se evaluaron tres opciones para resolver la comunicación cross-module 
sin romper clean architecture.

---

## Opciones evaluadas

### Opción A — Módulo `shared` con contratos y DTOs compartidos
Crear un módulo neutral donde viven las interfaces y DTOs que ambos 
módulos necesitan. Ningún módulo "posee" el contrato.

**Ventajas:** ningún módulo tiene autoridad sobre el otro, útil en 
comunicación bidireccional.  
**Desventajas:** `shared` tiende a convertirse en un cajón de sastre 
con el tiempo. Tracking pierde independencia en sus tipos.

### Opción B — Contratos en el módulo que los necesita + delegate como puente
El módulo que necesita datos define el contrato en su propio `domain`. 
El módulo que provee los datos implementa ese contrato en un archivo 
de configuración (`TrackingDelegatesConfig`) que actúa como puente.

**Ventajas:** cada módulo es completamente independiente en sus tipos. 
El acoplamiento está controlado y centralizado en un único archivo.  
**Desventajas:** companies debe importar contratos de tracking 
(acoplamiento unidireccional controlado).

### Opción C — CQRS
Separar lecturas y escrituras con Query/QueryHandler/Dispatcher por 
cada operación cross-module.

**Ventajas:** estándar reconocido, escala bien en sistemas grandes.  
**Desventajas:** sobrediseño para el tamaño del proyecto. Agrega 
Command, Handler y Dispatcher por cada operación sin beneficio real 
a esta escala.

---

## Decisión

**Opción B — Contratos en el módulo que los necesita + delegate como puente.**

---

## Razones

- El proyecto es un monolito modular académico de escala pequeña.
- `tracking` queda completamente independiente en sus tipos de dominio.
- El acoplamiento entre módulos está centralizado en un único archivo 
  por dirección (`TrackingDelegatesConfig`), haciendo las dependencias 
  explícitas y fáciles de rastrear.
- Si en el futuro companies necesita datos de tracking, se crea el 
  espejo (`CompanyDelegatesConfig` en tracking) sin generar ciclos.

---

## Consecuencias

- `TripScheduleProjection` vive en `companies/domain/dto/` — es una 
  proyección de consulta, no una entidad.
- `TripView` vive en `tracking/domain/model/` — es el tipo propio de 
  tracking para representar un viaje.
- `TrackingDelegatesConfig` es el único archivo del sistema autorizado 
  a importar tanto `companies` como `tracking`.
- Cada nuevo cruce cross-module de companies → tracking sigue este 
  mismo patrón.

---

## Referencias

- https://newsletter.fractionalarchitect.io/p/20-modular-monolith-various-ways
- HU-17 — Gestionar y transmitir viaje en tiempo real