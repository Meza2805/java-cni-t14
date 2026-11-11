package com.example.demo.producto;

import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository ya trae save, findAll, findById, deleteById, etc.
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
