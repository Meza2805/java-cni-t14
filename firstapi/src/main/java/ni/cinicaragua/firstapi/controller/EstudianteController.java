package ni.cinicaragua.firstapi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ni.cinicaragua.firstapi.model.Estudiante;

/*
 * ============================================================================
 *  CONTROLADOR: EstudianteController · CRUD con los verbos HTTP
 * ----------------------------------------------------------------------------
 *  CRUD = Create, Read, Update, Delete (Crear, Leer, Actualizar, Borrar).
 *  Cada operación usa un VERBO HTTP distinto sobre la misma ruta base:
 *
 *    Verbo   Ruta                      Acción              Respuesta
 *    ─────── ───────────────────────── ─────────────────── ─────────────────
 *    GET     /api/estudiantes          listar todos        200 OK
 *    GET     /api/estudiantes?nombre=  filtrar por nombre  200 OK
 *    GET     /api/estudiantes/{id}     buscar uno          200 OK · 404
 *    POST    /api/estudiantes          crear               201 Created · 400
 *    PUT     /api/estudiantes/{id}     actualizar          200 OK · 404 · 400
 *    DELETE  /api/estudiantes/{id}     eliminar            204 No Content · 404
 *
 *  CÓDIGOS DE ESTADO HTTP (el "semáforo" de la respuesta):
 *    2xx ✔ todo bien   ·   4xx ✘ error del cliente   ·   5xx ✘ error del servidor
 *
 *  ResponseEntity<T> nos permite elegir el CÓDIGO de estado además del
 *  cuerpo (body) de la respuesta.
 *
 *  ⚠ Por ahora los datos se guardan en una LISTA EN MEMORIA: se pierden al
 *    reiniciar la aplicación. En la clase de bases de datos reemplazaremos
 *    esta lista por un repositorio JPA conectado a una base de datos real.
 * ============================================================================
 */
@RestController
@RequestMapping("/api/estudiantes") // ruta base que comparten TODOS los métodos
public class EstudianteController {

    // "Base de datos" temporal: una lista en memoria.
    private final List<Estudiante> estudiantes = new ArrayList<>();

    // Generador de ids: 1, 2, 3... AtomicLong es seguro aunque lleguen
    // varias peticiones al mismo tiempo.
    private final AtomicLong secuenciaId = new AtomicLong();

    // El constructor se ejecuta una sola vez, cuando Spring crea el
    // controlador. Lo usamos para cargar datos de ejemplo.
    public EstudianteController() {
        estudiantes.add(new Estudiante(secuenciaId.incrementAndGet(), "Ana", "López", "ana@correo.com", 16));
        estudiantes.add(new Estudiante(secuenciaId.incrementAndGet(), "Carlos", "Martínez", "carlos@correo.com", 17));
        estudiantes.add(new Estudiante(secuenciaId.incrementAndGet(), "Lucía", "Hernández", "lucia@correo.com", 15));
    }

    // ------------------------------------------------------------------------
    // GET · LEER TODOS (y filtrar opcionalmente por nombre)
    // ------------------------------------------------------------------------
    // GET /api/estudiantes             → todos
    // GET /api/estudiantes?nombre=an   → los que contienen "an" en sus nombres
    // required = false → el parámetro es opcional (si no viene, llega null).
    @GetMapping
    public List<Estudiante> listar(@RequestParam(required = false) String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return estudiantes;
        }
        List<Estudiante> resultado = new ArrayList<>();
        for (Estudiante e : estudiantes) {
            // Comparamos en minúsculas para que "ANA" y "ana" coincidan.
            if (e.getNombres().toLowerCase().contains(nombre.toLowerCase())) {
                resultado.add(e);
            }
        }
        return resultado;
    }

    // ------------------------------------------------------------------------
    // GET · LEER UNO por su id
    // ------------------------------------------------------------------------
    // GET /api/estudiantes/2  → 200 con el estudiante, o 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<Estudiante> buscarPorId(@PathVariable Long id) {
        Estudiante encontrado = buscar(id);
        if (encontrado == null) {
            return ResponseEntity.notFound().build();     // 404 Not Found
        }
        return ResponseEntity.ok(encontrado);             // 200 OK
    }

    // ------------------------------------------------------------------------
    // POST · CREAR
    // ------------------------------------------------------------------------
    // POST /api/estudiantes   con un JSON en el cuerpo, por ejemplo:
    //   { "nombres": "Mario", "apellidos": "Ruiz",
    //     "correo": "mario@correo.com", "edad": 16 }
    //
    // @RequestBody → convierte el JSON recibido en un objeto Estudiante.
    // @Valid       → revisa las reglas (@NotBlank, @Email...) del modelo;
    //                si alguna falla, Spring responde 400 Bad Request.
    @PostMapping
    public ResponseEntity<Estudiante> crear(@Valid @RequestBody Estudiante nuevo) {
        nuevo.setId(secuenciaId.incrementAndGet()); // el servidor asigna el id
        estudiantes.add(nuevo);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo); // 201
    }

    // ------------------------------------------------------------------------
    // PUT · ACTUALIZAR (reemplaza todos los datos del estudiante)
    // ------------------------------------------------------------------------
    // PUT /api/estudiantes/2   con el JSON completo actualizado
    @PutMapping("/{id}")
    public ResponseEntity<Estudiante> actualizar(@PathVariable Long id,
                                                 @Valid @RequestBody Estudiante datos) {
        Estudiante existente = buscar(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();     // 404
        }
        existente.setNombres(datos.getNombres());
        existente.setApellidos(datos.getApellidos());
        existente.setCorreo(datos.getCorreo());
        existente.setEdad(datos.getEdad());
        return ResponseEntity.ok(existente);              // 200
    }

    // ------------------------------------------------------------------------
    // DELETE · ELIMINAR
    // ------------------------------------------------------------------------
    // DELETE /api/estudiantes/2  → 204 (borrado, sin cuerpo) o 404
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Estudiante existente = buscar(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();     // 404
        }
        estudiantes.remove(existente);
        return ResponseEntity.noContent().build();        // 204 No Content
    }

    // ------------------------------------------------------------------------
    // MÉTODO AUXILIAR (private: no es un endpoint, solo ayuda a los demás)
    // ------------------------------------------------------------------------
    // Recorre la lista y devuelve el estudiante con ese id, o null si no está.
    private Estudiante buscar(Long id) {
        for (Estudiante e : estudiantes) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        return null;
    }
}
