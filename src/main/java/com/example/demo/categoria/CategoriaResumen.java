package com.example.demo.categoria;

// DTO (Data Transfer Object): NO es una entidad, no tiene tabla.
// Solo sirve para devolver el resultado de una consulta con GROUP BY.
// Un "record" de Java crea solo: constructor, getters, equals, hashCode y toString.
public record CategoriaResumen(
        String categoria,       // Nombre de la categoria
        Long totalProductos,    // COUNT(p)      -> cuantos productos tiene
        Double precioPromedio   // AVG(p.precio) -> null si la categoria no tiene productos
) {
}
