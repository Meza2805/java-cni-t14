package com.example.demo.cliente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;      // Para escribir consultas JPQL a mano
import org.springframework.data.repository.query.Param;   // Une el parametro :categoriaId con el argumento del metodo

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

    // ===================== JPQL: doble JOIN (atraviesa las 3 entidades) =====================
    // Clientes que tienen como favorito algun producto de una categoria.
    //   Cliente --(favoritos @ManyToMany)--> Producto --(categoria @ManyToOne)--> Categoria
    // DISTINCT: si un cliente tiene 2 favoritos de esa categoria, aparece una sola vez.
    //
    // SQL equivalente (3 JOIN: incluye la tabla intermedia):
    //   SELECT DISTINCT cl.* FROM clientes cl
    //   JOIN cliente_favoritos cf ON cf.cliente_id = cl.id
    //   JOIN productos p          ON p.id = cf.producto_id
    //   JOIN categorias c         ON c.id = p.categoria_id
    //   WHERE c.id = ?
    @Query("""
            SELECT DISTINCT cl FROM Cliente cl
            JOIN cl.favoritos p
            JOIN p.categoria c
            WHERE c.id = :categoriaId
            """)
    List<Cliente> buscarPorCategoriaFavorita(@Param("categoriaId") Long categoriaId);

    // ===================== JPQL: LEFT JOIN FETCH (evita el problema N+1) =====================
    // Con LAZY, /listar hace 1 consulta para los clientes + 1 consulta POR CADA cliente
    // para leer sus favoritos (y otras para las categorias): el problema "N+1".
    // JOIN FETCH trae clientes, favoritos y la categoria de cada favorito en UNA sola consulta.
    // LEFT: incluye tambien a los clientes sin favoritos y a los productos sin categoria.
    @Query("""
            SELECT DISTINCT cl FROM Cliente cl
            LEFT JOIN FETCH cl.favoritos p
            LEFT JOIN FETCH p.categoria
            ORDER BY cl.id
            """)
    List<Cliente> listarConFavoritos();
}
