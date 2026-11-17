package com.example.demo.proveedor;

// DTO para el reporte: cuantos productos surte cada proveedor (igual que CategoriaResumen)
public record ProveedorResumen(
        String proveedor,      // Nombre del proveedor
        Long totalProductos    // COUNT(p) -> 0 si no surte ninguno (LEFT JOIN)
) {
}
