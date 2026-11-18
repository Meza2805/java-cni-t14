// Paquete donde vive la clase (debe coincidir con la carpeta)
package com.example.demo.proveedor;

// Entidad del otro lado de la relacion
import com.example.demo.producto.Producto;
// Evita que el JSON entre en un ciclo infinito (proveedor -> productos -> proveedor -> ...)
import com.fasterxml.jackson.annotation.JsonIgnore;
// Anotaciones de JPA: @Entity, @Table, @Id, @Column, @GeneratedValue, @OneToMany...
import jakarta.persistence.*;
// Validaciones: se revisan cuando el controller usa @Valid
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// Lombok: genera codigo repetitivo al compilar
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList; // Implementacion de la lista de productos
import java.util.List;      // Lista de productos que surte el proveedor

// =====================================================================
//  PRACTICA: entidad Proveedor (hecha copiando el modelo de Categoria)
// =====================================================================
@Entity                       // Esta clase es una ENTIDAD: JPA la mapea a una tabla
@Table(name = "proveedores")  // Nombre de la tabla en la BD (Hibernate la crea sola con ddl-auto=update)
@Data                         // Lombok: getters, setters, toString, equals y hashCode
@NoArgsConstructor            // Lombok: constructor vacio (obligatorio para JPA)
public class Proveedor {

    @Id                                                 // Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autoincremental: la BD asigna 1, 2, 3...
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")                       // No puede venir vacio
    @Size(max = 80, message = "El nombre no puede superar 80 caracteres") // Largo maximo
    @Column(nullable = false, length = 80)                                // NOT NULL y VARCHAR(80)
    private String nombre;

    @NotBlank(message = "El email es obligatorio")                // No puede venir vacio
    @Email(message = "El email no tiene un formato valido")       // algo@dominio.com
    @Column(nullable = false, unique = true)                      // NOT NULL y UNIQUE: no se repite
    private String email;

    @Size(max = 20, message = "El telefono no puede superar 20 caracteres") // Opcional, con largo maximo
    @Column(length = 20)                                                     // VARCHAR(20), admite NULL
    private String telefono;

    // ===================== RELACION: un proveedor -> muchos productos =====================
    // Igual que en Categoria: es el lado INVERSO. La clave foranea (proveedor_id) esta en Producto.
    @OneToMany(mappedBy = "proveedor")
    @JsonIgnore                 // No se envia en el JSON (evita el ciclo)
    @ToString.Exclude           // Lombok: no incluirla en toString()
    @EqualsAndHashCode.Exclude  // Lombok: no incluirla en equals()/hashCode()
    private List<Producto> productos = new ArrayList<>();

    // Constructor para crear proveedores sin productos: new Proveedor(null, "TecnoMax", "ventas@...", "22220000")
    public Proveedor(Long id, String nombre, String email, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }
}
