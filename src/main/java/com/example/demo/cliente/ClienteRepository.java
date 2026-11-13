package com.example.demo.cliente;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// JpaRepository<Entidad, TipoDelId>: Spring genera save, findAll, findById, deleteById, etc.
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Consulta derivada: SELECT count(*) > 0 FROM clientes WHERE email = ?
    boolean existsByEmail(String email);

    // SELECT * FROM clientes WHERE email = ?  (Optional: puede no existir)
    Optional<Cliente> findByEmail(String email);

    // Consulta derivada: SELECT * FROM clientes WHERE activo = true
    List<Cliente> findByActivoTrue();

    // Consulta derivada que atraviesa la relacion @ManyToMany: Favoritos -> Id
    // Spring genera un JOIN con la tabla intermedia:
    //   SELECT c.* FROM clientes c
    //   JOIN cliente_favoritos cf ON cf.cliente_id = c.id
    //   WHERE cf.producto_id = ?
    List<Cliente> findByFavoritosId(Long productoId);

    // ¿Algun cliente tiene al menos un favorito?  (se usa al cargar los datos de ejemplo)
    boolean existsByFavoritosIsNotEmpty();
}
