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
2. Abre la carpeta `java-cni-t14` (la raíz del repositorio) en VS Code.
3. Ejecuta `DemoApplication.java` con el botón ▶ **Run**,
   o desde la terminal: `./mvnw spring-boot:run` (en Windows: `mvnw.cmd spring-boot:run`).
4. La API queda disponible en `http://localhost:8080`.
5. Abre `productos.http` y haz clic en **Send Request** sobre cada petición.

## Cambiar de base de datos (perfiles)

Por defecto la app usa **SQLite**. Para usar otro motor se activa un **perfil de Spring**:
Spring lee `application.properties` y, encima, el archivo del perfil (`application-<perfil>.properties`),
cuyos valores reemplazan a los de SQLite.

Formas de activar un perfil:

- En `application.properties`, descomentando la línea `spring.profiles.active=...`
- Desde la terminal: `mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql`
- Con la variable de entorno `SPRING_PROFILES_ACTIVE=mysql`

El usuario, la contraseña, el host, el puerto y el nombre de la base se pueden cambiar con las variables
de entorno `DB_USER`, `DB_PASSWORD`, `DB_HOST`, `DB_PORT` y `DB_NAME`, sin tocar el código.

### MySQL (perfil `mysql`)

| Dato | Valor por defecto |
|---|---|
| Archivo | `src/main/resources/application-mysql.properties` |
| Driver | `mysql-connector-j` |
| URL | `jdbc:mysql://localhost:3306/crud_demo` |
| Usuario / contraseña | `root` / `root` |

La base `crud_demo` se crea sola gracias a `createDatabaseIfNotExist=true`, y las tablas las crea Hibernate.

### SQL Server (perfil `sqlserver`)

| Dato | Valor por defecto |
|---|---|
| Archivo | `src/main/resources/application-sqlserver.properties` |
| Driver | `mssql-jdbc` |
| URL | `jdbc:sqlserver://localhost:1433;databaseName=crud_demo` |
| Usuario / contraseña | `sa` / `YourStrong!Passw0rd` |

SQL Server **no** crea la base sola. Antes de arrancar la app, ejecuta en SQL Server Management Studio
(o en Azure Data Studio):

```sql
CREATE DATABASE crud_demo;
```

Las tablas sí las crea Hibernate. Requisitos: la autenticación de SQL Server (usuario y contraseña)
debe estar habilitada y TCP/IP activado en el puerto 1433 (SQL Server Configuration Manager).

## Estructura

```
src/main/java/com/example/demo
├── DemoApplication.java       Punto de entrada de la aplicación
├── producto/
│   ├── Producto.java          Entidad  -> tabla "productos"
│   ├── ProductoRepository.java  Acceso a la BD (Spring genera el SQL)
│   └── ProductoController.java  Endpoints REST
├── categoria/                 Mismo patrón: entidad, repositorio y controlador
├── cliente/                   Mismo patrón: entidad, repositorio y controlador
└── config/
    └── DatosIniciales.java    Inserta datos de ejemplo al arrancar
```

### Datos de ejemplo

Al arrancar la app, `DatosIniciales.java` inserta **5 categorías** y **5 clientes** de ejemplo
(uno de ellos inactivo), pero **solo si la tabla está vacía**. Así se pueden probar los endpoints
`GET` desde el primer momento sin duplicar datos en cada arranque.
Para volver a empezar desde cero, detén la app y borra el archivo `demo.db`.

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
