
# Restrictions, Constraints and Special Details For DB

---

## UNIQUES

```sql
-- Schedule: evitar horarios duplicados exactos
UNIQUE (route_id, day_of_week, departure_time, start_date)

-- Trip: un viaje por horario por día
UNIQUE (schedule_id, trip_date)

-- RouteStop: evitar dos paradas en la misma posición dentro de una ruta
UNIQUE (route_id, order_index)

-- Company: identificador fiscal único
UNIQUE (tax_id)

-- User: email único
UNIQUE (email)

-- CompanyUser: evitar duplicar usuario en la misma empresa
UNIQUE (user_id, company_id)
```

---

## CHECKS / VALIDACIONES

```sql
-- Schedule: día válido
CHECK (day_of_week BETWEEN 1 AND 7)

-- Schedule: fechas coherentes
CHECK (end_date IS NULL OR end_date >= start_date)
```

---

## PRIMARY KEYS ESPECIALES

```sql
-- BusLocation: una ubicación por bus
PRIMARY KEY (bus_id)
```

---

## ROLES

### Global Role (User)

```text
PLATFORM_ADMIN
COMPANY_USER
```

---

### CompanyUser Role

```text
OWNER -> manage admins
ADMIN -> manage company´s resources
```

---

## ENUMS / STATUS

### CompanyRequest.status

```text
PENDING
APPROVED
REJECTED
```

---

### Bus.status (sugerido)

```text
ACTIVE
INACTIVE
MAINTENANCE
```

---

### Trip.status

```text
PLANNED
IN_PROGRESS
COMPLETED
CANCELLED
REASSIGNED
```

---

### AuditLog.action

```text
CREATE
UPDATE
DELETE
ASSIGN
REASSIGN
```

---

## FOREIGN KEYS IMPORTANTES

```text
CompanyUser.user_id → User.id
CompanyUser.company_id → Company.id

CompanyRequest.company_id → Company.id
CompanyRequest.reviewed_by → User.id

Bus.company_id → Company.id

Schedule.route_id → Route.id

Trip.schedule_id → Schedule.id
Trip.bus_id → Bus.id

RouteStop.route_id → Route.id
RouteStop.stop_id → Stop.id

BusLocation.bus_id → Bus.id

AuditLog.user_id → User.id
```

---

## DETAILS

---

### Schedule Table - end_date attribute

En vez de modificar o borrar un horario existente, simplemente agregamos uno nuevo con fechas específicas. Por ejemplo:

```
Schedule
┌──────┬────────────────┬─────────────┬────────────┬────────────┐
│ id   │ departure_time │ day_of_week │ start_date │ end_date   │
├──────┼────────────────┼─────────────┼────────────┼────────────┤
│ s-1  │ 09:00          │ 1 (lunes)   │ 2026-01-01 │ null       │ ← horario normal
│ s-2  │ 07:00          │ 5 (viernes) │ 2026-04-13 │ 2026-04-18 │ ← Semana Santa
└──────┴────────────────┴─────────────┴────────────┴────────────┘
```

El `null` en `end_date` significa que ese horario no tiene fecha de vencimiento.

- Nunca se actualiza ni borra  
- Solo se agregan nuevos registros  
- Se preserva historial para `Trip`

---

### Trip como ejecución real

```text
Schedule → plan
Trip     → lo que realmente pasó
```

- El bus puede cambiar (`bus_id`)
    
- El estado refleja lo ocurrido (`status`)
    
- No se crean múltiples trips para el mismo horario/día
    

---

### BusLocation sin historial

```text
1 bus = 1 ubicación
```

- Se sobreescribe la fila
    
- No se guarda histórico (MVP)
    
- `updated_at` indica frescura
    

---

### RouteStop como entidad débil

- Depende de `Route` y `Stop`
    
- PK artificial (`id`):

```sql
UNIQUE (route_id, order_index)
```

---

### Separación de responsabilidades

```text
Schedule → cuándo debería pasar
Trip     → qué pasó realmente
AuditLog → qué cambios ocurrieron
CompanyUser → relación estable
```

---

### created_at

- Presente en entidades principales
    
- Permite:
    
    - orden temporal
        
    - debugging real
        
    - features futuras
        
