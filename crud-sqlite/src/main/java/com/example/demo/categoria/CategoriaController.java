// Paquete donde vive la clase
package com.example.demo.categoria;

import jakarta.validation.Valid;                   // Activa las validaciones de la entidad
import lombok.RequiredArgsConstructor;             // Lombok: constructor para los campos "final"
import org.springframework.http.HttpStatus;        // Codigos HTTP con nombre (CONFLICT = 409)
import org.springframework.http.ResponseEntity;    // Respuesta HTTP: codigo de estado + cuerpo
import org.springframework.web.bind.annotation.*;  // @RestController, @GetMapping, @PostMapping...

import java.net.URI;   // Para la cabecera "Location" del recurso creado
import java.util.List; // Lista de categorias

@RestController                     // Atiende peticiones HTTP y responde JSON
@RequestMapping("/api/categorias")  // Prefijo comun de todas las rutas de este controller
@RequiredArgsConstructor            // Spring inyecta el repositorio por constructor
public class CategoriaController {

    private final CategoriaRepository repo; // Acceso a la tabla "categorias"

    // ===================== READ: listar todas =====================
    @GetMapping("/listar")              // GET /api/categorias/listar
    public List<Categoria> listar() {
        return repo.findAll();          // SELECT * FROM categorias
    }

    // ===================== READ: obtener una =====================
    @GetMapping("/obtener/{id}")                                      // GET /api/categorias/obtener/1
    public ResponseEntity<Categoria> obtener(@PathVariable Long id) { // Toma el {id} de la URL
        return repo.findById(id)                                      // Busca por id (Optional)
                .map(ResponseEntity::ok)                              // Existe -> 200 OK
                .orElse(ResponseEntity.notFound().build());           // No existe -> 404
    }

    // ===================== CREATE: insertar =====================
    @PostMapping("/crear")                                                  // POST /api/categorias/crear
    public ResponseEntity<?> crear(@Valid @RequestBody Categoria categoria) { // <?> porque puede devolver Categoria o un mensaje de error
        if (repo.existsByNombre(categoria.getNombre())) {                   // El nombre es UNIQUE: revisamos antes de insertar
            return ResponseEntity.status(HttpStatus.CONFLICT)               // 409 Conflict
                    .body("Ya existe una categoria con el nombre: " + categoria.getNombre());
        }
        categoria.setId(null);                                              // El id lo genera la BD
        Categoria guardada = repo.save(categoria);                          // INSERT INTO categorias ...
        return ResponseEntity
                .created(URI.create("/api/categorias/obtener/" + guardada.getId())) // 201 Created + Location
                .body(guardada);                                                     // Devuelve la categoria creada
    }

    // ===================== UPDATE: actualizar =====================
    @PutMapping("/actualizar/{id}")                                      // PUT /api/categorias/actualizar/1
    public ResponseEntity<Categoria> actualizar(@PathVariable Long id,   // id de la URL
                                                @Valid @RequestBody Categoria datos) { // datos nuevos del JSON
        return repo.findById(id)                                         // Buscamos la categoria existente
                .map(c -> {                                              // Si existe...
                    c.setNombre(datos.getNombre());                      //   nuevo nombre
                    c.setDescripcion(datos.getDescripcion());            //   nueva descripcion
                    return ResponseEntity.ok(repo.save(c));              //   UPDATE -> 200 OK
                })
                .orElse(ResponseEntity.notFound().build());              // Si no existe -> 404
    }

    // ===================== DELETE: eliminar =====================
    @DeleteMapping("/eliminar/{id}")                              // DELETE /api/categorias/eliminar/1
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { // Sin cuerpo en la respuesta
        if (!repo.existsById(id)) {                               // Si no existe...
            return ResponseEntity.notFound().build();             // ...404
        }
        repo.deleteById(id);                                      // DELETE FROM categorias WHERE id = ?
        return ResponseEntity.noContent().build();                // 204 No Content
    }
}
