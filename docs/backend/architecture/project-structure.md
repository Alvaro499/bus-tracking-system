
```
src/main/java/com/bustracking/
│
├── BackendApplication.java
│
├── tracking/                             ← MÓDULO: Tracking
│   ├── domain/
│   │   ├── model/
│   │   │   ├── BusLocation.java
│   │   │   └── Placeholder.java
│   │   ├── repository/
│   │   │   └── BusLocationRepository.java
│   │   └── contract/
│   │       └── BusExistsById.java
│   │
│   ├── application/
│   │   ├── usecase/
│   │   │   ├── GetBusLocationUseCase.java
│   │   │   └── UpdateBusLocationUseCase.java
│   │   ├── service/
│   │   │   └── Placeholder.java
│   │   └── dto/
│   │       └── Placeholder.java
│   │
│   └── infrastructure/
│       ├── persistence/
│       │   ├── entity/
│       │   │   ├── BusLocationJpa.java
│       │   │   └── ...
│       │   └── repository/
│       │       ├── BusLocationJpaRepository.java
│       │       └── BusLocationRepositoryImpl.java
│       └── web/
│           ├── controller/
│           │   └── BusLocationController.java
│           ├── dto/
│           │   ├── request/
│           │   │   └── UpdateBusLocationRequest.java
│           │   └── response/
│           │       ├── BusLocationResponse.java
│           │       └── Placeholder.java
│           └── error/
│               └── TrackingExceptionHandler.java
│
├── companies/                            ← MÓDULO: Companies
│   ├── domain/
│   │   ├── model/
│   │   │   ├── Bus.java
│   │   │   ├── Company.java
│   │   │   ├── CompanyUser.java
│   │   │   ├── CompanyRequest.java
│   │   │   ├── Route.java
│   │   │   ├── Stop.java
│   │   │   ├── RouteStop.java
│   │   │   ├── Schedule.java
│   │   │   ├── Trip.java
│   │   │   └── RouteStopFare.java
│   │   ├── valueobjects/
│   │   │   ├── Plate.java
│   │   │   └── InternalNumber.java
│   │   ├── enums/
│   │   │   ├── BusStatus.java
│   │   │   ├── TripStatus.java
│   │   │   ├── Status.java
│   │   │   ├── Role.java
│   │   │   └── RequestStatus.java
│   │   ├── repository/
│   │   │   └── BusRepository.java
│   │   └── service/
│   │
│   ├── application/
│   │   ├── usecase/
│   │   │   └── Placeholder.java
│   │   ├── service/
│   │   │   └── Placeholder.java
│   │   └── dto/
│   │       └── Placeholder.java
│   │
│   └── infrastructure/
│       ├── persistence/
│       │   ├── entity/
│       │   │   ├── BusJpa.java
│       │   │   ├── CompanyJpa.java
│       │   │   ├── CompanyUserJpa.java
│       │   │   ├── CompanyRequestJpa.java
│       │   │   ├── RouteJpa.java
│       │   │   ├── StopJpa.java
│       │   │   ├── RouteStopJpa.java
│       │   │   ├── ScheduleJpa.java
│       │   │   ├── TripJpa.java
│       │   │   └── RouteStopFareJpa.java
│       │   └── repository/
│       │       ├── BusJpaRepository.java
│       │       └── BusRepositoryImpl.java
│       ├── config/
│       │   └── TrackingDelegatesConfig.java
│       ├── service/
│       └── web/
│           ├── controller/
│           │   └── Placeholder.java
│           └── dto/
│               ├── request/
│               │   └── Placeholder.java
│               └── response/
│                   └── Placeholder.java
│
├── admin/                                ← MÓDULO: Admin
│   ├── domain/
│   │   ├── model/
│   │   │   ├── User.java
│   │   │   └── AuditLog.java
│   │   ├── enums/
│   │   │   ├── GlobalRole.java
│   │   │   └── Action.java
│   │   ├── exception/
│   │   │   └── AdminNotActiveException.java
│   │   └── repository/
│   │       └── Placeholder.java
│   │
│   ├── application/
│   │   ├── usecase/
│   │   │   └── Placeholder.java
│   │   ├── service/
│   │   │   └── Placeholder.java
│   │   └── dto/
│   │       └── Placeholder.java
│   │
│   └── infrastructure/
│       ├── persistence/
│       │   ├── entity/
│       │   │   ├── UserJpa.java
│       │   │   └── AuditLogJpa.java
│       │   └── repository/
│       └── web/
│           ├── controller/
│           │   ├── EmpresaController.java
│           │   └── Placeholder.java
│           ├── dto/
│           │   ├── request/
│           │   │   └── Placeholder.java
│           │   └── response/
│           │       └── Placeholder.java
│           └── error/
│               └── AdminExceptionHandler.java
│
└── shared/                               ← CÓDIGO COMPARTIDO
    ├── valueobjects/
    │   ├── GpsCoordinate.java
    │   └── Placeholder.java
    ├── exception/
    │   ├── ApplicationException.java
    │   ├── BusinessRuleException.java
    │   ├── ValidationException.java
    │   ├── NotFoundException.java
    │   ├── ExternalServiceException.java
    │   ├── ErrorCode.java
    │   └── Placeholder.java
    ├── rules/
    │   └── Placeholder.java
    ├── config/
    │   └── Placeholder.java
    └── infrastructure/
        ├── error/
        │   ├── GlobalExceptionHandler.java
        │   └── ErrorResponse.java
        └── security/
            └── SecurityConfig.java

════════════════════════════════════════════════════════════════════

src/test/java/com/bustracking/
│
├── tracking/
│   ├── unit/
│   │   ├── domain/
│   │   │   └── BusLocationTest.java
│   │   └── usecase/
│   │       ├── GetBusLocationUseCaseTest.java
│   │       └── UpdateBusLocationUseCaseTest.java
│   ├── integration/
│   │   ├── controller/
│   │   │   └── BusLocationControllerTest.java
│   │   └── repository/
│   │       └── BusLocationRepositoryTest.java
│   └── e2e/
│       └── BusLocationE2ETest.java
│
├── companies/
│   ├── unit/
│   │   └── domain/
│   │       ├── BusTest.java
│   │       └── valueobjects/
│   │           ├── PlateTest.java
│   │           └── InternalNumberTest.java
│   ├── integration/
│   └── e2e/
│
├── admin/
│   ├── unit/
│   ├── integration/
│   └── e2e/
│
└── shared/
    ├── unit/
    │   └── GpsCoordinateTest.java
    └── testinfrastructure/
        ├── ControllerIntegrationTest.java
        ├── E2EIntegrationTest.java
        └── RepositoryIntegrationTest.java
```



Usuario	¿Cómo accede?	¿Por qué?
Cliente (Persona)	www.mibanco.com (Internet Pública)	Necesita estar expuesto al mundo.
Empleado (Cajero/Admin)	intranet.mibanco.local o backoffice.mibanco.com (Red Privada / VPN)	NO debe estar expuesto a Internet. Solo accesible desde la red corporativa o por VPN