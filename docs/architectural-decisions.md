

## Diseño Basado en POO


---

## Estilo Arquitectónico (Arquitectura Externa)

Se optó por un monolito modular debido a que el sistema es de tamaño pequeño-mediano, es desarrollado por un equipo reducido y se encuentra en etapa de MVP. Internamente el código estará organizado en módulos bien separados (visualización, gestión de empresa, tracking, administración), lo que permitiría extraer algún módulo como servicio independiente en el futuro si el sistema lo requiere, sin necesidad de reescribir desde cero.

Se descartaron los microservicios dado que no se identifican componentes que necesiten escalar de forma independiente, ni existe un equipo suficientemente grande que justifique esa separación. A futuro, si se incorporan funcionalidades como notificaciones por correo o un módulo de tipo Uber, se evaluaría extraerlos como servicios independientes en ese momento.

La parte de geolocalización, aunque tiene lógica más dinámica que el resto del sistema, no justifica una arquitectura orientada a eventos completa. Se manejará dentro del mismo monolito mediante actualización periódica de coordenadas desde los dispositivos de los buses.


```
src/
├── controllers/
│   ├── BusController.java
│   ├── EmpresaController.java
│   ├── AdminController.java
│   └── RutaController.java
├── services/
│   ├── BusService.java
│   ├── EmpresaService.java
│   └── RutaService.java
└── repositories/
    ├── BusRepository.java
    └── EmpresaRepository.java
```

```
Monolito Modular

src/
├── tracking/          ← todo lo del GPS y tiempo real
│   ├── controller/
│   ├── service/
│   └── repository/
│
├── empresas/          ← registro, gestión de empresa
│   ├── controller/
│   ├── service/
│   └── repository/
│
├── visualizacion/     ← lo que ve el usuario público
│   ├── controller/
│   └── service/
│
└── admin/             ← aprobaciones, gestión global
    ├── controller/
    └── service/
```


### Arquitectura (Arquitectura Interna)

- Clean Architecture

Todo esto aplicado dentro de un monolito modular, donde cada módulo (tracking, empresas, visualización, admin) respeta internamente esta misma estructura de capas.

---
## ¿Cómo se comunican las partes? 


La comunicación del sistema se divide en dos tipos según la naturaleza de cada módulo.

Para los módulos de gestión de empresas, buses, rutas, horarios y administración global se utilizará REST, ya que estas operaciones no requieren inmediatez para crear una ruta o aprobar una empresa son acciones puntuales donde una respuesta en milisegundos no es crítica.

Para el módulo de tracking, donde los dispositivos de los buses envían coordenadas y el mapa del usuario las refleja, se utilizará polling desde el frontend consultando el servidor cada N segundos. Esto es suficiente para mostrar la ubicación de un bus en movimiento sin interrumpir la interfaz del usuario ni requerir infraestructura adicional. WebSockets se considera como una evolución futura si la latencia del polling resulta insuficiente.



Polling                         WebSockets
─────────────────────           ─────────────────────
Frontend pregunta cada          Conexión abierta permanente
N segundos → "¿hay datos?"      Server empuja datos cuando llegan

Más simple de implementar       Más complejo
Funciona con REST normal        Requiere infraestructura adicional
Latencia = intervalo elegido    Latencia mínima
Suficiente para buses           Necesario para chat, trading, etc.

---


## ¿Dónde vive cada pieza? 

¿Dónde corre?           ¿Qué es?
─────────────────────────────────────────────
En el navegador         → App web (el mapa, lo que ve el usuario)
En un servidor          → Backend (la lógica, la base de datos)
En el bus físico        → App del bus (envía coordenadas GPS)


#### Bus App


```
Opción A: PWA (página web que funciona en celular)
          → No hay que publicar en tienda
          → Cualquier celular con internet la usa
          → Más simple de desarrollar

Opción B: App móvil nativa (Android/iOS)
          → Hay que publicarla en tienda
          → Más compleja de desarrollar
          → Innecesaria para este proyecto
```

Para el caso, la PWA es suficiente y es lo que tiene más sentido.

## ¿Qué base de datos? 


#### Relacional y No Relacioanl

- El sistema actual no es uno que estén contstantemente escribirendo datos o guardando, está más enfocado en lo que es lparte de visualizar informacion, tanto de la parte de administracion del sistema, gestor de la empresa y los usuarios.

- El unico modulo que podria estar generando mutiples valores cada N segundos, es la app del bus, ya que debe estar mostrando su localizacion GPS para obtener esa informacion y distribuirla  a los usuarios, los cuales pueden ser muchos.

- Los datos son estrcuturados o por lo menos lo la parte de gestión de la empresa con los buses, horarios, rutas, paradas, etc
- A diferencia de los puntos de ubicación, que son pocos y simples

Resumiendo:

- **NoSQL** es más adecuado para **concurrencia** y **lecturas/escrituras rápidas**, especialmente cuando los datos son **no estructurados** o se modifican con frecuencia.
    
- **SQL** es ideal cuando se requieren **operaciones transaccionales** y una **estructura fija de datos**.

- Por lo tanto quizá una combinacion en lo que respecta a SQL para los datos estrcutruados como gestion de transporte y empresas. NoSQL para las coordenadas que estarán sconstantmente sobreescribinend y  la parte de los usuarios visualizando (lecturas) de forma rápida

#### Decisión

###### Base de datos: PostgreSQL con evolución futura a Polyglot Persistence

El sistema está orientado principalmente a la visualización de información, con operaciones de escritura moderadas y predecibles. La mayoría de los datos son estructurados y con relaciones claras entre sí — empresas, buses, rutas, paradas y horarios — lo que hace que un modelo relacional sea la opción natural y suficiente para el MVP.

**PostgreSQL** cubre todas las necesidades actuales del sistema. El único módulo que genera escrituras frecuentes es la app del bus, pero el volumen real no justifica una base de datos adicional en esta etapa. Considerando un escenario realista para Costa Rica de aproximadamente 250 buses transmitiendo simultáneamente cada 10 segundos, se obtienen alrededor de 1,500 escrituras por minuto, lo cual PostgreSQL maneja sin problema. Además, no se guardará historial de coordenadas, solo se sobreescribe la última ubicación conocida de cada bus, manteniendo siempre una sola fila por unidad.

**A futuro**, si se incorpora un módulo de analytics para empresas — retrasos, tiempos exactos de llegada, cumplimiento de horarios — se evaluará migrar el módulo de coordenadas a **MongoDB**, aplicando el patrón **Polyglot Persistence**:

```
PostgreSQL   →  datos estructurados
                empresas, buses, rutas, horarios, paradas

MongoDB      →  datos de tracking e históricos
                coordenadas por recorrido, tiempos entre paradas,
                datos para analytics de retrasos y llegadas
```

Esto es viable sin reescribir el sistema gracias a Clean Architecture, donde la base de datos es un detalle de infraestructura. Solo se reemplazaría el adaptador del módulo de tracking, sin afectar el resto del sistema.

Para el despliegue, se pretende usar planes gratuitos, ya que es un proyecto de portafolio

- Supabase para PostgreSQL
- MongoDB Atlas para la futura incorporación de MongoDB.


## ¿Dónde se despliega? 

## Despliegue: Free tier con servicios en la nube

El sistema se desplegará en plataformas con planes gratuitos, suficientes para un proyecto académico y de portafolio. Cada pieza del sistema vive en el servicio más adecuado para su naturaleza:

```
Frontend          →  Vercel
Backend           →  Render
Base de datos     →  Supabase (PostgreSQL)
App del bus       →  misma URL del frontend (PWA)
```

Todas las plataformas seleccionadas ofrecen integración con GitHub, lo que permite despliegue automático con cada cambio en el repositorio.


---
## Lenguajes, Frameworks, Motores y Herramientas (Testing y Desarrollo)


#### **Frontend: Next.js + TypeScript**

Se utilizará Next.js con TypeScript como framework principal del frontend. Next.js está basado en React y agrega manejo de rutas por estructura de carpetas, layouts independientes por sección y middleware de autenticación, lo cual se adapta naturalmente a un sistema con tres módulos aislados: público, panel de empresa y panel de administración. TypeScript agrega tipado estático que reduce errores en tiempo


---

#### Backend: Java + Spring Boot

Se utilizará Java con Spring Boot como framework backend. Spring Boot es el estándar en la industria empresarial para APIs REST, tiene soporte nativo para Clean Architecture, inyección de dependencias, seguridad y manejo de base de datos. Además cuenta con una comunidad amplia y documentación extensa, lo que facilita resolver problemas durante el desarrollo.

**¿Por qué no otros frameworks?**

| Framework         | Razón de descarte                                                                                                                                                                              |
| ----------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Django (Python)   | Válido técnicamente, pero su arquitectura MTV complicaría su interacción con Clean Architecture.                                                                                               |
| ASP.NET (C#)      | Fue una buena opcion, pero el despliegue gratuito es más limitado que Java. Render y Railway tienen mejor soporte para Spring Boot                                                             |
| Node.js (Express) | Flexible pero sin estructura definida — requiere más decisiones de arquitectura manuales. Para un proyecto que busca demostrar Clean Architecture, Spring Boot lo hace más natural y explícito |

---

### Base de datos: PostgreSQL

Se utilizará PostgreSQL como base de datos principal. Todos los datos del sistema son estructurados y con relaciones estrictas entre sí, como empresas, buses, rutas, paradas, horarios, etc. 

Para las coordenadas GPS se guardará únicamente la última ubicación conocida de cada bus, sobreescribiendo el registro anterior, lo que evitará guardar decenas de miles de coordeadas generadas cada N segundos

Y a futuro, si se mete un módulo de analytics, para que las empresas puedan ver historial de retrasos, tiempos de llegada, historial de recorridos; entonces se evaluará agregar MongoDB,  bajo el patrón Polyglot Persistence, sin afectar la base PostgreSQL existente.

---

### Mapas: Leaflet + OpenStreetMap

Se utilizará Leaflet.js como librería de mapas sobre datos de OpenStreetMap. Ambas son open source, gratuitas y sin necesidad de API key ni tarjeta de crédito, a diferencia de Google Maps o Mapbox que tienen planes gratuitos limitados y requieren método de pago registrado.

---

### Testing

- Backend:
Se utilizará **JUnit** para pruebas unitarias dentro de Spring Boot, enfocadas en la lógica de negocio de los casos de uso. Para pruebas manuales de los endpoints de la API se utilizará **Postman**, permitiendo verificar respuestas, códigos de estado y estructura de datos antes de conectar el frontend.


- Frontend:

Se utilizará Cypress debido a que así no se implementará un segundo lenguajes para utilizar dicho framework, a diferencia de Selenium que es más compatible con Java,Python y C#

Cypress usa  `JavaScript / TypeScript`, los mismos que el proyecto frontend en general.

---

### Despliegue: Docker + Free Tier

Se utilizará Docker para encapsular cada componente del sistema, garantizando que el comportamiento sea idéntico en desarrollo local y en producción. Esto simplifica el despliegue en las plataformas seleccionadas ya que solo necesitan ejecutar el contenedor sin configuración extra del entorno.

```
Frontend (React)      →  Vercel
Backend (Spring Boot) →  Render
Base de datos         →  Supabase (PostgreSQL)
App del bus (PWA)     →  misma URL del frontend
```
