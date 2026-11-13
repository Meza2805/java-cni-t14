// Paquete donde vive la clase (debe coincidir con la carpeta)
package com.example.demo.config;

import com.example.demo.categoria.Categoria;            // Entidad Categoria
import com.example.demo.categoria.CategoriaRepository;  // Acceso a la tabla "categorias"
import com.example.demo.cliente.Cliente;                // Entidad Cliente
import com.example.demo.cliente.ClienteRepository;      // Acceso a la tabla "clientes"
import com.example.demo.producto.Producto;              // Entidad Producto
import com.example.demo.producto.ProductoRepository;    // Acceso a la tabla "productos"
import org.springframework.boot.CommandLineRunner;      // Codigo que se ejecuta al arrancar la app
import org.springframework.context.annotation.Bean;     // Registra un objeto administrado por Spring
import org.springframework.context.annotation.Configuration; // Clase de configuracion
import org.springframework.transaction.support.TransactionTemplate; // Ejecuta codigo dentro de una transaccion

import java.util.List; // Para crear listas de datos

@Configuration // Spring lee esta clase al arrancar y registra sus @Bean
public class DatosIniciales {

    // Un solo CommandLineRunner para garantizar el ORDEN:
    // primero categorias, luego clientes y al final productos (que necesitan las categorias).
    // Cada tabla solo se llena si esta vacia: asi no se duplican datos en cada arranque.
    @Bean
    CommandLineRunner cargarDatos(CategoriaRepository categoriaRepo,
                                  ClienteRepository clienteRepo,
                                  ProductoRepository productoRepo,
                                  TransactionTemplate tx) {
        return args -> {
            cargarCategorias(categoriaRepo);
            cargarClientes(clienteRepo);
            cargarProductos(productoRepo, categoriaRepo);
            // Los favoritos son LAZY: para leerlos y modificarlos la sesion de JPA debe seguir abierta.
            // tx.executeWithoutResult(...) ejecuta el bloque dentro de UNA transaccion.
            tx.executeWithoutResult(estado -> cargarFavoritos(clienteRepo, productoRepo));
        };
    }

    // ===================== Datos de ejemplo: CATEGORIAS =====================
    private void cargarCategorias(CategoriaRepository repo) {
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
    }

    // ===================== Datos de ejemplo: CLIENTES =====================
    private void cargarClientes(ClienteRepository repo) {
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
    }

    // ===================== Datos de ejemplo: PRODUCTOS (con su categoria) =====================
    private void cargarProductos(ProductoRepository repo, CategoriaRepository categoriaRepo) {
        if (repo.count() > 0) {  // SELECT count(*) FROM productos
            return;
        }
        // Buscamos las categorias por nombre (si alguna no existe, el producto queda sin categoria)
        Categoria electronica = categoriaRepo.findByNombre("Electronica").orElse(null);
        Categoria hogar = categoriaRepo.findByNombre("Hogar").orElse(null);
        Categoria deportes = categoriaRepo.findByNombre("Deportes").orElse(null);
        Categoria libros = categoriaRepo.findByNombre("Libros").orElse(null);

        repo.saveAll(List.of(    // Orden de campos: id, nombre, precio, categoria
                new Producto(null, "Laptop", 850.0, electronica),       // categoria_id -> Electronica
                new Producto(null, "Audifonos", 45.0, electronica),
                new Producto(null, "Licuadora", 60.0, hogar),
                new Producto(null, "Balon de futbol", 25.0, deportes),
                new Producto(null, "Pesas 5 kg", 30.0, deportes),
                new Producto(null, "Clean Code", 40.0, libros),
                new Producto(null, "Producto sin categoria", 10.0, null) // categoria_id = NULL
        ));
        System.out.println(">>> Productos de ejemplo insertados: " + repo.count());
    }

    // ===================== Datos de ejemplo: FAVORITOS (relacion muchos a muchos) =====================
    private void cargarFavoritos(ClienteRepository clienteRepo, ProductoRepository productoRepo) {
        if (clienteRepo.existsByFavoritosIsNotEmpty()) { // Si algun cliente ya tiene favoritos, no hacemos nada
            return;
        }
        agregarFavoritos(clienteRepo, productoRepo, "ana.lopez@correo.com", "Laptop", "Audifonos", "Clean Code");
        agregarFavoritos(clienteRepo, productoRepo, "carlos.perez@correo.com", "Balon de futbol", "Pesas 5 kg");
        agregarFavoritos(clienteRepo, productoRepo, "maria.gonzalez@correo.com", "Laptop", "Licuadora");
        agregarFavoritos(clienteRepo, productoRepo, "lucia.ramirez@correo.com", "Clean Code");
        System.out.println(">>> Favoritos de ejemplo insertados");
    }

    // Busca el cliente por email y le agrega los productos indicados por nombre (si existen)
    private void agregarFavoritos(ClienteRepository clienteRepo, ProductoRepository productoRepo,
                                  String email, String... nombresProductos) {
        clienteRepo.findByEmail(email)                      // SELECT * FROM clientes WHERE email = ?
                .ifPresent(cliente -> {
                    for (String nombre : nombresProductos) {
                        productoRepo.findFirstByNombre(nombre)
                                .ifPresent(cliente.getFavoritos()::add); // Agrega a la lista en Java
                    }
                    clienteRepo.save(cliente);              // INSERT INTO cliente_favoritos ... (una fila por producto)
                });
    }
}
