// Paquete donde vive la clase
package com.example.demo.categoria;

import com.example.demo.producto.Producto;           // Entidad relacionada
import com.example.demo.producto.ProductoRepository; // Para consultar los productos de una categoria
import jakarta.validation.Valid;                 // Activa las validaciones de la entidad
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

    private final CategoriaRepository repo;          // Acceso a la tabla "categorias"
    private final ProductoRepository productoRepo;   // Acceso a la tabla "productos" (para la relacion)

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
    public ResponseEntity<?> eliminar(@PathVariable Long id) {    // <?>: sin cuerpo o con mensaje de error
        if (!repo.existsById(id)) {                               // Si no existe...
            return ResponseEntity.notFound().build();             // ...404
        }
        // INTEGRIDAD REFERENCIAL: si hay productos que apuntan a esta categoria (categoria_id = id),
        // borrarla dejaria esas claves foraneas "huerfanas". MySQL y SQL Server lo impiden con un error;
        // SQLite, por defecto, no revisa las FK. Por eso lo validamos aqui y respondemos 409.
        if (productoRepo.existsByCategoriaId(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar: la categoria tiene productos. Quitalos o cambialos de categoria primero.");
        }
        repo.deleteById(id);                                      // DELETE FROM categorias WHERE id = ?
        return ResponseEntity.noContent().build();                // 204 No Content
    }

    // =====================================================================
    //                RELACION  Categoria (una) -> Productos (muchos)
    // =====================================================================

    // ===================== Productos de una categoria =====================
    @GetMapping("/{id}/productos")                                          // GET /api/categorias/1/productos
    public ResponseEntity<List<Producto>> productos(@PathVariable Long id) {
        if (!repo.existsById(id)) {                                         // Si la categoria no existe -> 404
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productoRepo.findByCategoriaId(id));      // SELECT * FROM productos WHERE categoria_id = ?
    }
}
