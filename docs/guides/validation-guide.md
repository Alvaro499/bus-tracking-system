
# Guía de Validaciones – BusTrack CR

Estándar de validaciones sincronizado entre backend y frontend.

---

## 1. Backend: Validaciones en Value Objects

Validar en el **constructor** del Value Object. Cuando los datos son inválidos, lanzar `ValidationException`.

**Ejemplos en proyecto:**
- [`GpsCoordinate.java`](../../backend/src/main/java/com/bustracking/shared/valueobjects/GpsCoordinate.java) — Valida rango de latitud (-90 a 90) y longitud (-180 a 180)
- [`Plate.java`](../../backend/src/main/java/com/bustracking/companies/domain/valueobjects/Plate.java) — Valida no-null, no-blank, 5-10 caracteres
- [`InternalNumber.java`](../../backend/src/main/java/com/bustracking/companies/domain/valueobjects/InternalNumber.java) — Valida opcional, 3-20 caracteres si presente
- [`BusLocation.java`](../../backend/src/main/java/com/bustracking/tracking/domain/model/BusLocation.java) — Valida que campos requeridos no sean null

**Patrón:**
```java
public MyValueObject(String value) {
    validate(value);  // Lanzar excepción si inválido
    this.value = value;
}

private void validate(String value) {
    if (value == null) {
        throw new ValidationException(
            ErrorCode.MISSING_REQUIRED_FIELD,
            "Campo requerido",
            "value no puede ser null"
        );
    }
    // ... más validaciones
}
```

---

## 2. Backend: Excepciones

Usar jerarquía de excepciones (extienden `ApplicationException`):

| Excepción | Cuándo | ErrorCode |
|-----------|--------|-----------|
| `ValidationException` | Datos inválidos | `INVALID_INPUT`, `MISSING_REQUIRED_FIELD` |
| `NotFoundException` | Recurso no existe | `*_NOT_FOUND` |
| `BusinessRuleException` | Violación de regla | `INVALID_STATE`, `DUPLICATE_RESOURCE` |
| `ExternalServiceException` | Servicio externo falla | `EXTERNAL_SERVICE_ERROR` |

Estructura requerida:
- `errorCode` — Código único (contrato con frontend)
- `userMessage` — Legible para usuario (en español)
- `devMessage` — Detalle técnico para logs

---

## 3. Backend: Handlers

Cada módulo tiene su propio `ExceptionHandler` para convertir excepciones a respuestas HTTP.

**Ejemplos en proyecto:**
- [`TrackingExceptionHandler.java`](../../backend/src/main/java/com/bustracking/tracking/infrastructure/web/error/TrackingExceptionHandler.java)
- [`AdminExceptionHandler.java`](../../backend/src/main/java/com/bustracking/admin/infrastructure/web/error/AdminExceptionHandler.java)

**Mapeo HTTP:**
- `ValidationException` → 400
- `NotFoundException` → 404
- `BusinessRuleException` → 422
- `ExternalServiceException` → 502


---

## 4. Frontend: Estructura de Error

El backend retorna errores con formato JSON:

```json
{
  "code": "BUS_NOT_FOUND",
  "message": "Bus no encontrado",
  "timestamp": 1711612345000
}
```

El `code` es el **contrato inmutable** con el frontend. Usar el `code` para decidir qué hacer, no el `message`.

---

## 5. Frontend: Clases de Utilidad

Crear tipos y clases para manejar errores:

```typescript
// lib/types/apiError.ts
export interface ApiErrorResponse {
  code: string;
  message: string;
  timestamp: number;
}

export class ApiErrorClass extends Error {
  constructor(
    public code: string,
    public message: string,
    public status: number,
    public timestamp: number
  ) {
    super(message);
  }
}
```

---

## 6. Frontend: Servicios

Usar interceptor de axios para transformar errores del backend a `ApiErrorClass`:

```typescript
// lib/httpClient.ts
import axios from 'axios';
import { ApiErrorResponse, ApiErrorClass } from './types/apiError';

const httpClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL,
});

httpClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.data) {
      const { code, message, timestamp } = error.response.data;
      throw new ApiErrorClass(code, message, error.response.status, timestamp);
    }
    throw new ApiErrorClass('INTERNAL_ERROR', 'Error inesperado', 500, Date.now());
  }
);

export default httpClient;
```

Servicios usan el cliente HTTP (ver [code-style-frontend.md](./code-style-frontend.md) sección 6.4).

---

## 7. Frontend: Manejo en Componentes

Crear hook para centralizar lógica de errores:

```typescript
// hooks/useErrorHandler.ts
import { useState } from 'react';
import { ApiErrorClass } from '@/lib/types/apiError';

export const useErrorHandler = () => {
  const [error, setError] = useState<ApiErrorClass | null>(null);
  
  const handleError = (err: unknown) => {
    if (err instanceof ApiErrorClass) setError(err);
    else setError(new ApiErrorClass('INTERNAL_ERROR', 'Error inesperado', 500, Date.now()));
  };
  
  const clearError = () => setError(null);
  
  return { error, handleError, clearError };
};
```

Usar en componentes para capturar errores de servicios y reaccionar según el `code`.

---

## 8. Frontend: Validación de Formularios

Validar en formularios antes de enviar al backend:

```typescript
// lib/validators/coordinateValidator.ts
export interface ValidationError {
  field: string;
  message: string;
}

export const coordinateValidator = {
  validate(lat: string, lng: string): ValidationError[] {
    const errors: ValidationError[] = [];
    
    const latNum = parseFloat(lat);
    if (isNaN(latNum) || latNum < -90 || latNum > 90) {
      errors.push({ field: 'latitude', message: 'Latitud entre -90 y 90' });
    }
    
    const lngNum = parseFloat(lng);
    if (isNaN(lngNum) || lngNum < -180 || lngNum > 180) {
      errors.push({ field: 'longitude', message: 'Longitud entre -180 y 180' });
    }
    
    return errors;
  },
};
```

---

## 9. Sincronización Backend-Frontend

El `ErrorCode` es contrato inmutable. Cuando backend agrega códigos nuevos, frontend debe actualizarse.

**Tabla de ErrorCodes sincronizada:**

Ver [code-style-frontend.md](./code-style-frontend.md) sección 6.2 para lista completa de ErrorCodes.

**Mejores prácticas:**
- Nunca cambiar el nombre de un `ErrorCode` (es contrato)
- Versionar app si cambias cómo manejas un código
- Sincronizar regularmente entre backend y frontend
- Loguear errores solo en desarrollo
