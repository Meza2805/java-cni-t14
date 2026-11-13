// Paquete donde vive la clase
package com.example.demo.cliente;

import com.example.demo.producto.Producto;           // Entidad relacionada
import com.example.demo.producto.ProductoRepository; // Para buscar el producto por id
import jakarta.validation.Valid;                   // Activa las validaciones de la entidad
import lombok.RequiredArgsConstructor;             // Lombok: constructor para los campos "final"
import org.springframework.http.HttpStatus;        // Codigos HTTP con nombre (CONFLICT = 409)
import org.springframework.http.ResponseEntity;    // Respuesta HTTP: codigo de estado + cuerpo
import org.springframework.web.bind.annotation.*;  // @RestController, @GetMapping, @PostMapping...

import java.net.URI;   // Para la cabecera "Location" del recurso creado
import java.util.List; // Lista de clientes
import java.util.Set;  // Conjunto de productos favoritos

@RestController                   // Atiende peticiones HTTP y responde JSON
@RequestMapping("/api/clientes")  // Prefijo comun de todas las rutas de este controller
@RequiredArgsConstructor          // Spring inyecta el repositorio por constructor
public class ClienteController {

    private final ClienteRepository repo;            // Acceso a la tabla "clientes"
    private final ProductoRepository productoRepo;   // Acceso a la tabla "productos" (para la relacion)

    // ===================== READ: listar todos =====================
    @GetMapping("/listar")            // GET /api/clientes/listar
    public List<Cliente> listar() {
        return repo.findAll();        // SELECT * FROM clientes
    }

    // ===================== READ: listar solo activos (consulta personalizada) =====================
    @GetMapping("/activos")              // GET /api/clientes/activos
    public List<Cliente> listarActivos() {
        return repo.findByActivoTrue();  // SELECT * FROM clientes WHERE activo = true
    }

    // ===================== READ: obtener uno =====================
    @GetMapping("/obtener/{id}")                                    // GET /api/clientes/obtener/1
    public ResponseEntity<Cliente> obtener(@PathVariable Long id) { // Toma el {id} de la URL
        return repo.findById(id)                                    // Busca por id (Optional)
                .map(ResponseEntity::ok)                            // Existe -> 200 OK
                .orElse(ResponseEntity.notFound().build());         // No existe -> 404
    }

    // ===================== CREATE: insertar =====================
    @PostMapping("/crear")                                              // POST /api/clientes/crear
    public ResponseEntity<?> crear(@Valid @RequestBody Cliente cliente) { // <?>: devuelve Cliente o mensaje de error
        if (repo.existsByEmail(cliente.getEmail())) {                   // El email es UNIQUE: revisamos antes de insertar
            return ResponseEntity.status(HttpStatus.CONFLICT)           // 409 Conflict
                    .body("Ya existe un cliente con el email: " + cliente.getEmail());
        }
        cliente.setId(null);                                            // El id lo genera la BD
        if (cliente.getActivo() == null) {                              // Si el JSON envio "activo": null...
            cliente.setActivo(true);                                    // ...lo dejamos activo por defecto
        }
        Cliente guardado = repo.save(cliente);                          // INSERT INTO clientes ...
        return ResponseEntity
                .created(URI.create("/api/clientes/obtener/" + guardado.getId())) // 201 Created + Location
                .body(guardado);                                                   // Devuelve el cliente creado
    }

    // ===================== UPDATE: actualizar =====================
    @PutMapping("/actualizar/{id}")                                    // PUT /api/clientes/actualizar/1
    public ResponseEntity<Cliente> actualizar(@PathVariable Long id,   // id de la URL
                                              @Valid @RequestBody Cliente datos) { // datos nuevos del JSON
        return repo.findById(id)                                       // Buscamos el cliente existente
                .map(c -> {                                            // Si existe...
                    c.setNombre(datos.getNombre());                    //   nuevo nombre
                    c.setEmail(datos.getEmail());                      //   nuevo email
                    c.setEdad(datos.getEdad());                        //   nueva edad
                    if (datos.getActivo() != null) {                   //   solo cambiamos "activo" si lo enviaron
                        c.setActivo(datos.getActivo());
                    }
                    return ResponseEntity.ok(repo.save(c));            //   UPDATE -> 200 OK
                })
                .orElse(ResponseEntity.notFound().build());            // Si no existe -> 404
    }

    // ===================== DELETE: eliminar =====================
    @DeleteMapping("/eliminar/{id}")                              // DELETE /api/clientes/eliminar/1
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { // Sin cuerpo en la respuesta
        if (!repo.existsById(id)) {                               // Si no existe...
            return ResponseEntity.notFound().build();             // ...404
        }
        // Cliente es el DUENO de la relacion @ManyToMany: al borrarlo, JPA borra primero
        // sus filas en la tabla intermedia (DELETE FROM cliente_favoritos WHERE cliente_id = ?)
        repo.deleteById(id);                                      // DELETE FROM clientes WHERE id = ?
        return ResponseEntity.noContent().build();                // 204 No Content
    }

    // =====================================================================
    //          RELACION  Cliente (muchos) <-> Producto (muchos): FAVORITOS
    // =====================================================================

    // ===================== Ver los favoritos de un cliente =====================
    @GetMapping("/{id}/favoritos")                                         // GET /api/clientes/1/favoritos
    public ResponseEntity<Set<Producto>> favoritos(@PathVariable Long id) {
        return repo.findById(id)
                .map(c -> ResponseEntity.ok(c.getFavoritos()))             // Al usar la lista, JPA hace el JOIN con cliente_favoritos
                .orElse(ResponseEntity.notFound().build());
    }

    // ===================== Agregar un producto a favoritos =====================
    @PostMapping("/{id}/favoritos/{productoId}")                           // POST /api/clientes/1/favoritos/3
    public ResponseEntity<?> agregarFavorito(@PathVariable Long id,
                                             @PathVariable Long productoId) {
        Cliente cliente = repo.findById(id).orElse(null);                  // Buscamos el cliente
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe el cliente con id: " + id);
        }
        Producto producto = productoRepo.findById(productoId).orElse(null); // Buscamos el producto
        if (producto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe el producto con id: " + productoId);
        }
        if (!cliente.getFavoritos().add(producto)) {                       // add() devuelve false si ya estaba (es un Set)
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El producto ya esta en los favoritos del cliente");
        }
        repo.save(cliente);                                                // INSERT INTO cliente_favoritos (cliente_id, producto_id) VALUES (?, ?)
        return ResponseEntity.ok(cliente.getFavoritos());                  // 200 OK con la lista actualizada
    }

    // ===================== Quitar un producto de favoritos =====================
    @DeleteMapping("/{id}/favoritos/{productoId}")                         // DELETE /api/clientes/1/favoritos/3
    public ResponseEntity<?> quitarFavorito(@PathVariable Long id,
                                            @PathVariable Long productoId) {
        Cliente cliente = repo.findById(id).orElse(null);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe el cliente con id: " + id);
        }
        boolean quitado = cliente.getFavoritos()                           // Quitamos el producto de la lista en Java...
                .removeIf(p -> p.getId().equals(productoId));
        if (!quitado) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ese producto no esta en los favoritos del cliente");
        }
        repo.save(cliente);                                                // ...y JPA hace: DELETE FROM cliente_favoritos WHERE ...
        return ResponseEntity.noContent().build();                         // 204 No Content
    }
}
