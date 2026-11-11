// Paquete donde vive la clase
package com.example.demo.producto;

import jakarta.validation.Valid;                       // Activa las validaciones de la entidad (@NotBlank, etc.)
import lombok.RequiredArgsConstructor;                 // Lombok: constructor para los campos "final"
import org.springframework.http.ResponseEntity;        // Respuesta HTTP completa: codigo de estado + cuerpo
import org.springframework.web.bind.annotation.*;      // @RestController, @GetMapping, @PostMapping...

import java.net.URI;   // Para armar la URL del recurso creado (cabecera "Location")
import java.util.List; // Lista de productos

@RestController                    // Clase que atiende peticiones HTTP y responde JSON automaticamente
@RequestMapping("/api/productos")  // Prefijo comun de TODAS las rutas de este controller
@RequiredArgsConstructor           // Genera el constructor -> Spring inyecta el repositorio ahi
public class ProductoController {

    private final ProductoRepository repo; // Acceso a la BD. "final" = se asigna una vez (inyeccion por constructor)

    // ===================== READ: listar todos =====================
    @GetMapping("/listar")                 // GET /api/productos/listar
    public List<Producto> listar() {
        return repo.findAll();             // SELECT * FROM productos -> Spring lo convierte en un arreglo JSON
    }

    // ===================== READ: obtener uno =====================
    @GetMapping("/obtener/{id}")                                     // GET /api/productos/obtener/1  ({id} es variable)
    public ResponseEntity<Producto> obtener(@PathVariable Long id) { // @PathVariable: toma el {id} de la URL
        return repo.findById(id)                                     // Busca por id -> devuelve Optional (puede venir vacio)
                .map(ResponseEntity::ok)                             // Si existe -> 200 OK con el producto
                .orElse(ResponseEntity.notFound().build());          // Si no existe -> 404 Not Found
    }

    // ===================== CREATE: insertar =====================
    @PostMapping("/crear")                                                       // POST /api/productos/crear
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) { // @RequestBody: JSON -> objeto Producto. @Valid: valida (si falla -> 400)
        producto.setId(null);                                                    // Ignoramos cualquier id enviado: lo genera la BD
        Producto guardado = repo.save(producto);                                 // INSERT INTO productos ... -> devuelve el producto con su id
        return ResponseEntity
                .created(URI.create("/api/productos/obtener/" + guardado.getId())) // 201 Created + cabecera Location con la URL del nuevo recurso
                .body(guardado);                                                    // Cuerpo: el producto guardado en JSON
    }

    // ===================== UPDATE: actualizar =====================
    @PutMapping("/actualizar/{id}")                                     // PUT /api/productos/actualizar/1
    public ResponseEntity<Producto> actualizar(@PathVariable Long id,   // id que viene en la URL
                                               @Valid @RequestBody Producto datos) { // datos nuevos que vienen en el JSON
        return repo.findById(id)                                        // 1) Buscamos el producto existente
                .map(p -> {                                             // 2) Si existe...
                    p.setNombre(datos.getNombre());                     //    copiamos el nombre nuevo
                    p.setPrecio(datos.getPrecio());                     //    copiamos el precio nuevo
                    return ResponseEntity.ok(repo.save(p));             //    save con id existente = UPDATE -> 200 OK
                })
                .orElse(ResponseEntity.notFound().build());             // 3) Si no existe -> 404 Not Found
    }

    // ===================== DELETE: eliminar =====================
    @DeleteMapping("/eliminar/{id}")                         // DELETE /api/productos/eliminar/1
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { // Void: la respuesta no lleva cuerpo
        if (!repo.existsById(id)) {                          // Si no existe ese id...
            return ResponseEntity.notFound().build();        // ...404 Not Found
        }
        repo.deleteById(id);                                 // DELETE FROM productos WHERE id = ?
        return ResponseEntity.noContent().build();           // 204 No Content: borrado OK, sin cuerpo
    }
}
