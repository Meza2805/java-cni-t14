// Paquete donde vive la clase (debe coincidir con la carpeta)
package com.example.demo.config;

import com.example.demo.categoria.Categoria;            // Entidad Categoria
import com.example.demo.categoria.CategoriaRepository;  // Acceso a la tabla "categorias"
import com.example.demo.cliente.Cliente;                // Entidad Cliente
import com.example.demo.cliente.ClienteRepository;      // Acceso a la tabla "clientes"
import org.springframework.boot.CommandLineRunner;      // Codigo que se ejecuta al arrancar la app
import org.springframework.context.annotation.Bean;     // Registra un objeto administrado por Spring
import org.springframework.context.annotation.Configuration; // Clase de configuracion

import java.util.List; // Para crear listas de datos

@Configuration // Spring lee esta clase al arrancar y registra sus @Bean
public class DatosIniciales {

    // ===================== Datos de ejemplo: CATEGORIAS =====================
    @Bean // Spring ejecuta este CommandLineRunner una vez, justo despues de arrancar
    CommandLineRunner cargarCategorias(CategoriaRepository repo) { // Spring inyecta el repositorio
        return args -> {
            if (repo.count() > 0) {  // SELECT count(*) FROM categorias
                return;              // Si ya hay datos, no insertamos nada (evita duplicados)
            }
            repo.saveAll(List.of(    // INSERT de varias filas a la vez
                    new Categoria(null, "Electronica", "Computadoras, celulares y accesorios"), // id null: lo genera la BD
                    new Categoria(null, "Hogar", "Muebles, cocina y decoracion"),
                    new Categoria(null, "Ropa", "Prendas de vestir y calzado"),
                    new Categoria(null, "Deportes", "Articulos deportivos y fitness"),
                    new Categoria(null, "Libros", "Libros fisicos y digitales")
            ));
            System.out.println(">>> Categorias de ejemplo insertadas: " + repo.count());
        };
    }

    // ===================== Datos de ejemplo: CLIENTES =====================
    @Bean
    CommandLineRunner cargarClientes(ClienteRepository repo) {
        return args -> {
            if (repo.count() > 0) {  // SELECT count(*) FROM clientes
                return;              // Ya hay datos: no hacemos nada
            }
            repo.saveAll(List.of(    // Orden de campos: id, nombre, email, edad, activo
                    new Cliente(null, "Ana Lopez", "ana.lopez@correo.com", 25, true),
                    new Cliente(null, "Carlos Perez", "carlos.perez@correo.com", 34, true),
                    new Cliente(null, "Maria Gonzalez", "maria.gonzalez@correo.com", 41, true),
                    new Cliente(null, "Jose Martinez", "jose.martinez@correo.com", 29, false), // Inactivo: no sale en /activos
                    new Cliente(null, "Lucia Ramirez", "lucia.ramirez@correo.com", 52, true)
            ));
            System.out.println(">>> Clientes de ejemplo insertados: " + repo.count());
        };
    }
}
