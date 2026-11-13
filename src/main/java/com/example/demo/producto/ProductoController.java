// Paquete donde vive la clase
package com.example.demo.producto;

import com.example.demo.categoria.Categoria;           // Entidad relacionada
import com.example.demo.categoria.CategoriaRepository; // Para buscar la categoria por id
import jakarta.validation.Valid;                       // Activa las validaciones de la entidad (@NotBlank, etc.)
import lombok.RequiredArgsConstructor;                 // Lombok: constructor para los campos "final"
import org.springframework.http.ResponseEntity;        // Respuesta HTTP completa: codigo de estado + cuerpo
import org.springframework.web.bind.annotation.*;      // @RestController, @GetMapping, @PostMapping...

import java.net.URI;   // Para armar la URL del recurso creado (cabecera "Location")
import java.util.List; // Lista de productos

@RestController                    // Clase que atiende peticiones HTTP y responde JSON automaticamente
@RequestMapping("/api/productos")  // Prefijo comun de TODAS las rutas de este controller
@RequiredArgsConstructor           // Genera el constructor -> Spring inyecta los repositorios ahi
public class ProductoController {

    private final ProductoRepository repo;               // Acceso a la tabla "productos"
    private final CategoriaRepository categoriaRepo;     // Acceso a la tabla "categorias" (para la relacion)

    // ===================== READ: listar todos =====================
    @GetMapping("/listar")                 // GET /api/productos/listar
    public List<Producto> listar() {
        return repo.findAll();             // SELECT * FROM productos (+ su categoria) -> arreglo JSON
    }

    // ===================== READ: obtener uno =====================
    @GetMapping("/obtener/{id}")                                     // GET /api/productos/obtener/1  ({id} es variable)
    public ResponseEntity<Producto> obtener(@PathVariable Long id) { // @PathVariable: toma el {id} de la URL
        return repo.findById(id)                                     // Busca por id -> devuelve Optional (puede venir vacio)
                .map(ResponseEntity::ok)                             // Si existe -> 200 OK con el producto
                .orElse(ResponseEntity.notFound().build());          // Si no existe -> 404 Not Found
    }

    // ===================== CREATE: insertar (con categoria opcional) =====================
    // JSON de ejemplo:  { "nombre": "Mouse", "precio": 15, "categoria": { "id": 1 } }
    // Si no se envia "categoria", el producto se crea sin categoria.
    @PostMapping("/crear")                                                // POST /api/productos/crear
    public ResponseEntity<?> crear(@Valid @RequestBody Producto producto) { // <?>: devuelve Producto o un mensaje de error
        producto.setId(null);                                             // Ignoramos cualquier id enviado: lo genera la BD

        if (producto.getCategoria() != null) {                            // Si el JSON trae una categoria...
            Long categoriaId = producto.getCategoria().getId();           // ...tomamos solo su id
            Categoria categoria = (categoriaId == null) ? null : categoriaRepo.findById(categoriaId).orElse(null);
            if (categoria == null) {                                      // Si no existe esa categoria -> 400
                return ResponseEntity.badRequest().body("No existe la categoria con id: " + categoriaId);
            }
            producto.setCategoria(categoria);                             // Enlazamos el producto con la categoria REAL de la BD
        }

        Producto guardado = repo.save(producto);                          // INSERT INTO productos (..., categoria_id) VALUES (..., ?)
        return ResponseEntity
                .created(URI.create("/api/productos/obtener/" + guardado.getId())) // 201 Created + cabecera Location
                .body(guardado);                                                    // Cuerpo: el producto guardado en JSON
    }

    // ===================== UPDATE: actualizar =====================
    // Solo cambia nombre y precio. La categoria se cambia con los endpoints de RELACION (abajo).
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

    // =====================================================================
    //                RELACION  Producto (muchos) -> Categoria (una)
    // =====================================================================

    // ===================== Asignar (o cambiar) la categoria de un producto =====================
    @PutMapping("/{id}/categoria/{categoriaId}")               // PUT /api/productos/1/categoria/2
    public ResponseEntity<?> asignarCategoria(@PathVariable Long id,
                                              @PathVariable Long categoriaId) {
        Producto producto = repo.findById(id).orElse(null);                  // Buscamos el producto
        if (producto == null) {
            return ResponseEntity.status(404).body("No existe el producto con id: " + id);
        }
        Categoria categoria = categoriaRepo.findById(categoriaId).orElse(null); // Buscamos la categoria
        if (categoria == null) {
            return ResponseEntity.status(404).body("No existe la categoria con id: " + categoriaId);
        }
        producto.setCategoria(categoria);              // Enlazamos los dos objetos en Java...
        return ResponseEntity.ok(repo.save(producto)); // ...y JPA hace: UPDATE productos SET categoria_id = ? WHERE id = ?
    }

    // ===================== Quitar la categoria de un producto =====================
    @DeleteMapping("/{id}/categoria")                          // DELETE /api/productos/1/categoria
    public ResponseEntity<Producto> quitarCategoria(@PathVariable Long id) {
        return repo.findById(id)
                .map(p -> {
                    p.setCategoria(null);                      // UPDATE productos SET categoria_id = NULL WHERE id = ?
                    return ResponseEntity.ok(repo.save(p));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ===================== Productos sin categoria =====================
    @GetMapping("/sin-categoria")                              // GET /api/productos/sin-categoria
    public List<Producto> sinCategoria() {
        return repo.findByCategoriaIsNull();                   // SELECT * FROM productos WHERE categoria_id IS NULL
    }
}
