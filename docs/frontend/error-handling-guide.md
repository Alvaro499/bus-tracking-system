# Gestión de Errores en Frontend

## 1. Escenarios de Manejo de Errores

El sistema de gestión de errores en las aplicaciones frontend (management-app y passengers-app) implementa un enfoque estratificado que replica la arquitectura del backend, distinguiendo tres escenarios de error según el contexto en que ocurren:

### 1.1 Global Error Boundary

**Propósito:** Último recurso para capturar errores no esperados de renderizado.

**Cuándo ocurre:** Cuando un componente explota durante su ciclo de renderizado sin ser atrapado por ninguna otra capa.

**Comportamiento:** React automáticamente ejecuta el método `getDerivedStateFromError` del Error Boundary, permitiendo capturar la excepción antes de que llegue al usuario.

**Ejemplo de error capturado:**
```tsx
// Componente que falla si recibe null
function BusCard({ bus }) {
  return <p>{bus.plate.toUpperCase()}</p>; // Explota si bus es null
}
```

**Equivalencia Backend:** Funciona como un `@ExceptionHandler` global en Spring Boot.

---

### 1.2 Section Error Boundary

**Propósito:** Capturar errores de renderizado en secciones específicas sin afectar toda la página.

**Cuándo ocurre:** Cuando se desea aislar fallos en componentes particulares (ej: MapContainer, DriverPanel).

**Comportamiento:** El Error Boundary se posiciona alrededor de la sección específica, mostrando un mensaje de error localizado mientras el resto de la página permanece funcional.

**Diferencia con Global:** Proporciona granularidad; no destruye toda la interfaz si una sección falla.

---

### 1.3 Try-Catch en Features

**Propósito:** Manejar errores asincronos (API calls, fetch, callbacks) en el contexto de un componente feature.

**Cuándo ocurre:** Cuando se ejecuta código asincrónico en un callback (usePolling, onClick, useEffect).

**Comportamiento:** El error es atrapado en el try-catch y guardado en estado local (ej: `apiError`), permitiendo renderizar un mensaje de error sin desmontar el componente.

**Razón de ser:** React Error Boundaries NO capturan errores en callbacks asincrónicos; por ello, se requiere captura manual en el try-catch.

---

## 2. Estructura de Manejo de Errores

### 2.1 Archivos Core

```
lib/errors/
├── apiError.ts                 # Definición de ApiErrorClass e interfaz
└── GlobalErrorBoundary.tsx     # Error Boundary global
```

**lib/errors/apiError.ts:**
- Define la clase `ApiErrorClass` que representa errores de la API.
- Define la interfaz `ApiErrorResponse` que estructura la respuesta del backend.
- Contrato: `code` es inmutable (identifica el error), `userMessage` es mutable (para mostrar al usuario).

**lib/errors/GlobalErrorBoundary.tsx:**
- Componente React que extiende `Component` e implementa el patrón Error Boundary.
- Implementa `getDerivedStateFromError` para capturar excepciones.
- Implementa `componentDidCatch` para logging.
- Decide qué renderizar basado en `error.code` (no en `error.userMessage`).

### 2.2 Capas de Interceptación

```
httpClient (lib/httpClient.ts)
  ↓ (lanza ApiErrorClass si falla)
Service (infrastructure/services/*)
  ↓ (sin try-catch)
Feature Component (features/*/container.tsx)
  ↓ (try-catch captura)
  → Estado local (apiError) o
  → Propaga a GlobalErrorBoundary
```

---

## 3. Ejemplos de Implementación

### 3.1 Global Error Boundary

**Ubicación:** `lib/errors/GlobalErrorBoundary.tsx`

```tsx
export class GlobalErrorBoundary extends Component<Props, State> {
  static getDerivedStateFromError(error: unknown): State {
    if (error instanceof ApiErrorClass) {
      return { error };
    }
    return {
      error: new ApiErrorClass(
        'UNEXPECTED_ERROR',
        'Ocurrió un error inesperado',
        500,
        Date.now()
      ),
    };
  }

  componentDidCatch(error: unknown, errorInfo: any) {
    console.error('Error captured by GlobalErrorBoundary:', error, errorInfo);
  }

  render() {
    const { error } = this.state;

    if (error) {
      switch (error.code) {
        case 'BUS_NOT_FOUND':
          return (
            <div style={{ padding: '20px', color: 'red' }}>
              <h2>Bus no encontrado</h2>
              <p>El bus solicitado no existe en el sistema.</p>
            </div>
          );

        case 'INVALID_COORDINATES':
          return (
            <div style={{ padding: '20px', color: 'red' }}>
              <h2>Coordenadas inválidas</h2>
              <p>Las coordenadas enviadas no son válidas.</p>
            </div>
          );

        default:
          return (
            <div style={{ padding: '20px', color: 'red' }}>
              <h2>Error inesperado</h2>
              <p>{error.userMessage}</p>
            </div>
          );
      }
    }

    return this.props.children;
  }
}
```

**Registro en raíz (App.tsx o layout.tsx):**
```tsx
<GlobalErrorBoundary>
  {children}
</GlobalErrorBoundary>
```

---

### 3.2 Try-Catch en Feature (MapContainer - passengers-app)

**Ubicación:** `features/map/map.container.tsx`

```tsx
import { useState } from 'react';
import { busLocationService } from '../../infrastructure/services/busLocationService';
import { ApiErrorClass } from '../../lib/errors/apiError';

export function MapContainer() {
  const [busLocation, setBusLocation] = useState<BusLocation | null>(null);
  const [apiError, setApiError] = useState<ApiErrorClass | null>(null);

  usePolling(async () => {
    try {
      const location = await busLocationService.getBusLocation(BUS_ID);
      setBusLocation(location);
      setApiError(null); // Limpia error anterior si ahora funciona
    } catch (err) {
      if (err instanceof ApiErrorClass) {
        setApiError(err); // Error conocido del backend
      }
    }
  }, POLLING_INTERVAL);

  // Error localizado: la página sigue viva
  if (apiError) return <p>{apiError.userMessage}</p>;
  if (!busLocation) return <p>Cargando ubicación...</p>;

  return (
    <>
      <BusMap location={busLocation} />
      <p>Bus: {busLocation.busId}</p>
    </>
  );
}
```

**Flujo:**
1. `busLocationService.getBusLocation()` llama a `httpClient.get()`
2. Si falla, `httpClient` lanza `ApiErrorClass`
3. Try-catch captura la excepción
4. Se guarda en `apiError` (estado local)
5. El componente renderiza el mensaje de error sin desmontar

---

### 3.3 Section Error Boundary (Ejemplo: DriverPanel)

**Ubicación:** `lib/errors/SectionErrorBoundary.tsx` (reutilizable)

```tsx
import { Component, ReactNode } from 'react';
import { ApiErrorClass } from './apiError';

interface Props {
  children: ReactNode;
  section?: string; // Nombre opcional de la sección
}

interface State {
  error: ApiErrorClass | null;
}

/**
 * Reutilizable Error Boundary para secciones específicas.
 * Más granular que Global, menos destructivo.
 */
export class SectionErrorBoundary extends Component<Props, State> {
  static getDerivedStateFromError(error: unknown): State {
    if (error instanceof ApiErrorClass) {
      return { error };
    }
    return {
      error: new ApiErrorClass(
        'SECTION_ERROR',
        'Error en sección',
        500,
        Date.now()
      ),
    };
  }

  render() {
    if (this.state.error) {
      return (
        <div style={{ 
          padding: '15px', 
          border: '1px solid #ff6b6b', 
          borderRadius: '4px',
          backgroundColor: '#ffe0e0'
        }}>
          <p><strong>Error en {this.props.section || 'esta sección'}:</strong> {this.state.error.userMessage}</p>
          <button onClick={() => window.location.reload()}>Reintentar</button>
        </div>
      );
    }

    return this.props.children;
  }
}
```

**Uso en una página:**
```tsx
export default function DriverPage() {
  return (
    <div>
      <h1>Panel de Conductor</h1>
      
      <SectionErrorBoundary section="Panel de Ubicación">
        <DriverContainer />
      </SectionErrorBoundary>

      <SectionErrorBoundary section="Panel de Estadísticas">
        <StatisticsPanel />
      </SectionErrorBoundary>
    </div>
  );
}
```

**Comportamiento:**
- Si `DriverContainer` falla → solo esa sección muestra error
- Si `StatisticsPanel` falla → solo esa sección muestra error
- El resto de la página permanece funcional

---

## 4. Flujo Completo de un Error

```
┌─────────────────────────────────────────┐
│ MapContainer (Feature)                  │
│                                         │
│  usePolling(async () => {               │
│    try {                                │
│      await busLocationService.get()     │
│      ─────────────────────────────────→ │
│    } catch (err) {                      │
│      setApiError(err)  ← Capturado      │
│    }                                    │
│  })                                     │
└─────────────────────────────────────────┘
              ↓ (no se propaga)
         Renderiza localmente
         "El bus no fue encontrado"

┌─────────────────────────────────────────┐
│ Si MapContainer NO tiene try-catch:     │
│ El error se propaga...                  │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│ App.tsx                                 │
│ <GlobalErrorBoundary>                   │
│   {children}                            │
│ </GlobalErrorBoundary>                  │
└─────────────────────────────────────────┘
              ↓
       Capturado aquí
       Renderiza GlobalUI
       "Bus no encontrado"
```

---

## 5. Decisiones de Diseño

### 5.1 Error.code vs Error.userMessage

- **error.code:** Contrato entre backend y frontend. Inmutable. Se usa para tomar decisiones arquitectónicas (qué renderizar, a dónde redirigir, etc).
- **error.userMessage:** Mensaje para mostrar al usuario. Mutable. Puede cambiar según la región, configuración, etc.

### 5.2 Try-Catch Obligatorio en Features

Los servicios NO incluyen try-catch. El error se lanza y fluye hacia:
1. Try-catch en el feature (opción 1 - recomendado para errores conocidos)
2. GlobalErrorBoundary (opción 2 - último recurso)

Esto fuerza explicititud: cada feature sabe qué errores espera manejar.

### 5.3 SectionErrorBoundary Reutilizable

En lugar de crear un Error Boundary para cada sección, se crea uno genérico reutilizable con prop `section` para identificar dónde falló.

---

## 6. Checklist de Implementación

Al crear una nueva feature con API calls:

- [ ] Importar `ApiErrorClass` desde `lib/errors/apiError`
- [ ] Usar `busLocationService` (o similar) sin try-catch internos
- [ ] En el componente feature, envolver el call asincrónico en try-catch
- [ ] Guardar error en estado local (ej: `setApiError`)
- [ ] Renderizar error localmente antes de renderizar contenido
- [ ] GlobalErrorBoundary capturará cualquier error no atrapado

---

## 7. Diferencias Entre management-app y passengers-app

Ambas implementan la misma estrategia de error handling, con adaptaciones según el toolchain:

| Aspecto | management-app (Next.js) | passengers-app (Vite) |
|--------|--------------------------|----------------------|
| Env API URL | `process.env.NEXT_PUBLIC_API_URL` | `import.meta.env.VITE_API_URL` |
| Type Imports | `import { ApiErrorResponse }` | `import { type ApiErrorResponse }` |
| Root Boundary | `src/app/layout.tsx` | `src/App.tsx` |
| Features | `src/features/{feature}/` | `src/features/{feature}/` |
| Structure | Usa `app/` (Next.js App Router) | Usa `src/` (Vite standard) |

El patrón de error handling es **idéntico en ambas**, asegurando consistencia arquitectónica.
