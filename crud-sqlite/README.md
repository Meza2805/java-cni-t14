# CRUD con Spring Boot + SQLite

Proyecto de ejemplo de una API REST con **Spring Boot**, **Spring Data JPA** y una base de datos **SQLite**.
Tiene tres entidades (Producto, Categoria y Cliente), cada una con su CRUD completo: crear, leer, actualizar y eliminar.
Todo el código está comentado línea por línea.

## Requisitos

- **JDK 21** o superior
- **VS Code** con el *Extension Pack for Java* (o IntelliJ IDEA)
- Extensión **REST Client** de VS Code para probar los endpoints (o Postman)

No hay que instalar SQLite: la base de datos es un archivo (`demo.db`) que se crea solo al arrancar la app.

## Cómo ejecutarlo

1. Descarga la rama:
   ```bash
   git clone -b clase-springboot-crud-sqlite https://github.com/Meza2805/java-cni-t14.git
   ```
2. Abre la carpeta `crud-sqlite` en VS Code.
3. Ejecuta `DemoApplication.java` con el botón ▶ **Run**,
   o desde la terminal: `./mvnw spring-boot:run` (en Windows: `mvnw.cmd spring-boot:run`).
4. La API queda disponible en `http://localhost:8080`.
5. Abre `productos.http` y haz clic en **Send Request** sobre cada petición.

## Estructura

```
src/main/java/com/example/demo
├── DemoApplication.java       Punto de entrada de la aplicación
├── producto/
│   ├── Producto.java          Entidad  -> tabla "productos"
│   ├── ProductoRepository.java  Acceso a la BD (Spring genera el SQL)
│   └── ProductoController.java  Endpoints REST
├── categoria/                 Mismo patrón: entidad, repositorio y controlador
└── cliente/                   Mismo patrón: entidad, repositorio y controlador
```

Flujo de una petición:

```
Cliente (Postman / REST Client)  →  Controller  →  Repository  →  SQLite (demo.db)
```

## Endpoints

| Operación | Método | Productos | Categorías | Clientes |
|---|---|---|---|---|
| Listar todos | GET | `/api/productos/listar` | `/api/categorias/listar` | `/api/clientes/listar` |
| Obtener uno | GET | `/api/productos/obtener/{id}` | `/api/categorias/obtener/{id}` | `/api/clientes/obtener/{id}` |
| Crear | POST | `/api/productos/crear` | `/api/categorias/crear` | `/api/clientes/crear` |
| Actualizar | PUT | `/api/productos/actualizar/{id}` | `/api/categorias/actualizar/{id}` | `/api/clientes/actualizar/{id}` |
| Eliminar | DELETE | `/api/productos/eliminar/{id}` | `/api/categorias/eliminar/{id}` | `/api/clientes/eliminar/{id}` |
| Solo activos | GET | — | — | `/api/clientes/activos` |

### Códigos de respuesta

| Código | Significado | Cuándo aparece |
|---|---|---|
| 200 OK | Todo bien | Listar, obtener, actualizar |
| 201 Created | Recurso creado | Crear |
| 204 No Content | Borrado sin cuerpo | Eliminar |
| 400 Bad Request | Datos inválidos | Falla una validación (`@NotBlank`, `@Email`, `@Min`...) |
| 404 Not Found | No existe | El `id` no está en la tabla |
| 409 Conflict | Duplicado | Nombre de categoría o email de cliente repetido |

## Dependencias principales (`pom.xml`)

| Dependencia | Para qué sirve |
|---|---|
| `spring-boot-starter-webmvc` | Crear los endpoints REST |
| `spring-boot-starter-data-jpa` | Entidades y repositorios (Hibernate) |
| `sqlite-jdbc` | Driver para conectarse a SQLite |
| `hibernate-community-dialects` | Le enseña a Hibernate a generar SQL para SQLite |
| `spring-boot-starter-validation` | Validaciones: `@NotBlank`, `@Email`, `@Min`... |
| `lombok` | Genera getters, setters y constructores |

## Ejercicio propuesto

El chequeo de duplicados (nombre de categoría y email de cliente) solo se hace al **crear**.
Agrega la misma validación al endpoint **actualizar**, para que responda `409 Conflict`
en lugar de un error `500` cuando se intente usar un nombre o email que ya existe.
