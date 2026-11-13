package com.example.demo.categoria;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

// JpaRepository<Entidad, TipoDelId>: Spring genera save, findAll, findById, deleteById, etc.
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Consulta derivada del nombre del metodo: SELECT count(*) > 0 FROM categorias WHERE nombre = ?
    boolean existsByNombre(String nombre);

    // SELECT * FROM categorias WHERE nombre = ?  (Optional: puede no existir)
    Optional<Categoria> findByNombre(String nombre);

    // ===================== JPQL: LEFT JOIN + GROUP BY =====================
    // JPQL se escribe con CLASES y ATRIBUTOS de Java (Categoria, c.productos), no con tablas.
    // "LEFT JOIN c.productos p" usa la relacion @OneToMany: no hace falta escribir el ON,
    // JPA ya sabe que se une por productos.categoria_id = categorias.id.
    // LEFT JOIN: incluye tambien las categorias SIN productos (con total 0).
    // "SELECT new ...": crea un objeto CategoriaResumen por cada fila del resultado.
    //
    // SQL equivalente:
    //   SELECT c.nombre, COUNT(p.id), AVG(p.precio)
    //   FROM categorias c LEFT JOIN productos p ON p.categoria_id = c.id
    //   GROUP BY c.id, c.nombre
    //   ORDER BY COUNT(p.id) DESC
    @Query("""
            SELECT new com.example.demo.categoria.CategoriaResumen(c.nombre, COUNT(p), AVG(p.precio))
            FROM Categoria c
            LEFT JOIN c.productos p
            GROUP BY c.id, c.nombre
            ORDER BY COUNT(p) DESC
            """)
    List<CategoriaResumen> resumen();
}
