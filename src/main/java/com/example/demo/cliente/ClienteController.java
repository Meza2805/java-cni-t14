// Paquete donde vive la clase
package com.example.demo.cliente;

import jakarta.validation.Valid;                   // Activa las validaciones de la entidad
import lombok.RequiredArgsConstructor;             // Lombok: constructor para los campos "final"
import org.springframework.http.HttpStatus;        // Codigos HTTP con nombre (CONFLICT = 409)
import org.springframework.http.ResponseEntity;    // Respuesta HTTP: codigo de estado + cuerpo
import org.springframework.web.bind.annotation.*;  // @RestController, @GetMapping, @PostMapping...

import java.net.URI;   // Para la cabecera "Location" del recurso creado
import java.util.List; // Lista de clientes

@RestController                   // Atiende peticiones HTTP y responde JSON
@RequestMapping("/api/clientes")  // Prefijo comun de todas las rutas de este controller
@RequiredArgsConstructor          // Spring inyecta el repositorio por constructor
public class ClienteController {

    private final ClienteRepository repo; // Acceso a la tabla "clientes"

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
        repo.deleteById(id);                                      // DELETE FROM clientes WHERE id = ?
        return ResponseEntity.noContent().build();                // 204 No Content
    }
}
