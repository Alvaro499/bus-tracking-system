
**Versión:** 1.0


  
## 1. Introducción

  

  

### 1.1 Propósito

  

Este documento define las convenciones de estilo de código para el proyecto backend desarrollado en Java utilizando el framework Spring Boot. Su objetivo es asegurar la consistencia, legibilidad y mantenibilidad del código base por todos los miembros del equipo.

  

  

### 1.2 Audiencia

  

Este documento está dirigido a todos los desarrolladores que contribuyen al proyecto backend y hacia aquellos de cualquier otro ámbito de interés del proyecto interesados.

  

  

### 1.3 Referencias

  

- [Guía de estilo de código de Google para Java](https://google.github.io/styleguide/javaguide.html)  
- [Convenciones Clean Code](https://elhacker.info/manuales/Lenguajes%20de%20Programacion/Codigo%20limpio%20-%20Robert%20Cecil%20Martin.pdf)
- [Convenciones de código de Oracle para Java](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html)

## 2. Convenciones de Nombres

  

  

### 2.1 Paquetes

  

- Utilizar minúsculas y separar palabras con puntos (.).

  

- Estructura basada en la funcionalidad o capas: `com.tuempresa.nombredelproyecto.<modulo>.<capa>`  

  

  **Justificación:** La convención de nombres en minúsculas con separadores de puntos facilita la organización lógica del código y evita conflictos con nombres de clases. Con esto se establece una estructura basada en la funcionalidad o capas que mejora la mantenibilidad y la comprensión del rol de cada componente dentro de la aplicación.

  

  **Ejemplo:**  

  

  `com.example.gestionpedidos.controlador`,  

  

  `com.example.gestionpedidos.servicio`,  

  

  `com.example.gestionpedidos.modelo`,  

  

  `com.example.gestionpedidos.repositorio`

  

  

### 2.2 Clases

  

- Utilizar PascalCase.

  

- Nombres sustantivos claros y descriptivos.  

  

  **Justificación:** Usar nombres sustantivos claros y descriptivos mejora la legibilidad y la comprensión del propósito de cada clase dentro del sistema.

**Ejemplo:** `Empresa`, `TrackingServicio`, `BusControlador`

  

  

### 2.3 Interfaces

  

- Utilizar PascalCase.

  

- Nombres que indiquen su rol.  

  

  **Justificación:** Los nombres que indican el rol de la interfaz ayudan a comprender su función y las responsabilidades que deben cumplir las clases que la implementan

  

**Ejemplo:** `EmpresaRepositorio`, `ServicioTracking`

  

  

### 2.4 Métodos

  

- Utilizar camelCase.

  

- Nombres que sean verbos o frases verbales que describan la acción.  

  

  **Justificación:** Utilizar verbos o frases verbales que describan la acción que realiza el método hace que el código sea más intuitivo y fácil de entender

**Ejemplo:** `obtenerEmpresaPorId`, `guardarBus`, `calcularETA`

  

  

### 2.5 Variables

  

- **Locales y de Instancia:** camelCase. Nombres descriptivos.  

  

  **Justificación:** Los nombres descriptivos facilitan la comprensión del propósito de cada variable.

**Ejemplo:** `nombreEmpresa`, `latitudBus`, `velocidadPromedio`

  

- **Constantes (`static final`):** UPPER_SNAKE_CASE.  

  

  **Justificación:** UPPER_SNAKE_CASE es la convención para indicar que una variable es una constante, mejorando la legibilidad y distinguiéndolas de las variables modificables.

  **Ejemplo:** `MAX_INTENTOS`, `API_KEY`

  

- **Booleanas:** Prefijos `is`, `has`, `can`.  

  

  **Justificación:** Los prefijos `is`, `has`, `can` hacen que las variables booleanas sean más explícitas sobre la condición que representan, mejorando la claridad del código.

  **Ejemplo:** `esActivo`, `tienePermisos`, `puedeEditar`



## 3. Formato del Código

  

  

### 3.1 Indentación

  

- Utilizar **4 espacios** para la indentación. No usar tabs.

  

  **Justificación:** La indentación consistente con 4 espacios mejora significativamente la legibilidad del código, permitiendo identificar fácilmente los bloques de código y el flujo de control, así mismo evitar los tabs asegura la uniformidad visual en diferentes entornos de desarrollo.

  **Ejemplo:**
  
```java
public class Ejemplo {

    // 4 espacios para el cuerpo de clase

    public void metodo() {

        // 4 espacios para el bloque de método

        if (condicion) {

            // 4 espacios para el bloque if

            System.out.println("Texto indentado correctamente");

        }

    }

}
```



### 3.2 Longitud de Línea

  

- Máximo **120 caracteres** por línea.

  

  **Justificación:** Limitar la longitud de las líneas a un máximo de 120 caracteres mejora la legibilidad del código al evitar el desplazamiento horizontal, lo que facilita la revisión del código y el trabajo en equipos con diferentes configuraciones de pantalla.

   **Ejemplo:**  

```java

// Correcto (80 chars)

String mensaje = "Línea corta que cumple con el límite de longitud";

  

// Incorrecto (130 chars)

String mensajeLargo = "Esta línea claramente excede el límite recomendado de 120 caracteres, haciendo necesario el scroll horizontal para leerla completamente";

```

### 3.3 Espacios en Blanco

  

- Alrededor de operadores binarios (`=`, `+`, `-`, `*`, `/`, `==`, `!=`, `>`, `<`, `>=`, `<=`, `&&`, `||`).

  

- Después de comas en listas de argumentos.

  

- Después de las palabras clave `if`, `for`, `while`, `switch`.

  

- Líneas en blanco entre secciones lógicas del código y entre métodos.

  

  **Justificación:** El uso estratégico de espacios en blanco alrededor de operadores, después de comas y palabras clave, y entre secciones lógicas del código mejora la claridad visual y facilita la distinción entre los diferentes elementos del lenguaje, haciendo el código más fácil de leer y comprender.

  **Ejemplo:**

```java

public class Calculadora {

    public int sumar(int a, int b) {

        return a + b;  // Espacio alrededor de +

    }

  

    public boolean esPositivo(int numero) {

        if (numero > 0) {  // Espacio después de if y alrededor de >

            return true;

        }

        return false;

    }

  

    // Línea en blanco entre métodos

    public void imprimir(String mensaje, int veces) {

        for (int i = 0; i < veces; i++) {  // Espacios en for, ; y <

            System.out.println(mensaje);

        }

    }

}

```

### 3.4 Llaves `{ }`

  

- Utilizar el estilo **K&R** (llave de apertura en la misma línea, llave de cierre en línea separada).

  

   **Justificación:** El estilo K&R es una convención común en Java que mejora la legibilidad al agrupar visualmente el bloque de código asociado a una estructura de control (como `if`, `for`, `while`) o a la definición de una clase o método

  **Ejemplo:**

```java
if (condicion) {
    // Código
}
```

  

  

## 4. Estructura del Código

  

  

### 4.1 Orden de los Miembros de una Clase

  

1. Campos estáticos públicos  

  

2. Campos estáticos protegidos  

  

3. Campos estáticos (sin modificador de acceso)  

  

4. Campos estáticos privados  

  

5. Campos de instancia públicos  

  

6. Campos de instancia protegidos  

  

7. Campos de instancia (sin modificador de acceso)  

  

8. Campos de instancia privados  

  

9. Constructores  

  

10. Métodos estáticos  

  

11. Métodos de instancia  

  

12. Getters y Setters (si son necesarios, seguir un patrón consistente)  

  

13. Métodos `equals()`, `hashCode()`, `toString()` (si se sobrescriben)  

  

14. Clases internas (si son necesarias)  

  

  **Justificación:** Seguir un orden consistente para la declaración de los miembros de una clase mejora la organización y la legibilidad del código, además facilita la búsqueda de elementos específicos dentro de la clase y proporciona una estructura predecible para otros desarrolladores. Con esto orden sugerido agrupará los elementos por su tipo (estáticos vs. instancia) y luego por su modificador de acceso, colocando los constructores y métodos principales en una posición central.

 **Ejemplo:**

```java

public class Empresa {

    // 1. Campos estáticos

    public static final int MAX_BUSES = 100;

    private static int empresasRegistradas;

  

    // 2. Campos de instancia

    private String nombre;

    protected Long id;

    private String estado;

  

    // 3. Constructores

    public Empresa(String nombre) {

        this.nombre = nombre;

        this.estado = "PENDIENTE";

    }

  

    // 4. Métodos

    public static int getEmpresasRegistradas() {

        return empresasRegistradas;

    }

  

    public void aprobar() {

        this.estado = "APROBADA";

    }

  

    // 5. Getters/Setters

    public String getNombre() {

        return nombre;

    }

  

    // 6. Métodos estándar

    @Override

    public String toString() {

        return "Empresa: " + nombre + " (" + estado + ")";

    }

}

```

### 4.2 Comentarios

  

- Utilizar comentarios `//` para explicaciones cortas dentro de los métodos.

  

- Utilizar comentarios `/* ... */` para comentarios más extensos o para marcar secciones.

  

- Utilizar **Javadoc** (`/** ... */`) para documentar clases, interfaces, métodos públicos y protegidos, incluyendo `@param`, `@return`, `@throws`.

  

  **Justificación:** Los comentarios cortos (`//`) son útiles para aclarar líneas específicas o lógica dentro de un método. Por otro lado,  los comentarios más extensos (`/* ... */`) sirven para explicar bloques de código o secciones completas. Además Javadoc (`/** ... */`) es crucial para documentar la API pública y protegida, permitiendo la generación automática de documentación y proporcionando información importante sobre el uso de clases, interfaces y métodos (parámetros, valores de retorno, excepciones).

  **Ejemplo:**
```java

/**

 * Calcula el área de un círculo

 * @param radio Radio del círculo (debe ser positivo)

 * @return Área calculada

 * @throws IllegalArgumentException si radio es negativo

 */

public class Calculadora {

  

    /*

     * Constantes matemáticas

     * (sección agrupada)

     */

    private static final double PI = 3.1416;

  

    public double areaCirculo(double radio) {

        // Validación rápida

        if (radio < 0) {

            throw new IllegalArgumentException("Radio negativo");

        }

        return PI * radio * radio;

    }

}

```

### 4.3 Imports

  

- Organizar en el siguiente orden:

  

  1. `java.*`

  

  2. `javax.*`

  

  3. `org.*` (Spring y otras librerías principales)

  

  4. Imports del proyecto (`com.tuempresa...`)

  

- Dentro de cada grupo, ordenar alfabéticamente.

  

- Evitar imports comodín (`.*`) a menos que sea estrictamente necesario.

  

  **Justificación:** Organizar los imports de forma lógica y alfabética mejora la legibilidad y facilita la identificación de las dependencias del código por lo que agrupar los imports por origen (Java core, extensiones, librerías de terceros, proyecto actual) proporciona una visión clara de las dependencias externas e internas. Además evitar los imports comodín (`.*`) hace que el código sea más explícito sobre las clases que se están utilizando, lo que puede ayudar a prevenir conflictos de nombres y a mejorar la comprensión de las dependencias.

  

  **Ejemplo:**

```java

// 1. Java Core

import java.time.LocalDate;

import java.util.List;

  

// 2. Java EE/Jakarta

import javax.persistence.Entity;

import javax.persistence.Id;

  

// 3. Librerías (Spring, etc)

import org.springframework.stereotype.Service;

import org.hibernate.annotations.Cache;

  bustracking.companies.domain.Empresa;

import com.bustracking.shared

import com.tuempresa.app.model.User;

import com.tuempresa.app.util.Validator;

```

### 4.4 Anotaciones de Spring Boot

  

- Colocar las anotaciones en líneas separadas directamente encima del elemento al que se aplican.

  

- Mantener un orden consistente para las anotaciones (por ejemplo, anotaciones de Spring primero, luego las personalizadas).

  

  **Justificación:** Colocar las anotaciones en líneas separadas directamente encima del elemento al que se aplican mejora la claridad y la legibilidad del código, facilitando la identificación de la funcionalidad o la configuración que la anotación proporciona. Por lo tanto, mantener un orden consistente para las anotaciones (por ejemplo, primero las anotaciones de Spring, luego las personalizadas) contribuye a una estructura de código más organizada y fácil de entender.

  **Ejemplo:**

```java
@Service
@Transactional
public class MiServicio {
    // ...
}
```

  

  

## 5. Mejores Prácticas Específicas de Spring Boot

  

  

### 5.1 Nombres de Beans

  

- Utilizar camelCase con la primera letra en minúscula del nombre de la clase.

**Ejemplo:** `empresaServicio` para la clase `EmpresaServicio`.

  **Justificación:** Utilizar camelCase con la primera letra en minúscula para los nombres de los beans sigue la convención estándar de nomenclatura de variables en Java y facilita la identificación de las instancias gestionadas por el contenedor de Spring, ayudando a mantener la coherencia en todo el proyecto.

  **Ejemplo:** `usuarioServicio` para la clase `UsuarioServicio`.

  

  

### 5.2 Anotaciones de Componentes

  

- Utilizar las anotaciones semánticas de Spring (`@Service`, `@Repository`, `@Controller`, `@RestController`, `@Component`) en lugar de `@Component` genérico cuando sea apropiado.

  

  **Justificación:** Utilizar las anotaciones semánticas de Spring (`@Service`, `@Repository`, `@Controller`, `@RestController`, `@Component`) en lugar de `@Component` genérico proporciona una mayor claridad sobre el rol y la responsabilidad del componente dentro de la arquitectura de la aplicación. Con esto se mejora la comprensión del código y facilita la aplicación de aspectos específicos de cada tipo de componente.

  

  **Ejemplo:**

```javaEmpresaRepository { /*...*/ }

  

@Service // Lógica

public class EmpresaService { /*...*/ }

  

@RestController // API

public class Empresa

@RestController // API

public class Controller { /*...*/ }

  

```

### 5.3 Mapeo de Endpoints

  

- Utilizar `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` de forma clara y consistente en los controladores REST.

  

- Mantener las rutas de los endpoints en minúsculas y separadas por guiones (`-`).  

  

  **Justificación:** Utilizar `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` de forma explícita y consistente hace que el propósito de cada endpoint REST sea más evidente. Además mantener las rutas de los endpoints en minúscula y separadas por guiones (`-`) es una convención común en las APIs RESTful que mejora la uniformidad y la legibilidad de las URLs.

  

**Ejemplo:** `/empresas`, `/{buses-id}/ubicacion`, `/rastreo/actualizar`

## 6. Estándar de Manejo de Errores y Excepciones

### 6.1 Jerarquía de Excepciones

El proyecto utiliza una jerarquía definida de excepciones para manejar diferentes tipos de errores de forma consistente:

- **`ApplicationException`** (abstracta, extiende `RuntimeException`): Clase base de todas las excepciones del proyecto. Contiene tres campos inmutables:
  - `errorCode`: Código de error que actúa como contrato entre backend y frontend.
  - `userMessage`: Mensaje legible para el usuario final.
  - `devMessage`: Mensaje técnico con detalles para logs del servidor.

- **`NotFoundException`**: Se lanza cuando un recurso solicitado no existe en la base de datos.

- **`ValidationException`**: Se lanza cuando los datos enviados por el cliente no cumplen las validaciones requeridas.

- **`BusinessRuleException`**: Se lanza cuando ocurre una violación de las reglas de negocio de la aplicación. No se incluye "cause" al ser reglas de negocio.

- **`ExternalServiceException`**: Se lanza cuando falla la comunicación con servicios externos (APIs de terceros, integraciones, etc.). Es la única excepción que captura la causa raíz (`Throwable cause`).

**Justificación:** Una jerarquía clara de excepciones permite manejar diferentes tipos de errores de forma específica en cada módulo, mejorando el control del flujo de excepciones y facilitando el debugging. Cada tipo de excepción comunica claramente qué tipo de problema ocurrió, lo que mejora tanto la experiencia del usuario como el mantenimiento del código.

### 6.2 Separación de Mensajes: userMessage vs devMessage

Uno de los principios clave en el manejo de errores es la **separación clara entre mensajes para el usuario y mensajes técnicos**:

- **`userMessage`**: Mensaje legible y amigable para el usuario final. Está redactado en español y no contiene detalles técnicos. Es lo **único** que viaja en el JSON de respuesta HTTP hacia el cliente.

- **`devMessage`**: Mensaje técnico detallado para los logs del servidor. Puede incluir valores de variables, IDs, queries, stack traces, etc. **Nunca se expone al cliente** por razones de seguridad y claridad.

**Ejemplo de construcción:**

```java
throw new NotFoundException(
    ErrorCode.USER_NOT_FOUND,
    "El usuario solicitado no existe",  // userMessage
    "Usuario con ID 42 no encontrado en la base de datos"  // devMessage
);
```

**En los logs del servidor:**
```
WARN - Not found: Usuario con ID 42 no encontrado en la base de datos
```

**En la respuesta HTTP al cliente:**
```json
{
  "code": "USER_NOT_FOUND",
  "message": "El usuario solicitado no existe"
}
```

### 6.3 Formato de Respuesta de Error al Cliente

Todas las respuestas de error siguen un formato JSON consistente:

```json
{
  "code": "VALIDATION_ERROR",
  "message": "El email ingresado no es válido",
  "timestamp": 1711612345000
}
```

**Campos:**
- `code`: Valor del `ErrorCode` enum (nunca cambia una vez definido).
- `message`: El `userMessage` de la excepción.
- `timestamp`: Timestamp Unix en milisegundos del momento en que ocurrió el error.

### 6.4 Mapeo de HTTP Status Codes

El `AdminExceptionHandler` (y similares en otros módulos) mapea cada tipo de excepción a un código HTTP específico:

| Excepción | Código HTTP | Reason Phrase |
|-----------|-------------|---------------|
| `NotFoundException` | 404 | Not Found |
| `ValidationException` | 400 | Bad Request |
| `BusinessRuleException` | 422 | Unprocessable Entity |
| `ExternalServiceException` | 502 | Bad Gateway |
| `ApplicationException` (fallback) | 400 | Bad Request |
| `Exception` (genérica) | 500 | Internal Server Error |

**Justificación:** Cada código HTTP comunica claramente el tipo de error según el estándar RESTful, lo que permite al cliente frontend tomar decisiones informadas sobre cómo reaccionar ante cada tipo de error.

### 6.5 Regla de Diseño de Subclases

Todas las subclases de `ApplicationException` deben seguir esta regla de diseño:

1. **Recibir obligatoriamente `userMessage` y `devMessage` como parámetros separados.**
2. **No se permite un constructor con un único mensaje genérico.**

Esto asegura que cada excepción siempre tenga un mensaje amigable para el usuario y un mensaje técnico para los desarrolladores.

**Correcto:**
```java
public class NotFoundException extends ApplicationException {
    public NotFoundException(ErrorCode errorCode, String userMessage, String devMessage) {
        super(errorCode, userMessage, devMessage);
    }
}

// Uso:
throw new NotFoundException(
    ErrorCode.ADMIN_NOT_FOUND,
    "El administrador solicitado no existe",
    "Admin con ID 123 no encontrado en tabla ADMINS"
);
```

**Incorrecto (no permitido):**
```java
public class NotFoundException extends ApplicationException {
    public NotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message, message);  // ❌ Mismo mensaje para ambos
    }
}
```

### 6.6 El ErrorCode: Contrato entre Backend y Frontend

El `ErrorCode` actúa como un **contrato inmutable entre el backend y el frontend**:

- Cada código debe ser único y descriptivo (ej: `USER_NOT_FOUND`, `DUPLICATE_RESOURCE`).
- **Una vez definido, nunca debe cambiar de nombre.** Cambiarlo rompe las aplicaciones clientes que dependen de ese código.
- El `message` (userMessage) **sí puede cambiar** sin romper nada, ya que es solo para el usuario final.

**Ejemplo de evolución segura:**
```java
// ✅ SEGURO - El código permanece igual
ErrorCode.USER_NOT_FOUND
// En v1.0: message = "Usuario no encontrado"
// En v2.0: message = "El usuario solicitado no existe"  // Cambio seguro

// ❌ PELIGROSO - Cambiar el código rompe clientes
// USER_NOT_FOUND → USER_DOES_NOT_EXIST  // Rompe clientes que esperan USER_NOT_FOUND
```

### 6.7 Ejemplo Completo: Manejo en un Caso de Uso

```java
@Service
public class ObtenerAdminService {
    
    public AdminResponse execute(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
            .orElseThrow(() -> new NotFoundException(
                ErrorCode.ADMIN_NOT_FOUND,
                "El administrador solicitado no existe",
                "Admin con ID " + adminId + " no encontrado en ADMINS"
            ));
        
        if (!admin.isActive()) {
            throw new BusinessRuleException(
                ErrorCode.ADMIN_NOT_ACTIVE,
                "Este administrador no está activo",
                "Admin ID " + adminId + " tiene estado: " + admin.getStatus()
            );
        }
        
        return new AdminResponse(admin);
    }
}
```

**Flujo:**
1. El servicio lanza una excepción con mensajes claros.
2. El `AdminExceptionHandler` la captura.
3. El handler loguea el `devMessage` en los logs del servidor.
4. La respuesta HTTP solo incluye el `userMessage` en el JSON.
5. El cliente recibe un código HTTP y un mensaje de usuario legible.

**Nota:** Queda total libertad para generar nuevos encapsulamientos de excepciones específicas de cada módulo (ej: `CompanyNotFoundException`, `InvalidBusStateException`), siempre y cuando respeten la jerarquía y las reglas de diseño documentadas aquí.


### 8. SOLID
Es una familia de cinco principios impulsados por Robert C. Martín. Estos principios establecen unos estándares útiles para construir tanto módulos individuales como una arquitectura más grande. Es importante mencionar que estos principios están mayormente pensados para el paradigma de POO (Programación Orientada a Objetos). Sin embargo, algunos de estos principios son aplicables en otros paradigmas.

En este apartado se impulsará el uso de unicamente el primer principio **Single Responsibility**.


### 8.1 Single Responsibility

Este principio indica que cada clase o función debe de poseer una única responsabilidad con el fin de mantener la mayor cohesión y menor acoplamiento posibles. Es decir, separar el código lo máximo posible y evitar dependencia entre sus componentes.

Ejemplo básico:
```java
//No recomendado - (Una clase hace demasiado)
class User {
    void saveToDatabase() { /*...*/ }
    void sendEmail() { /*...*/ }
    void generateReport() { /*...*/ }
}
```


```java
//Recomendado - (Separar responsabilidades)
class User {
    // Solo maneja datos del usuario
}

class UserRepository {
    void save(User user) { /*...*/ }
}

class EmailService {
    void sendWelcomeEmail(User user) { /*...*/ }
}
```


### 9. Clean-Code

Clean Code es un conjunto de principios diseñados para mejorar la legibilidad, mantenibilidad y eficiencia del código. Aunque algunas prácticas pueden variar según el contexto o preferencias del equipo, existen reglas fundamentales ampliamente aceptadas que todo desarrollador debería seguir. A continuación, se presentan algunas de las más importantes, enfocadas en métodos y nombres, para ayudar a escribir código claro, consistente y fácil de entender.
### 9.1 Métodos

- **Tamaño Reducido:** los métodos deben ser pequeños para que sean fáciles de leer y entender. La regla básica es que una función no debería tener más de 20 líneas. 


**_NOTA:_** En este proyecto, por temas de subjetividad, se establecerá un máximo de 30 líneas.


- **Exceso de Argumentos:** utilizar un número mínimo de argumentos en las funciones, preferiblemente entre dos o 3. Esta recomendación se omite para los constructores, inyección de independencias o cualquier otra estructura fabricadora.

- **Evitar exceso de anidaciones dentro de un solo método:** el uso excesivo de de condicionales y bucles anidados en un solo método evita que el flujo del proceso sea lógico.

```java
//Forma Inadecuada
function calculateDiscount(price: number, isMember: boolean, hasCoupon: boolean) {
    if (isMember) {
        if (hasCoupon) {
            return price * 0.7; // 30% off
        } else {
            return price * 0.9; // 10% off
        }
    } else {
        if (hasCoupon) {
            return price * 0.8; // 20% off
        } else {
            return price; // No discount
        }
    }
}
```

```java
//Forma Adecuada
function calculateDiscount(price: number, isMember: boolean, hasCoupon: boolean) {
    if (isMember && hasCoupon) return price * 0.7;
    if (isMember) return price * 0.9;
    if (hasCoupon) return price * 0.8;
    return price;
}
```

### 9.2 Variables

- **Evitar validaciones largas en `if` (usar variables descriptivas)**


```java
 //Forma complicada
 if (user.age > 18 && user.hasLicense && !user.isSuspended && user.accountBalance > 0) {
    allowDriving();
}
```


public double calcularTarifa(double distancia, boolean esEstudiante, boolean tieneDescuento) {
    if (esEstudiante) {
        if (tieneDescuento) {
            return distancia * 0.5; // 50% descuento
        } else {
            return distancia * 0.8; // 20% descuento
        }
    } else {
        if (tieneDescuento) {
            return distancia * 0.9; // 10% descuento
        } else {
            return distancia; // Sin descuento
        }
    }
}
```

```java
//Forma Adecuada
public double calcularTarifa(double distancia, boolean esEstudiante, boolean tieneDescuento) {
    if (esEstudiante && tieneDescuento) return distancia * 0.5;
    if (esEstudiante) return distancia * 0.8;
    if (tieneDescuento) return distancia * 0.9;
    return distanciave) { }
if (user.hasLicense) { }
if (user.canDrive) { }
```

## 10. Proceso de Aplicación

### 10.1 Code Reviews
* Todo el código subido durante cada Pull Request debe ser revisado y aprobado por al menos un miembro del equipo Backend. Y de ser posible, revisado por un miembro del equipo Frontend.

//Forma complicada
if (bus.estado != null && bus.empresa.isAprobada && !bus.estaDeMantenimiento && bus.ubicacion != null) {
    permiteRastreo();
}
```


```java
//Forma adecuada
boolean estaEnServicio = bus.estado != null && bus.empresa.isAprobada;
boolean puedeRastrearse = !bus.estaDeMantenimiento && bus.ubicacion != null;

if (estaEnServicio && puedeRastrearse) {
    permiteRastreo();//Forma complicada
if (empresa.activa) { } // ¿Qué significa "activa"?
if (verificarBus(id)) { } // ¿Devuelve un booleano?
```


```java
//Forma intuitiva
if (empresa.isAprobada) { }
if (bus.hasUbicacion) { }
if (rastreo.canEnviarDatos) { }