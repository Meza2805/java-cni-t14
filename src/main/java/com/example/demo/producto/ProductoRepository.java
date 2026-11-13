package com.example.demo.producto;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

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
}
