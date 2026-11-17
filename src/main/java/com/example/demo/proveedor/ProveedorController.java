// Paquete donde vive la clase
package com.example.demo.proveedor;

import com.example.demo.producto.Producto;
import com.example.demo.producto.ProductoRepository;
import jakarta.validation.Valid;                   // Activa las validaciones de la entidad
import lombok.RequiredArgsConstructor;             // Lombok: constructor para los campos "final"
import org.springframework.http.HttpStatus;        // Codigos HTTP con nombre (CONFLICT = 409)
import org.springframework.http.ResponseEntity;    // Respuesta HTTP: codigo de estado + cuerpo
import org.springframework.web.bind.annotation.*;  // @RestController, @GetMapping, @PostMapping...

import java.net.URI;
import java.util.List;
import java.util.Objects;

// =====================================================================
//  PRACTICA: API REST de proveedores (modelo: CategoriaController)
// =====================================================================
@RestController                      // Atiende peticiones HTTP y responde JSON
@RequestMapping("/api/proveedores")  // Prefijo comun de todas las rutas
@RequiredArgsConstructor             // Spring inyecta los repositorios por constructor
public class ProveedorController {

    private final ProveedorRepository repo;          // Tabla "proveedores"
    private final ProductoRepository productoRepo;   // Tabla "productos" (para la relacion)

    // ===================== READ: listar todos =====================
    @GetMapping("/listar")                 // GET /api/proveedores/listar
    public List<Proveedor> listar() {
        return repo.findAll();             // SELECT * FROM proveedores
    }

    // ===================== READ: obtener uno =====================
    @GetMapping("/obtener/{id}")           // GET /api/proveedores/obtener/1
    public ResponseEntity<Proveedor> obtener(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)                      // 200
                .orElse(ResponseEntity.notFound().build());   // 404
    }

    // ===================== CREATE: insertar =====================
    @PostMapping("/crear")                 // POST /api/proveedores/crear
    public ResponseEntity<?> crear(@Valid @RequestBody Proveedor proveedor) {
        if (repo.existsByEmail(proveedor.getEmail())) {       // El email es UNIQUE
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe un proveedor con el email: " + proveedor.getEmail());
        }
        proveedor.setId(null);                                // El id lo genera la BD
        Proveedor guardado = repo.save(proveedor);            // INSERT INTO proveedores ...
        return ResponseEntity
                .created(URI.create("/api/proveedores/obtener/" + guardado.getId())) // 201 + Location
                .body(guardado);
    }

    // ===================== UPDATE: actualizar =====================
    @PutMapping("/actualizar/{id}")        // PUT /api/proveedores/actualizar/1
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Proveedor datos) {
        Proveedor actual = repo.findById(id).orElse(null);
        if (actual == null) {
            return ResponseEntity.notFound().build();         // 404
        }
        // El email no puede pertenecer a OTRO proveedor
        boolean emailOcupado = repo.findByEmail(datos.getEmail())
                .filter(otro -> !Objects.equals(otro.getId(), id))
                .isPresent();
        if (emailOcupado) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe un proveedor con el email: " + datos.getEmail());
        }
        actual.setNombre(datos.getNombre());
        actual.setEmail(datos.getEmail());
        actual.setTelefono(datos.getTelefono());
        return ResponseEntity.ok(repo.save(actual));          // UPDATE -> 200
    }

    // ===================== DELETE: eliminar =====================
    @DeleteMapping("/eliminar/{id}")       // DELETE /api/proveedores/eliminar/1
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();         // 404
        }
        // Integridad referencial: no borrar un proveedor que tiene productos (igual que categorias)
        if (productoRepo.existsByProveedorId(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar: el proveedor tiene productos asignados");
        }
        repo.deleteById(id);                                  // DELETE FROM proveedores WHERE id = ?
        return ResponseEntity.noContent().build();            // 204
    }

    // ===================== Productos de un proveedor =====================
    @GetMapping("/{id}/productos")         // GET /api/proveedores/1/productos
    public ResponseEntity<List<Producto>> productos(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productoRepo.findByProveedorId(id)); // SELECT * FROM productos WHERE proveedor_id = ?
    }
}
