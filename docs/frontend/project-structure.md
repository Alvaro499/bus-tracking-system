# Estructura de Proyectos Frontend

## 1. Visión General

La arquitectura del frontend replica la estructura del backend, utilizando Clean Architecture con capas separadas: Domain, Infrastructure y Features. Esta aproximación asegura:
- **Escalabilidad:** Nuevas features sin afectar código existente
- **Testabilidad:** Cada capa es independiente y testeable
- **Mantenibilidad:** Responsabilidades claramente definidas

---

## 2. Estructura Compartida

### 2.1 management-app (Next.js 16.2.4)

```
management-app/
├── src/
│   ├── app/                           # Next.js App Router
│   │   └── layout.tsx                 # Root layout con GlobalErrorBoundary
│   │
│   ├── domain/
│   │   ├── interfaces/                # Contratos de entidades
│   │   ├── models/                    # Tipos y DTOs
│   │   └── mappers/                   # Conversión entre capas
│   │
│   ├── infrastructure/
│   │   └── services/
│   │       └── busLocationService.ts  # API calls a backend
│   │
│   ├── features/                      # Cada feature es autocontenida
│   │   ├── driver/
│   │   │   ├── driver.container.tsx   # Componente inteligente
│   │   │   └── components/            # Componentes presentacionales
│   │   ├── admin/
│   │   │   └── ...
│   │   └── companies/
│   │       └── ...
│   │
│   ├── lib/                           # Utilidades compartidas
│   │   ├── errors/
│   │   │   ├── apiError.ts           # Clase ApiErrorClass
│   │   │   └── GlobalErrorBoundary.tsx
│   │   ├── httpClient.ts             # Cliente HTTP centralizado
│   │   ├── types/                     # Tipos globales
│   │   └── utils.ts                  # Utilidades generales
│   │
│   ├── shared/
│   │   ├── components/                # Componentes reutilizables (NO shadcn)
│   │   ├── hooks/                     # Hooks personalizados
│   │   └── utils/                     # Funciones de utilidad
│   │
│   ├── components/                    # Componentes shadcn/ui
│   │   ├── ui/                        # Componentes primarios
│   │   └── ...
│   │
│   └── eslint.config.mjs
│
├── package.json
├── tsconfig.json
└── next.config.ts
```

### 2.2 passengers-app (Vite 8.0.10, React 19.2.5)

```
passengers-app/
├── src/
│   ├── domain/
│   │   ├── interfaces/                # Contratos de entidades
│   │   ├── models/                    # Tipos y DTOs
│   │   └── mappers/                   # Conversión entre capas
│   │
│   ├── infrastructure/
│   │   └── services/
│   │       └── busLocationService.ts  # API calls a backend
│   │
│   ├── features/                      # Cada feature es autocontenida
│   │   ├── Home/
│   │   └── map/
│   │       ├── components/
│   │       ├── map.container.tsx      # Componente inteligente
│   │       └── ...
│   │
│   ├── lib/                           # Utilidades compartidas
│   │   ├── errors/
│   │   │   ├── apiError.ts           # Clase ApiErrorClass
│   │   │   └── GlobalErrorBoundary.tsx
│   │   ├── httpClient.ts             # Cliente HTTP centralizado
│   │   └── utils.ts                  # Utilidades generales
│   │
│   ├── shared/
│   │   ├── hooks/                     # Hooks personalizados
│   │   └── ...
│   │
│   ├── components/                    # Componentes reutilizables
│   │   ├── BusMap.tsx
│   │   └── ...
│   │
│   ├── assets/                        # Recursos estáticos
│   ├── styles/                        # CSS global
│   ├── App.tsx                        # Root component con GlobalErrorBoundary
│   ├── App.css
│   ├── index.css
│   └── main.tsx
│
├── public/
├── index.html
├── package.json
├── tsconfig.json
└── vite.config.ts
```

---

## 3. Explicación de Capas

### 3.1 Domain (Lógica de Negocio)

**Propósito:** Define las reglas de negocio independientes del framework.

**interfaces/:**
```typescript
// domain/interfaces/IBusLocation.ts
export interface IBusLocation {
  getBusLocation(busId: string): Promise<BusLocation>;
  updateBusLocation(busId: string, lat: number, lng: number): Promise<void>;
}
```

**models/:**
```typescript
// domain/models/BusLocation.ts
export interface BusLocation {
  busId: string;
  lat: number;
  lng: number;
  timestamp: number;
}
```

**mappers/:**
Convierte DTOs del backend a modelos del dominio y viceversa.

---

### 3.2 Infrastructure (Acceso a Recursos Externos)

**Propósito:** Implementa las interfaces del domain usando APIs, bases de datos, etc.

**services/busLocationService.ts:**
```typescript
// Sin try-catch aquí
export const busLocationService = {
  async getBusLocation(busId: string): Promise<BusLocation> {
    return httpClient.get<BusLocation>(`/tracking/buses/${busId}/location`);
  },

  async updateBusLocation(busId: string, lat: number, lng: number): Promise<void> {
    await httpClient.post(`/tracking/buses/${busId}/location`, { lat, lng });
  }
};
```

**Por qué no hay try-catch:**
- El `httpClient` ya lanza `ApiErrorClass`
- El servicio es agnóstico al handling; solo redirige errores hacia arriba
- El try-catch se hace en el **feature** que usa el servicio

---

### 3.3 Features (Lógica de UI y Estado)

**Propósito:** Componentes inteligentes (container) que orquestan servicios y estado.

**map/map.container.tsx (passengers-app):**
```typescript
export function MapContainer() {
  const [busLocation, setBusLocation] = useState<BusLocation | null>(null);
  const [apiError, setApiError] = useState<ApiErrorClass | null>(null);

  usePolling(async () => {
    try {
      const location = await busLocationService.getBusLocation(BUS_ID);
      setBusLocation(location);
      setApiError(null);
    } catch (err) {
      if (err instanceof ApiErrorClass) {
        setApiError(err);  // Try-catch aquí
      }
    }
  }, POLLING_INTERVAL);

  if (apiError) return <p>{apiError.userMessage}</p>;
  return <BusMap location={busLocation} />;
}
```

**Por qué el try-catch está aquí:**
- Los errores asincronos ocurren en callbacks (`usePolling`, `onClick`, etc)
- React Error Boundaries NO capturan errores en callbacks
- Se captura explícitamente para cada feature que hace API calls

---

### 3.4 lib (Utilidades Compartidas)

#### 3.4.1 errors/

**apiError.ts:**
```typescript
export interface ApiErrorResponse {
  code: string;       // Contrato inmutable con backend
  message: string;
  timestamp: number;
}

export class ApiErrorClass extends Error {
  constructor(
    public readonly code: string,
    public readonly userMessage: string,
    public readonly status: number,
    public readonly timestamp: number
  ) {
    super(userMessage);
    this.name = 'ApiErrorClass';
  }
}
```

**GlobalErrorBoundary.tsx:**
- React Error Boundary que captura errores de renderizado
- Último recurso; solo para bugs inesperados
- Decide qué renderizar basado en `error.code`

#### 3.4.2 httpClient.ts

**Propósito:** Cliente HTTP centralizado con interceptación de errores.

```typescript
// Cuando la respuesta es 4xx o 5xx:
if (!response.ok) {
  const body = await response.json();
  throw new ApiErrorClass(
    body.code,           // Del backend
    body.message,        // Del backend
    response.status,
    body.timestamp
  );
}
```

**Ventajas:**
- Errores centralizados
- Consistencia en toda la app
- Fácil de loguear, monitorear, etc

#### 3.4.3 types/ (solo management-app)

Definiciones de tipos Next.js específicas (Server Actions, etc).

---

### 3.5 shared (Reutilizable en Toda la App)

#### 3.5.1 components/

**management-app:** Componentes de UI personalizados (NOT shadcn).
**passengers-app:** Componentes reutilizables no específicos a una feature.

Diferencia con `components/` (en management-app solo):
- `src/components/` = shadcn/ui components
- `src/shared/components/` = Custom components

#### 3.5.2 hooks/

Hooks reutilizables:
```typescript
// shared/hooks/usePolling.ts
export function usePolling(fn: () => Promise<void>, interval: number) {
  useEffect(() => {
    const timer = setInterval(fn, interval);
    return () => clearInterval(timer);
  }, [fn, interval]);
}
```

#### 3.5.3 utils/

Funciones de utilidad:
```typescript
// shared/utils/formatters.ts
export function formatCoordinates(lat: number, lng: number): string {
  return `${lat.toFixed(4)}, ${lng.toFixed(4)}`;
}
```

---

## 4. Diferencias Entre management-app y passengers-app

### 4.1 Estructura Raíz

| Aspecto | management-app | passengers-app |
|--------|-----------------|-----------------|
| Entry Point | `src/app/layout.tsx` | `src/App.tsx` |
| Router | Next.js App Router | React Router (manual) |
| Root Directory | `app/` | `src/` |
| Config | `next.config.ts` | `vite.config.ts` |

**Razón:** Next.js requiere `app/` para el App Router; Vite usa estructura estándar.

### 4.2 Environment Variables

| Aspecto | management-app | passengers-app |
|--------|-----------------|-----------------|
| Acceso | `process.env.NEXT_PUBLIC_*` | `import.meta.env.VITE_*` |
| Ejemplo | `process.env.NEXT_PUBLIC_API_URL` | `import.meta.env.VITE_API_URL` |

**Razón:** Cada bundler expone variables de forma diferente.

### 4.3 Type Imports

| Aspecto | management-app | passengers-app |
|--------|-----------------|-----------------|
| TypeScript | 5.x | ~6.0 |
| Imports | `import { Type }` | `import { type Type }` |

**Razón:** TypeScript 6.0 requiere `type`-only imports con `verbatimModuleSyntax: true`.

### 4.4 Components

| Aspecto | management-app | passengers-app |
|--------|-----------------|-----------------|
| shadcn/ui | `src/components/ui/*` | Instalado, no usado aún |
| Custom | `src/shared/components/*` | `src/components/*` |

**Razón:** management-app separa shadcn de custom; passengers-app es más simple aún.

---

## 5. Patrón de Error Handling

Ambas apps implementan el mismo patrón, descrito en [error-handling-guide.md](./error-handling-guide.md):

1. **httpClient** lanza `ApiErrorClass`
2. **Service** propaga sin try-catch
3. **Feature** captura en try-catch (try-catch en callbacks asincronos)
4. **GlobalErrorBoundary** captura lo que nadie más atrapó

---

## 6. Decisiones de Arquitectura

### 6.1 Por qué Clean Architecture

Replica el backend (Spring Boot con capas):
- **Domain** = DTOs y interfaces
- **Infrastructure** = Servicios que implementan las interfaces
- **Features** = Componentes que orquestan servicios

Esto asegura:
- Consistencia entre frontend y backend
- Fácil de escalar
- Nuevas features sin modificar existentes

### 6.2 Por qué httpClient Centralizado

- Un único punto de error handling
- Fácil de agregar logging, autenticación, retry logic
- Sincronización con backend de forma consistente

### 6.3 Por qué Try-Catch en Features

- Explicititud: cada feature sabe qué errores maneja
- Errores asincronos no son capturados por Error Boundaries
- Permite manejar errores con UI localizada

---

## 7. Adiciones Futuras

### 7.1 Cuando agregar SectionErrorBoundary

Usar cuando se quiera aislar fallos en secciones grandes:
```typescript
<SectionErrorBoundary section="Panel de Ubicación">
  <DriverContainer />
</SectionErrorBoundary>
```

Ver [error-handling-guide.md - Section 3.3](./error-handling-guide.md#33-section-error-boundary-ejemplo-driver-panel).

### 7.2 Cuando agregar Más Features

1. Crear carpeta `src/features/{nombre}/`
2. Agregar `{nombre}.container.tsx` con try-catch
3. Agregar `components/` si necesita subcomponentes
4. Seguir el patrón de busLocationService en infrastructure

---

## 8. Checklist: Nueva Feature

- [ ] Crear `src/features/{nombre}/`
- [ ] Crear servicio en `src/infrastructure/services/{nombre}Service.ts`
- [ ] Crear container en `src/features/{nombre}/{nombre}.container.tsx`
- [ ] Agregar try-catch para API calls
- [ ] Importar `ApiErrorClass` para type checking
- [ ] Guardar errores en estado local
- [ ] Renderizar error localmente antes de contenido

---

## 9. Referencias

- [Error Handling Guide](./error-handling-guide.md)
- [Code Style - Backend](./guides/code-style-backend.md)
- [Code Style - Frontend](./guides/code-style-frontend.md)


