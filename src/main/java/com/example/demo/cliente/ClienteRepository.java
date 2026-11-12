package com.example.demo.cliente;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// JpaRepository<Entidad, TipoDelId>: Spring genera save, findAll, findById, deleteById, etc.
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Consulta derivada: SELECT count(*) > 0 FROM clientes WHERE email = ?
    boolean existsByEmail(String email);

    // Consulta derivada: SELECT * FROM clientes WHERE activo = true
    List<Cliente> findByActivoTrue();
}
