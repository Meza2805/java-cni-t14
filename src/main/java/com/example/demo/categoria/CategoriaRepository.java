package com.example.demo.categoria;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// JpaRepository<Entidad, TipoDelId>: Spring genera save, findAll, findById, deleteById, etc.
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Consulta derivada del nombre del metodo: SELECT count(*) > 0 FROM categorias WHERE nombre = ?
    boolean existsByNombre(String nombre);

    // SELECT * FROM categorias WHERE nombre = ?  (Optional: puede no existir)
    Optional<Categoria> findByNombre(String nombre);
}
