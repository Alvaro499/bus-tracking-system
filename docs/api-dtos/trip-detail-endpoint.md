
# Implement endpoint: get trip details

**Endpoint:** `GET /trips/{tripId}/detail` – Data Mapping

---

## Description

This endpoint provides the driver with all the information needed to display the active trip view on the frontend.  
Because the `tracking` module must respect clean architecture boundaries, it cannot directly access `companies` tables. Instead, a contract is defined in tracking and implemented by a delegate in companies. The delegate runs two JPQL queries and maps the results into tracking domain views, which the controller then converts into response DTOs and serializes to JSON.

---

## Example JSON response

```json
{
  "trip": {
    "id": "b70e8400-e29b-41d4-a716-446655440001",
    "routeName": "Cartago-Orosi",
    "origin": "Cartago",
    "destination": "Orosi",
    "departureTime": "08:00:00",
    "status": "IN_PROGRESS"
  },
  "stops": [
    {
      "routeStop": {
        "id": "960e8400-e29b-41d4-a716-446655440001",
        "stopId": "860e8400-e29b-41d4-a716-446655440001",
        "orderIndex": 1,
        "estimatedTimeOffset": 0
      },
      "stop": {
        "id": "860e8400-e29b-41d4-a716-446655440001",
        "name": "Cartago",
        "latitude": 9.8612,
        "longitude": -83.9180,
        "reference": "Terminal Central Cartago"
      },
      "completedAt": "2026-06-21T08:05:00"
    }
  ]
}
```

---

## General Data Flow

```plaintext
Tables: trip, schedule, route, route_stop, stop, trip_stop
       ↓
TripJpaRepository (JPQL queries)
       ↓
TripScheduleProjection  +  TripStopDetailProjection
       ↓
TripRepository (domain interface in companies)
       ↓
TripDetailDelegate (implements GetTripDetail contract)
       ↓
GetTripDetail (contract in tracking domain)
       ↓
GetTripDetailUseCase
       ↓
DriverTripQueryController
       ↓
TripDetailResponse (JSON)
```

---

## DTOs and objects involved

### 1. Frontend

---

#### `Trip`

| | |
|---|---|
| **What it is** | TypeScript interface representing a trip as shown in the driver's trip list and trip detail screens. |
| **Where it comes from** | Defined in the frontend domain models to match the backend's `TripResponse`. |
| **Who uses it** | `TripDetail`, `TripCard` component, `trip-list.container.tsx`. |

| Property | Value |
|----------|-------|
| Package | `src/domain/models/Trip.ts` (frontend) |
| Type | TypeScript interface |
| Status | Existing |
| Justification | Provides type safety for trip data received from the API. |
| Communicates with | `TripResponse` (backend JSON) |

**Structure:**

```typescript
export interface Trip {
  id: string;
  routeName: string;
  origin: string;
  destination: string;
  departureTime: string; // "HH:mm:ss"
  status: string;
}
```

---

#### `RouteStop`

| | |
|---|---|
| **What it is** | TypeScript interface for the relationship between a route and a stop. |
| **Where it comes from** | Defined in the frontend domain models. |
| **Who uses it** | `TripDetail` (inside the stops array). |

| Property | Value |
|----------|-------|
| Package | `src/domain/models/RouteStop.ts` |
| Type | TypeScript interface |
| Status | Existing |
| Justification | Provides order and timing data for a stop within a route. |
| Communicates with | `RouteStopResponse` (backend JSON) |

**Structure:**

```typescript
export interface RouteStop {
  id: string;
  stopId: string;
  orderIndex: number;
  estimatedTimeOffset: number;
}
```

---

#### `Stop`

| | |
|---|---|
| **What it is** | TypeScript interface representing a physical bus stop. |
| **Where it comes from** | Defined in the frontend domain models. |
| **Who uses it** | `TripDetail` (inside the stops array). |

| Property | Value |
|----------|-------|
| Package | `src/domain/models/Stop.ts` |
| Type | TypeScript interface |
| Status | Existing |
| Justification | Holds geographic and descriptive data of a stop. |
| Communicates with | `StopResponse` (backend JSON) |

**Structure:**

```typescript
export interface Stop {
  id: string;
  name: string;
  latitude: number;
  longitude: number;
  reference: string;
}
```

---

#### `TripDetail`

| | |
|---|---|
| **What it is** | TypeScript interface representing the full trip detail view consumed by the driver screen. |
| **Where it comes from** | Defined in the frontend codebase to mirror the JSON response. |
| **Who uses it** | `trip-detail.container.tsx` and its child components. |

| Property | Value |
|----------|-------|
| Package | `src/domain/models/TripDetail.ts` (frontend) |
| Type | TypeScript interface |
| Status | Existing |
| Justification | Provides type safety and a clear contract between the backend API and the UI. |
| Communicates with | `TripDetailResponse` (backend JSON) |

**Structure:**

```typescript
export interface TripDetail {
  trip: Trip;
  stops: Array<{
    routeStop: RouteStop;
    stop: Stop;
    completedAt: string | null;
  }>;
}
```

---

### 2. Backend (tracking module)

---

#### `TripDetailResponse`

| | |
|---|---|
| **What it is** | Top-level response DTO for the endpoint. |
| **Where it comes from** | Built manually in `DriverTripQueryController` from a `TripDetailView`. |
| **Who uses it** | Serialized by Spring to the JSON response. |

| Property | Value |
|----------|-------|
| Package | `com.bustracking.tracking.infrastructure.web.dto.response` |
| Type | `record` |
| Status | New |
| Justification | Adapts the domain model to the exact JSON format expected by the frontend. |
| Communicates with | `TripDetailView` -> JSON |

**Structure:**

```java
public record TripDetailResponse(
    TripResponse trip,
    List<TripStopDetailResponse> stops
) {}
```

**JSON equivalent:**

```json
{
  "trip": { ... },
  "stops": [ ... ]
}
```

---

#### `TripStopDetailResponse`

| | |
|---|---|
| **What it is** | A single stop inside the `stops` array of the response. |
| **Where it comes from** | Mapped from `TripStopDetailView`. |
| **Who uses it** | `TripDetailResponse`. |

| Property | Value |
|----------|-------|
| Package | `com.bustracking.tracking.infrastructure.web.dto.response` |
| Type | `record` |
| Status | New |
| Justification | Groups route-stop and stop data together, including the completion timestamp. |
| Communicates with | `TripStopDetailView` -> `TripDetailResponse` |

**Structure:**

```java
public record TripStopDetailResponse(
    RouteStopResponse routeStop,
    StopResponse stop,
    LocalDateTime completedAt
) {}
```

**JSON equivalent:**

```json
{
  "routeStop": { ... },
  "stop": { ... },
  "completedAt": "2026-06-21T08:05:00"
}
```

---

#### `RouteStopResponse`

| | |
|---|---|
| **What it is** | Data of a stop within a route (order, time offset). |
| **Where it comes from** | Mapped from `RouteStopView`. |
| **Who uses it** | `TripStopDetailResponse`. |

| Property | Value |
|----------|-------|
| Package | `com.bustracking.tracking.infrastructure.web.dto.response` |
| Type | `record` |
| Status | New |
| Justification | Provides the route-specific stop data without exposing internal identifiers. |
| Communicates with | `RouteStopView` -> `TripStopDetailResponse` |

**Structure:**

```java
public record RouteStopResponse(
    UUID id,
    UUID stopId,
    Integer orderIndex,
    Integer estimatedTimeOffset
) {}
```

**JSON equivalent:**

```json
{
  "id": "960e8400-...",
  "stopId": "860e8400-...",
  "orderIndex": 1,
  "estimatedTimeOffset": 0
}
```

---

#### `StopResponse`

| | |
|---|---|
| **What it is** | Geographic information of a bus stop. |
| **Where it comes from** | Mapped from `StopView`. |
| **Who uses it** | `TripStopDetailResponse`. |

| Property | Value |
|----------|-------|
| Package | `com.bustracking.tracking.infrastructure.web.dto.response` |
| Type | `record` |
| Status | New |
| Justification | Delivers stop coordinates and reference text without exposing internal domain logic. |
| Communicates with | `StopView` -> `TripStopDetailResponse` |

**Structure:**

```java
public record StopResponse(
    UUID id,
    String name,
    BigDecimal latitude,
    BigDecimal longitude,
    String reference
) {}
```

**JSON equivalent:**

```json
{
  "id": "860e8400-...",
  "name": "Cartago",
  "latitude": 9.8612,
  "longitude": -83.9180,
  "reference": "Terminal Central Cartago"
}
```

---

#### `TripDetailView`

| | |
|---|---|
| **What it is** | Domain object aggregating the basic trip info and its list of stops. |
| **Where it comes from** | Built by `TripDetailDelegate` from two projections. |
| **Who uses it** | `GetTripDetailUseCase` and then the controller. |

| Property | Value |
|----------|-------|
| Package | `com.bustracking.tracking.domain.model` |
| Type | `class` |
| Status | New |
| Justification | Represents the trip detail inside the tracking domain, decoupled from persistence and HTTP concerns. |
| Communicates with | `TripDetailDelegate` (producer) -> `GetTripDetailUseCase` -> controller |

**Structure:**

```java
public class TripDetailView {
    private final TripView trip;
    private final List<TripStopDetailView> stops;
}
```

---

#### `TripStopDetailView`

| | |
|---|---|
| **What it is** | Domain view for one stop within a trip detail. |
| **Where it comes from** | Mapped from `TripStopDetailProjection`. |
| **Who uses it** | `TripDetailView`. |

| Property | Value |
|----------|-------|
| Package | `com.bustracking.tracking.domain.model` |
| Type | `class` |
| Status | New |
| Justification | Holds route-stop info, stop info, and completion status inside the tracking domain. |
| Communicates with | `TripDetailDelegate` -> `TripDetailView` |

**Structure:**

```java
public class TripStopDetailView {
    private final RouteStopView routeStop;
    private final StopView stop;
    private final LocalDateTime completedAt;
}
```

---

#### `RouteStopView`

| | |
|---|---|
| **What it is** | Domain view for the relationship between a route and a stop. |
| **Where it comes from** | Mapped from `TripStopDetailProjection`. |
| **Who uses it** | `TripStopDetailView`. |

| Property | Value |
|----------|-------|
| Package | `com.bustracking.tracking.domain.model` |
| Type | `class` |
| Status | New |
| Justification | Carries route-stop order and timing data without persistence details. |
| Communicates with | `TripDetailDelegate` -> `TripStopDetailView` |

**Structure:**

```java
public class RouteStopView {
    private final UUID id;
    private final UUID stopId;
    private final int orderIndex;
    private final int estimatedTimeOffset;
}
```

---

#### `StopView`

| | |
|---|---|
| **What it is** | Domain view for a geographic stop. |
| **Where it comes from** | Mapped from `TripStopDetailProjection`. |
| **Who uses it** | `TripStopDetailView`. |

| Property | Value |
|----------|-------|
| Package | `com.bustracking.tracking.domain.model` |
| Type | `class` |
| Status | New |
| Justification | Provides stop location data inside the tracking domain. |
| Communicates with | `TripDetailDelegate` -> `TripStopDetailView` |

**Structure:**

```java
public class StopView {
    private final UUID id;
    private final String name;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final String reference;
}
```

---

### 3. Backend (companies module)

---

#### `TripScheduleProjection`

| | |
|---|---|
| **What it is** | Read-only projection of a trip with its route and schedule data. |
| **Where it comes from** | JPQL query in `TripJpaRepository` joining `TripJpa`, `ScheduleJpa`, `RouteJpa`. |
| **Who uses it** | `TripDetailDelegate` to build the `TripView`. |

| Property | Value |
|----------|-------|
| Package | `com.bustracking.companies.domain.dto` |
| Type | `record` |
| Status | Existing |
| Justification | Avoids loading full JPA entities; only the fields needed for the trip header. |
| Communicates with | `TripRepository` -> `TripDetailDelegate` |

**Structure:**

```java
public record TripScheduleProjection(
    UUID id,
    String routeName,
    String origin,
    String destination,
    LocalTime departureTime,
    TripStatus status
) {}
```

---

#### `TripStopDetailProjection`

| | |
|---|---|
| **What it is** | Projection that combines route-stop, stop, and completion timestamp for a trip. |
| **Where it comes from** | JPQL query in `TripJpaRepository` with multiple JOINs. |
| **Who uses it** | `TripDetailDelegate` to build `TripStopDetailView` objects. |

| Property | Value |
|----------|-------|
| Package | `com.bustracking.companies.domain.dto` |
| Type | `record` |
| Status | New |
| Justification | Fetches all necessary stop data in a single efficient query, avoiding N+1 problems. |
| Communicates with | `TripRepository` -> `TripDetailDelegate` |

**Structure:**

```java
public record TripStopDetailProjection(
    UUID routeStopId,
    UUID stopId,
    String stopName,
    BigDecimal stopLat,
    BigDecimal stopLng,
    String stopReference,
    Integer orderIndex,
    Integer estimatedTimeOffset,
    LocalDateTime completedAt
) {}
```
