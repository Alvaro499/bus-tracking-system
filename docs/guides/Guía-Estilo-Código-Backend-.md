
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

  **Ejemplo:** `Usuario`, `ProductoServicio`, `PedidoControlador`

  

  

### 2.3 Interfaces

  

- Utilizar PascalCase.

  

- Nombres que indiquen su rol.  

  

  **Justificación:** Los nombres que indican el rol de la interfaz ayudan a comprender su función y las responsabilidades que deben cumplir las clases que la implementan

  

  **Ejemplo:** `UsuarioRepositorio`, `ServicioAutenticacion`

  

  

### 2.4 Métodos

  

- Utilizar camelCase.

  

- Nombres que sean verbos o frases verbales que describan la acción.  

  

  **Justificación:** Utilizar verbos o frases verbales que describan la acción que realiza el método hace que el código sea más intuitivo y fácil de entender

  **Ejemplo:** `obtenerUsuarioPorId`, `guardarProducto`, `calcularTotal`

  

  

### 2.5 Variables

  

- **Locales y de Instancia:** camelCase. Nombres descriptivos.  

  

  **Justificación:** Los nombres descriptivos facilitan la comprensión del propósito de cada variable.

  **Ejemplo:** `nombreUsuario`, `precioUnitario`, `cantidad`

  

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

public class Usuario {

    // 1. Campos estáticos

    public static final int MAX_INTENTOS = 3;

    private static int contador;

  

    // 2. Campos de instancia

    private String nombre;

    protected int id;

  

    // 3. Constructores

    public Usuario(String nombre) {

        this.nombre = nombre;

    }

  

    // 4. Métodos

    public static int getContador() {

        return contador;

    }

  

    public void login() {

        // lógica

    }

  

    // 5. Getters/Setters

    public String getNombre() {

        return nombre;

    }

  

    // 6. Métodos estándar

    @Override

    public String toString() {

        return "Usuario: " + nombre;

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

  

// 4. Proyecto

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

  

  **Justificación:** Utilizar camelCase con la primera letra en minúscula para los nombres de los beans sigue la convención estándar de nomenclatura de variables en Java y facilita la identificación de las instancias gestionadas por el contenedor de Spring, ayudando a mantener la coherencia en todo el proyecto.

  **Ejemplo:** `usuarioServicio` para la clase `UsuarioServicio`.

  

  

### 5.2 Anotaciones de Componentes

  

- Utilizar las anotaciones semánticas de Spring (`@Service`, `@Repository`, `@Controller`, `@RestController`, `@Component`) en lugar de `@Component` genérico cuando sea apropiado.

  

  **Justificación:** Utilizar las anotaciones semánticas de Spring (`@Service`, `@Repository`, `@Controller`, `@RestController`, `@Component`) en lugar de `@Component` genérico proporciona una mayor claridad sobre el rol y la responsabilidad del componente dentro de la arquitectura de la aplicación. Con esto se mejora la comprensión del código y facilita la aplicación de aspectos específicos de cada tipo de componente.

  

  **Ejemplo:**

```java

@Repository // Base de datos

public class Repo { /*...*/ }

  

@Service // Lógica

public class Service { /*...*/ }

  

@RestController // API

public class Controller { /*...*/ }

  

```

### 5.3 Mapeo de Endpoints

  

- Utilizar `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` de forma clara y consistente en los controladores REST.

  

- Mantener las rutas de los endpoints en minúsculas y separadas por guiones (`-`).  

  

  **Justificación:** Utilizar `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` de forma explícita y consistente hace que el propósito de cada endpoint REST sea más evidente. Además mantener las rutas de los endpoints en minúscula y separadas por guiones (`-`) es una convención común en las APIs RESTful que mejora la uniformidad y la legibilidad de las URLs.

  

  **Ejemplo:** `/usuarios`, `/{productos-id}`, `/pedidos/nuevo`

## 6. Estándar de Manejo de Errores y Excepciones

Esta sección será documentada en futuras versiones del proyecto.
{
  "message": "Internal Server Error", // Mensaje genérico
  "code": 500
}
```

**_NOTA:_** queda total libertad de generar nuevos encapsulamientos de Excepciones. 
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

### 8.2 Single Responsibility con Patrón Result y CreateEventHandler

El diseño del `CreateEventHandler` es un ejemplo práctico de cómo aplicar el principio de responsabilidad única (SRP), el cual indica que una clase o componente debe tener **una sola razón para cambiar**.

- **Un solo método público**
La interfaz `CreateEventHandler` define únicamente un método público:
```java
Result createEvent(Command command);
```

Este método concentra la entrada y salida del proceso principal: crear un evento. Toda la lógica interna de validación o creación concreta se encapsula en métodos privados, que pueden cambiar sin afectar a quien consume el handler.

- **Separación clara de responsabilidades**
	- `CreateEventHandler` solo **recibe y procesa comandos** relacionados con la creación de eventos.
	- La conversión de datos (`DTO → Command`) y el manejo de errores se hace afuera, en el `Controller`, permitiendo que el handler **se enfoque solo en su lógica central**.

- **Uso de `Result` como canal de comunicación**
La interfaz devuelve un tipo `Result`, que está sellado usando `sealed interface`. Esto obliga a quien use el handler a contemplar **todos los posibles resultados** en un `switch` exhaustivo:
```java
sealed interface Result {
    record Success(...) implements Result {}
    record InvalidFields(...) implements Result {}
    record Unauthorized(...) implements Result {}
    record InvalidDate(...) implements Result {}
}

```

Esto mejora la mantenibilidad y previene errores en tiempo de compilación al agregar nuevos tipos de resultado.

- **Uso de `sealed` - Ventaja de Diseño**
	- Controlar **cuáles clases pueden implementar `Result`**.
	- **Forzar al compilador** a que cualquier `switch` sobre un `Result` sea completo.
	- Hacer el código **más expresivo, seguro y fácil de testear**:

- **Interfaz como contrato**
	- Separar la **lógica de negocio** de su implementación.
	- Facilitar **tests unitarios o mocks**, ya que permite cambiar la implementación sin alterar quien la usa.
```java
CreateEventHandler handler = command -> new Result.Success(201, "Creado");
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


```java
 //Forma adecuada
const isAdultWithValidLicense = user.age > 18 && user.hasLicense;
const isAccountInGoodStanding = !user.isSuspended && user.accountBalance > 0;

if (isAdultWithValidLicense && isAccountInGoodStanding) {
    allowDriving();
}
```


- **Prefijos para booleanos (is, has, can) **


```java
 //Forma complicada
 if (user.valid) { } // ¿Qué significa "valid"?
if (checkPermission()) { } // ¿Devuelve un booleano?
```


```java
 //Forma intuitiva
 if (user.isActive) { }
if (user.hasLicense) { }
if (user.canDrive) { }
```

## 10. Proceso de Aplicación

### 10.1 Code Reviews
* Todo el código subido durante cada Pull Request debe ser revisado y aprobado por al menos un miembro del equipo Backend. Y de ser posible, revisado por un miembro del equipo Frontend.

* Las revisiones de código deben incluir la verificación del cumplimiento de esta guía de estilo.

### 10.2 Integración Continua
* Integrar herramientas de linting y formatting en el pipeline de CI/CD para asegurar el cumplimiento automático de la guía.