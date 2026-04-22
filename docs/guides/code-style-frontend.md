# Documento de Guía de Estilo de Código del Frontend

**BusTrack CR — Aplicación Next.js**

---

## 1. Introducción

Esta guía establece estándares de código para el frontend desarrollado con **Next.js + TypeScript**. El objetivo es mantener consistencia, facilitar el mantenimiento y garantizar que el manejo de errores sea coherente con la API del backend.

---

## 2. Estructura del Código

* Definición de la organización de archivos y carpetas.
* Convenciones de nomenclatura para archivos y componentes.
* Separación de responsabilidades en el código.
* Convenciones para la declaración de clases, métodos, variables y funciones.

---

### Organización de Archivos y Carpetas

El proyecto sigue una estructura modular para facilitar el mantenimiento y la escalabilidad. A continuación se describen los directorios del frontend:

* **adapters**: Mapeo de modelos externos a modelos de negocio.
* **assets**: Archivos estáticos como imágenes, íconos, fuentes, etc.
* **components**: Componentes reutilizables utilizados en toda la aplicación.
* **contexts**: Proveedores de contexto global disponibles en la aplicación.
* **hooks**: Hooks personalizados que extienden la funcionalidad de los componentes.
* **models**: Definición de interfaces y tipos utilizados en la aplicación.
* **pages**: Vistas principales de la aplicación como Login, Dashboard, etc.
* **services**: Encapsulan llamadas a APIs o servicios externos (login, registro, etc).
* **utilities**: Funciones auxiliares puras que no gestionan estado.

### 2.1 Organización de Archivos y Carpetas

El proyecto sigue una estructura modular que se integra con la arquitectura de Next.js (App Router). La organización refleja Clean Architecture, con capas claramente separadas:

**En la raíz del proyecto:**
```
frontend/
├── src/
│   ├── app/                    # Next.js App Router
│   │   ├── (layout)/           # Layouts compartidos
│   │   ├── (auth)/             # Contexto de autenticación
│   │   ├── public/             # Rutas públicas
│   │   ├── dashboard/          # Rutas protegidas
│   │   └── api/                # Route handlers (Backend en el mismo repo si aplica)
│   ├── components/             # Componentes React reutilizables
│   ├── hooks/                  # Custom hooks
│   ├── services/               # Servicios de API (comunicación con backend)
│   ├── models/                 # Tipos e interfaces TypeScript
│   ├── context/                # Contextos de React (estado global)
│   ├── adapters/               # Mapeo de modelos externos → internos
│   ├── utils/                  # Funciones auxiliares puras
│   ├── lib/                    # Configuraciones y helpers (axios, auth, etc)
│   └── assets/                 # Archivos estáticos (imágenes, íconos)
├── public/                     # Assets estáticos servidos directamente
├── .env.local                  # Variables de entorno
├── next.config.ts              # Configuración de Next.js
├── tsconfig.json               # Configuración de TypeScript
└── package.json
```

**Directorios principales:**

* **`app/`**: Estructura de rutas del App Router de Next.js. Cada carpeta = ruta (excepto `(layout)` y `(auth)` que son grupos sin afectar la URL).
* **`components/`**: Componentes reutilizables (botones, tarjetas, formularios, etc). Sin lógica de negocio.
* **`hooks/`**: Custom hooks que encapsulan lógica reutilizable (fetch, autenticación, estado local).
* **`services/`**: Capa de comunicación con APIs externas (tu backend Spring Boot, APIs de terceros).
* **`models/`**: Definición de tipos e interfaces TypeScript (mapea con DTOs del backend).
* **`context/`**: Contextos de React para estado global (AuthContext, ThemeContext, etc).
* **`adapters/`**: Transforma respuestas del backend en modelos internos de la app.
* **`utils/`**: Funciones puras sin efectos secundarios (formateo, validación, cálculos).
* **`lib/`**: Configuraciones compartidas (cliente axios, constantes, helpers de autenticación).

---

### 2.2 Estructura interna de las páginas

Para mantener una organización más clara, dentro de cada carpeta de página se utiliza una estructura que agrupa archivos por propósito:

* `index.tsx`: componente principal de la vista.
* `hooks.ts`: lógica específica de hooks relacionados a esa página.
* `types.ts`: definición de interfaces o tipos usados en esa página.

Esta estructura permite tener el código **más centralizado y enfocado** dentro del componente de página que se está desarrollando, facilitando su comprensión y mantenimiento

Para las páginas principales (dentro de `app/`), mantén esta estructura:

```
app/dashboard/
├── page.tsx                # Componente principal de la página
├── layout.tsx              # Layout específico (si es necesario)
├── hooks.ts                # Hooks específicos de esta página
├── types.ts                # Interfaces/tipos específicos
├── components/             # Componentes internos de la página
│   ├── DashboardHeader.tsx
│   └── DashboardContent.tsx
```

- `page.tsx`: Componente raíz de la página (llamado automáticamente por Next.js).
- `layout.tsx`: Layout específico para esta ruta (si difiere del layout padre).
- `hooks.ts`: Hooks personalizados usados solo en esta página.
- `types.ts`: Interfaces y tipos locales de la página.
- `components/`: Componentes menores que solo se usan en esta página.

Esta estructura permite tener el código **centralizado y enfocado** en cada sección de la aplicación.

---

## 3. Convenciones de Nomenclatura para Archivos y Componentes

### 3.1 Archivos de Componentes

Los nombres de los archivos de componentes deben usar **PascalCase** (por ejemplo, `UserCard.tsx`, `BusLocationMap.tsx`). Esta convención asegura que los componentes se distingan visualmente del resto del código.

### 3.2 Hooks Personalizados

Los hooks personalizados deben empezar con el prefijo `use`, seguido de una descripción clara de su función en el nombre. Ejemplos:

* `useFetchData.ts`
* `useAuth.ts`
* `useLocalStorage.ts`

### 3.3 Modelos de Datos

Los archivos que contienen las clases o estructuras de modelos de datos deben tener un sufijo `.models.ts` y deben estar en **PascalCase**. Ejemplo:

* `BusLocation.models.ts`
* `User.models.ts`

### 3.4 Interfaces y Tipos

Las interfaces y los tipos deben ser declarados en **PascalCase**. Ejemplo:

* `interface BusEvent`
* `type ApiResponse`

### 3.5 Servicios

Los archivos de servicios deben seguir el formato **[nombre del recurso].service.ts**. Ejemplo:

* `buses.service.ts`
* `auth.service.ts`

---

## 4. Separación de Responsabilidades

### 4.1 adapters

   * **Responsabilidad**: Este directorio se encarga de **transformar** o **mapear** los datos entre modelos externos (de APIs, bases de datos, etc.) y los modelos internos que utiliza la lógica de negocio de la aplicación. No debe contener lógica de UI ni manejar estado.
   * **Ejemplo**: `BusLocationAdapter.ts` mapea los datos de la API a un formato compatible con los modelos internos de la aplicación.

### 4.2 assets

   * **Responsabilidad**: Aquí se almacenan los **archivos estáticos** (imágenes, íconos, fuentes, etc.) utilizados por la aplicación. No debe contener lógica ni procesamiento de datos.
   * **Ejemplo**: `logo.png`, `icon.svg`, etc.

### 4.3 components

   * **Responsabilidad**: Los componentes deben ser **reutilizables** y encargarse de la **presentación** de la aplicación. Deben recibir datos desde sus **props** o el **contexto** y manejar eventos (clics, formularios, etc.), pero no deben contener lógica de negocio ni llamadas a APIs.
   * **Ejemplo**: `Button.tsx`, `Card.tsx`.

### 4.4 context

   * **Responsabilidad**: Los contextos gestionan el **estado global** de la aplicación, proporcionando datos a través de toda la aplicación sin necesidad de prop drilling. Aquí debe definirse la lógica de los datos globales, pero no debe incluirse la lógica de presentación o UI.
   * **Ejemplo**: `AuthContext.tsx`, `BusTrackingContext.tsx`.

### 4.5 hooks

   * **Responsabilidad**: Los hooks personalizados se encargan de la **lógica reutilizable**. Estos pueden manejar estado, interactuar con APIs o gestionar efectos secundarios, pero no deben involucrarse en la UI ni tener lógica de presentación.
   * **Ejemplo**: `useFetchData.ts`, `useAuth.ts`.

### 4.6 models

* **Responsabilidad**: Definen interfaces y tipos TypeScript que mapean con los DTOs/modelos del backend. Sin lógica de negocio.
* **Ejemplo**: `BusLocation.models.ts`, `User.models.ts`.

### 4.7 services

* **Responsabilidad**: Encapsulan comunicación con APIs (tu backend Spring Boot, APIs externas). Realizan llamadas HTTP, transforman respuestas.
* **Ejemplo**: `buses.service.ts`, `auth.service.ts`, `tracking.service.ts`.

### 4.8 utils

* **Responsabilidad**: Funciones puras auxiliares sin efectos secundarios. Formateo, validación, cálculos.
* **Ejemplo**: `formatDate.ts`, `validateEmail.ts`, `calculateDistance.ts`.

### 4.9 lib

* **Responsabilidad**: Las utilidades contienen **funciones auxiliares** que realizan tareas pequeñas y específicas, sin tener que ver con el estado o la UI. Son funciones puras que no dependen de ninguna estructura de datos o estado de la aplicación.
* **Ejemplo**: `formatDate.ts`, `validateEmail.ts`, `calculateDistance.ts`

---

## 5. Convenciones de Código

### 5.1 Métodos y Funciones

Deben seguir **camelCase** (ej: `handleClick`, `fetchBuses`, `submitForm`). Usar funciones flecha para preservar el contexto.

```typescript
const handleSubmit = async () => {
  // Código
}

const fetchBuses = () => {
  // Código
}
```

Nombres descriptivos que empiecen con verbo: `fetchData`, `handleSubmit`, `validateEmail`.

### 5.2 Variables

Siguen **camelCase** (ej: `userData`, `isLoading`, `busCount`).

Variables booleanas: Usa prefijos `is`, `has`, `can` (ej: `isAuthenticated`, `hasPermission`, `canEdit`).

Constantes: **UPPER_SNAKE_CASE** (ej: `API_BASE_URL`, `MAX_RETRIES`).

```typescript
const userData = { name: 'John' };
const isLoading = false;
const API_BASE_URL = 'https://api.example.com';
```

---

## 6. Manejo de Errores y Sincronización con Backend

El frontend debe estar sincronizado con la estructura de errores del backend de Spring Boot. Esta sección documenta cómo manejar las respuestas de error.

### 6.1 Estructura de Error del Backend

Todas las respuestas de error del backend siguen este formato JSON:

```json
{
  "code": "ERROR_CODE_NAME",
  "message": "Mensaje amigable para el usuario en español",
  "timestamp": 1711612345000
}
```

**Campos:**
- `code`: Identificador único del error (nunca cambia, es un contrato entre backend y frontend)
- `message`: Mensaje legible para el usuario (NO contiene detalles técnicos)
- `timestamp`: Unix timestamp en milisegundos

### 6.2 ErrorCodes Disponibles

El backend define estos `ErrorCode` que el frontend debe manejar:

| ErrorCode | HTTP Status | Descripción |
|-----------|-------------|-------------|
| `INVALID_INPUT` | 400 | Input inválido del usuario |
| `INVALID_EMAIL` | 400 | Email con formato incorrecto |
| `INVALID_FORMAT` | 400 | Formato incorrecto en los datos |
| `MISSING_REQUIRED_FIELD` | 400 | Falta un campo obligatorio |
| `USER_NOT_FOUND` | 404 | Usuario no existe |
| `RESOURCE_NOT_FOUND` | 404 | Recurso no encontrado |
| `ADMIN_NOT_FOUND` | 404 | Administrador no existe |
| `BUS_NOT_FOUND` | 404 | Bus no existe |
| `BUS_LOCATION_NOT_FOUND` | 404 | Ubicación del bus no encontrada |
| `ADMIN_NOT_ACTIVE` | 422 | Admin no está activo |
| `INVALID_STATE` | 422 | Estado inválido para la operación |
| `DUPLICATE_RESOURCE` | 422 | El recurso ya existe |
| `INSUFFICIENT_PERMISSIONS` | 422 | Sin permisos para la operación |
| `UNAUTHORIZED` | 401 | No autenticado |
| `FORBIDDEN` | 403 | Autenticado pero sin acceso |
| `EXTERNAL_SERVICE_ERROR` | 502 | Error al contactar servicio externo |
| `SERVICE_UNAVAILABLE` | 502 | Servicio externo no disponible |
| `INTERNAL_ERROR` | 500 | Error inesperado del servidor |

### 6.3 Mapeo HTTP Status Codes

| Tipo de Error | Status HTTP | Acción del Frontend |
|---------------|-------------|-------------------|
| Validación (INVALID_*) | 400 | Mostrar mensaje de validación en formulario |
| No encontrado (*_NOT_FOUND) | 404 | Mostrar página "Recurso no disponible" |
| Regla de negocio | 422 | Mostrar modal/toast con la restricción |
| Permisos | 401/403 | Redirigir a login o mostrar acceso denegado |
| Servicio externo | 502 | Mostrar "Servicio temporalmente no disponible" |
| Error interno | 500 | Mostrar "Error inesperado, intente más tarde" |

### 6.4 Manejo de Errores en Servicios

Crea un tipo para la respuesta de error:

```typescript
// lib/types/apiError.ts
export interface ApiError {
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

En tus servicios, intercepta y transforma errores:

```typescript
// services/buses.service.ts
import axios, { AxiosError } from 'axios';
import { ApiError, ApiErrorClass } from '@/lib/types/apiError';

const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL,
});

apiClient.interceptors.response.use(
  response => response,
  (error: AxiosError<ApiError>) => {
    if (error.response?.data) {
      const { code, message, timestamp } = error.response.data;
      throw new ApiErrorClass(code, message, error.response.status, timestamp);
    }
    throw new ApiErrorClass(
      'INTERNAL_ERROR',
      'Error inesperado',
      error.status || 500,
      Date.now()
    );
  }
);

export const busesService = {
  async getBusLocation(busId: string) {
    try {
      const response = await apiClient.get(`/tracking/buses/${busId}/location`);
      return response.data;
    } catch (error) {
      if (error instanceof ApiErrorClass) {
        throw error; // Re-throw para que el componente lo maneje
      }
      throw new ApiErrorClass('INTERNAL_ERROR', 'Error desconocido', 500, Date.now());
    }
  },
};
```

### 6.5 Manejo de Errores en Componentes

Usa un hook personalizado para manejar errores:

```typescript
// hooks/useErrorHandler.ts
import { useState } from 'react';
import { ApiErrorClass } from '@/lib/types/apiError';

export const useErrorHandler = () => {
  const [error, setError] = useState<ApiErrorClass | null>(null);

  const handleError = (err: unknown) => {
    if (err instanceof ApiErrorClass) {
      setError(err);
    } else {
      setError(new ApiErrorClass('INTERNAL_ERROR', 'Error inesperado', 500, Date.now()));
    }
  };

  const clearError = () => setError(null);

  const getErrorAction = () => {
    if (!error) return null;

    switch (error.code) {
      case 'UNAUTHORIZED':
        return { type: 'redirect', target: '/login' };
      case 'FORBIDDEN':
        return { type: 'show', message: 'No tienes acceso a este recurso' };
      case 'BUS_NOT_FOUND':
      case 'RESOURCE_NOT_FOUND':
        return { type: 'notFound' };
      case 'INVALID_INPUT':
      case 'MISSING_REQUIRED_FIELD':
        return { type: 'validation', message: error.message };
      default:
        return { type: 'toast', message: error.message };
    }
  };

  return { error, handleError, clearError, getErrorAction };
};
```

En un componente:

```typescript
// components/BusLocationMap.tsx
'use client';

import { useErrorHandler } from '@/hooks/useErrorHandler';
import { busesService } from '@/services/buses.service';
import { useEffect, useState } from 'react';

export function BusLocationMap({ busId }: { busId: string }) {
  const [location, setLocation] = useState(null);
  const [loading, setLoading] = useState(true);
  const { error, handleError, clearError } = useErrorHandler();

  useEffect(() => {
    const fetchLocation = async () => {
      try {
        clearError();
        const data = await busesService.getBusLocation(busId);
        setLocation(data);
      } catch (err) {
        handleError(err);
      } finally {
        setLoading(false);
      }
    };

    fetchLocation();
  }, [busId]);

  if (loading) return <div>Cargando...</div>;
  
  if (error?.code === 'BUS_NOT_FOUND') {
    return <div>Bus no encontrado</div>;
  }

  if (error) {
    return <div className="text-red-500">{error.message}</div>;
  }

  return <div>{/* Render location map */}</div>;
}
```

### 6.6 Mejores Prácticas

1. **Nunca expongas detalles técnicos** al usuario. El backend maneja eso con `devMessage` en logs.

2. **Trata cada ErrorCode específicamente**. No hagas un catch-all genérico.

3. **El ErrorCode es un contrato inmutable**. Si cambias cómo manejas un código, versiona tu app.

4. **Registra errores en desarrollo**. Console.log el objeto de error completo en dev, nunca en producción.

```typescript
if (process.env.NODE_ENV === 'development') {
  console.error('[API Error]', error);
}
```

5. **Sincroniza periódicamente los ErrorCodes**. Cuando el backend agrega nuevos ErrorCode, actualiza esta guía y tu código.

---


