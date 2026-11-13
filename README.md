# Relaciones entre entidades con Spring Boot + JPA

Proyecto de ejemplo de una API REST con **Spring Boot**, **Spring Data JPA** y una base de datos **SQLite** (o MySQL / SQL Server).
Tiene tres entidades (Producto, Categoria y Cliente), cada una con su CRUD completo, y **relaciones** entre ellas:
`@ManyToOne` / `@OneToMany` (Producto - Categoria), `@ManyToMany` (Cliente - Producto) y consultas con **JOIN** en JPQL.
Todo el código está comentado línea por línea.

## Requisitos

- **JDK 21** o superior
- **VS Code** con el *Extension Pack for Java* (o IntelliJ IDEA)
- Extensión **REST Client** de VS Code para probar los endpoints (o Postman)

No hay que instalar SQLite: la base de datos es un archivo (`demo.db`) que se crea solo al arrancar la app.

## Cómo ejecutarlo

1. Descarga la rama:
   ```bash
   git clone -b clase-springboot-relaciones --single-branch https://github.com/Meza2805/java-cni-t14.git
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
├── categoria/                 Mismo patrón (+ CategoriaResumen.java: DTO para el GROUP BY)
├── cliente/                   Mismo patrón: entidad, repositorio y controlador
└── config/
    └── DatosIniciales.java    Inserta datos de ejemplo al arrancar
```

### Datos de ejemplo

Al arrancar la app, `DatosIniciales.java` inserta **5 categorías**, **5 clientes** (uno inactivo),
**7 productos** (enlazados a su categoría; uno queda sin categoría) y los **favoritos** de 4 clientes,
pero **solo si la tabla está vacía**. Así se pueden probar los endpoints
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

## Relaciones entre entidades

```
 categorias                 productos                     cliente_favoritos            clientes
┌──────────────┐ 1      N ┌───────────────────┐ 1      N ┌──────────────────┐ N      1 ┌──────────┐
│ id (PK)      │◄─────────┤ categoria_id (FK) │◄─────────┤ producto_id (FK) │          │ id (PK)  │
│ nombre       │          │ id (PK)           │          │ cliente_id  (FK) ├─────────►│ nombre   │
│ descripcion  │          │ nombre, precio    │          └──────────────────┘          │ email ...│
└──────────────┘          └───────────────────┘           tabla intermedia             └──────────┘
```

| Relación | Anotación (dueño) | Lado inverso | En la BD |
|---|---|---|---|
| Muchos productos → una categoría | `@ManyToOne` + `@JoinColumn` en `Producto` | `@OneToMany(mappedBy = "categoria")` en `Categoria` | Columna `productos.categoria_id` |
| Muchos clientes ↔ muchos productos (favoritos) | `@ManyToMany` + `@JoinTable` en `Cliente` | — (unidireccional) | Tabla intermedia `cliente_favoritos` |

Ideas clave:

- El **dueño** de la relación es la entidad que tiene la clave foránea (o el `@JoinTable`). Solo los cambios en el dueño se guardan en la BD.
- `@JsonIgnore` en `Categoria.productos` evita un JSON infinito (categoría → productos → categoría → ...).
- `@ManyToOne` es **EAGER** por defecto (trae la categoría junto al producto). `@OneToMany` y `@ManyToMany` son **LAZY** (se cargan solo al usarlas).
- **Integridad referencial**: no se puede borrar una categoría con productos (409), y al borrar un producto primero se quita de los favoritos.
  MySQL y SQL Server crean las claves foráneas y las hacen cumplir; SQLite, por defecto, no las revisa.

### Endpoints de relaciones

| Operación | Método | Ruta |
|---|---|---|
| Crear producto con categoría | POST | `/api/productos/crear` con `"categoria": { "id": 1 }` |
| Asignar / cambiar categoría | PUT | `/api/productos/{id}/categoria/{categoriaId}` |
| Quitar categoría | DELETE | `/api/productos/{id}/categoria` |
| Productos sin categoría | GET | `/api/productos/sin-categoria` |
| Productos de una categoría | GET | `/api/categorias/{id}/productos` |
| Favoritos de un cliente | GET | `/api/clientes/{id}/favoritos` |
| Agregar favorito | POST | `/api/clientes/{id}/favoritos/{productoId}` |
| Quitar favorito | DELETE | `/api/clientes/{id}/favoritos/{productoId}` |
| Clientes que tienen un producto como favorito | GET | `/api/productos/{id}/clientes` |

### Consultas con JOIN (JPQL)

JPQL se escribe con **clases y atributos** (`JOIN p.categoria c`), no con tablas: JPA ya sabe por qué columnas unir.

| Consulta | Ruta | JOIN |
|---|---|---|
| Productos por nombre de categoría | `GET /api/productos/buscar-por-categoria?nombre=Deportes` | `INNER JOIN` |
| Productos de una categoría en un rango de precio | `GET /api/productos/buscar-por-categoria-y-precio?categoriaId=1&min=10&max=100` | `INNER JOIN` + `BETWEEN` |
| Resumen: total y precio promedio por categoría | `GET /api/categorias/resumen` | `LEFT JOIN` + `GROUP BY` + DTO |
| Clientes con favoritos de una categoría | `GET /api/clientes/por-categoria-favorita/{categoriaId}` | Doble `JOIN` (3 entidades) |
| Clientes con sus favoritos en 1 consulta | `GET /api/clientes/con-favoritos` | `LEFT JOIN FETCH` (evita N+1) |

Para ver el SQL que genera cada consulta, mira la consola: `spring.jpa.show-sql=true` lo imprime.

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
