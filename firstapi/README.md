# 🌱 firstapi · Primera API REST con Spring Boot

**Curso Fundamentos de Desarrollo Backend con Java · Temporada XIV · CI Nicaragua · 2026**

Proyecto de práctica para las **Lecturas 06 y 07** (*Introducción a Spring Boot* y
*APIs, endpoints y verbos HTTP*). Todo el código está comentado línea por línea para que
puedas leerlo como si fuera una guía.

---

## 🎯 ¿Qué vas a aprender?

- Cómo se organiza un proyecto Spring Boot (clase principal, `controller/`, `model/`).
- Crear endpoints con `@RestController` y `@GetMapping`.
- Recibir datos con `@PathVariable`, `@RequestParam` y `@RequestBody`.
- Cómo Spring convierte objetos Java a **JSON** (y por qué los getters importan).
- Un **CRUD** completo con los verbos `GET`, `POST`, `PUT` y `DELETE`.
- Responder con los **códigos de estado** correctos (`200`, `201`, `204`, `400`, `404`).
- Validar datos con `@Valid`, `@NotBlank`, `@Email`, `@Min` y `@Max`.
- Probar endpoints automáticamente con **MockMvc**.

---

## 📂 Estructura

```
firstapi
 ┣ 📜 pom.xml                          → dependencias (Maven)
 ┣ 📂 src/main/java/ni/cinicaragua/firstapi
 ┃ ┣ 📜 FirstapiApplication.java       → punto de entrada (main)
 ┃ ┣ 📂 controller
 ┃ ┃ ┣ 📜 HelloController.java         → primeros endpoints GET
 ┃ ┃ ┗ 📜 EstudianteController.java    → CRUD con verbos HTTP
 ┃ ┗ 📂 model
 ┃   ┣ 📜 Persona.java                 → modelo simple
 ┃   ┗ 📜 Estudiante.java              → modelo con validaciones
 ┣ 📂 src/main/resources
 ┃ ┗ 📜 application.properties         → configuración (puerto, H2)
 ┗ 📂 src/test/java/...                → pruebas automáticas
```

---

## 🚀 Cómo ejecutarlo

Requisito: **JDK 17 o superior**.

```bash
cd firstapi
./mvnw spring-boot:run        # Linux / macOS
mvnw.cmd spring-boot:run      # Windows
```

La API queda disponible en **http://localhost:8080**.
En VS Code también puedes abrir `FirstapiApplication.java` y pulsar ▶ **Run**.

Para ejecutar las pruebas:

```bash
./mvnw test
```

---

## 🔌 Endpoints

### HelloController

| Verbo | Ruta | Qué hace |
|:---:|:---|:---|
| GET | `/hello` | Devuelve un saludo fijo |
| GET | `/hello/{nombre}` | Saluda usando un dato de la ruta (`@PathVariable`) |
| GET | `/saludo?nombre=Ana` | Saluda usando un parámetro de consulta (`@RequestParam`) |
| GET | `/persona` | Devuelve una persona en JSON |
| GET | `/personas` | Devuelve una lista de personas en JSON |

### EstudianteController · `/api/estudiantes`

| Verbo | Ruta | Qué hace | Respuesta |
|:---:|:---|:---|:---|
| GET | `/api/estudiantes` | Lista todos | `200` |
| GET | `/api/estudiantes?nombre=an` | Filtra por nombre | `200` |
| GET | `/api/estudiantes/{id}` | Busca uno | `200` · `404` |
| POST | `/api/estudiantes` | Crea uno | `201` · `400` |
| PUT | `/api/estudiantes/{id}` | Actualiza uno | `200` · `404` · `400` |
| DELETE | `/api/estudiantes/{id}` | Elimina uno | `204` · `404` |

> ⚠️ Los estudiantes se guardan en una lista **en memoria**: al reiniciar la app vuelven
> los 3 datos de ejemplo. En la clase de bases de datos la cambiaremos por JPA.

---

## 🧪 Pruébalo con curl (o Postman)

```bash
# Listar
curl http://localhost:8080/api/estudiantes

# Crear
curl -X POST http://localhost:8080/api/estudiantes \
     -H "Content-Type: application/json" \
     -d '{"nombres":"Mario","apellidos":"Ruiz","correo":"mario@correo.com","edad":16}'

# Crear con datos inválidos → 400 Bad Request
curl -i -X POST http://localhost:8080/api/estudiantes \
     -H "Content-Type: application/json" \
     -d '{"nombres":"","apellidos":"Ruiz","correo":"no-es-correo","edad":10}'

# Actualizar
curl -X PUT http://localhost:8080/api/estudiantes/1 \
     -H "Content-Type: application/json" \
     -d '{"nombres":"Ana María","apellidos":"López","correo":"ana@correo.com","edad":17}'

# Eliminar
curl -i -X DELETE http://localhost:8080/api/estudiantes/1
```

---

## 🗄️ Consola H2

Con la app encendida, abre **http://localhost:8080/h2-console**

- JDBC URL: `jdbc:h2:mem:firstapi`
- Usuario: `sa`
- Contraseña: *(vacía)*

---

## 📝 Ejercicios propuestos

1. Agrega un endpoint `GET /hello/{nombre}/{apellido}` que salude con nombre y apellido.
2. Agrega el atributo `carrera` a `Estudiante` con la validación `@NotBlank`.
3. Crea `GET /api/estudiantes/mayores?edad=16` que devuelva los estudiantes con edad mayor o igual.
4. Escribe una prueba en `EndpointsTests` para el endpoint `PUT`.
