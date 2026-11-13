package com.example.demo.producto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;      // Para escribir consultas JPQL a mano
import org.springframework.data.repository.query.Param;   // Une el parametro :nombre con el argumento del metodo

import java.util.List;
import java.util.Optional;

// JpaRepository ya trae save, findAll, findById, deleteById, etc.
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Consulta derivada que "navega" la relacion: Categoria -> Id
    // SELECT * FROM productos WHERE categoria_id = ?
    List<Producto> findByCategoriaId(Long categoriaId);

    // Productos que no tienen categoria asignada
    // SELECT * FROM productos WHERE categoria_id IS NULL
    List<Producto> findByCategoriaIsNull();

    // SELECT count(*) > 0 FROM productos WHERE categoria_id = ?
    boolean existsByCategoriaId(Long categoriaId);

    // SELECT * FROM productos WHERE nombre = ? LIMIT 1  (se usa al cargar los datos de ejemplo)
    Optional<Producto> findFirstByNombre(String nombre);

    // ===================== JPQL: INNER JOIN =====================
    // "JOIN p.categoria c" recorre la relacion @ManyToOne de Producto.
    // INNER JOIN: los productos SIN categoria no aparecen.
    // :nombre es un PARAMETRO con nombre; se llena con el argumento del metodo (evita inyeccion SQL).
    //
    // SQL equivalente:
    //   SELECT p.* FROM productos p
    //   JOIN categorias c ON c.id = p.categoria_id
    //   WHERE LOWER(c.nombre) = LOWER(?)
    //   ORDER BY p.precio
    @Query("""
            SELECT p FROM Producto p
            JOIN p.categoria c
            WHERE LOWER(c.nombre) = LOWER(:nombre)
            ORDER BY p.precio
            """)
    List<Producto> buscarPorNombreDeCategoria(@Param("nombre") String nombre);

    // ===================== JPQL: JOIN + condicion sobre las dos tablas =====================
    // Productos de una categoria con precio dentro de un rango.
    @Query("""
            SELECT p FROM Producto p
            JOIN p.categoria c
            WHERE c.id = :categoriaId
              AND p.precio BETWEEN :min AND :max
            ORDER BY p.precio
            """)
    List<Producto> buscarPorCategoriaYPrecio(@Param("categoriaId") Long categoriaId,
                                             @Param("min") Double min,
                                             @Param("max") Double max);
}
