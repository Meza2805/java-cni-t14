package com.example.demo.proveedor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

// JpaRepository<Entidad, TipoDelId>: Spring genera save, findAll, findById, deleteById, etc.
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    // Consulta derivada: SELECT count(*) > 0 FROM proveedores WHERE email = ?
    boolean existsByEmail(String email);

    // SELECT * FROM proveedores WHERE email = ?  (Optional: puede no existir)
    Optional<Proveedor> findByEmail(String email);

    // Cada proveedor CON sus productos en UNA sola consulta (para la vista; evita N+1)
    @Query("""
            SELECT DISTINCT pr FROM Proveedor pr
            LEFT JOIN FETCH pr.productos
            ORDER BY pr.id
            """)
    List<Proveedor> listarConProductos();

    // Reporte: cuantos productos surte cada proveedor (LEFT JOIN + GROUP BY + DTO)
    //   SQL equivalente:
    //   SELECT pr.nombre, COUNT(p.id) FROM proveedores pr
    //   LEFT JOIN productos p ON p.proveedor_id = pr.id
    //   GROUP BY pr.id, pr.nombre
    @Query("""
            SELECT new com.example.demo.proveedor.ProveedorResumen(pr.nombre, COUNT(p))
            FROM Proveedor pr
            LEFT JOIN pr.productos p
            GROUP BY pr.id, pr.nombre
            ORDER BY COUNT(p) DESC
            """)
    List<ProveedorResumen> resumen();
}
