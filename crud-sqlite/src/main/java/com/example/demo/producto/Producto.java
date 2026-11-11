// Paquete donde vive la clase (debe coincidir con la carpeta)
package com.example.demo.producto;

// Anotaciones de JPA: @Entity, @Table, @Id, @Column, @GeneratedValue...
import jakarta.persistence.*;
// Validaciones: se revisan cuando el controller usa @Valid
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
// Lombok: genera codigo repetitivo al compilar
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity                    // Esta clase es una ENTIDAD: JPA la mapea a una tabla de la BD (ORM)
@Table(name = "productos") // Nombre de la tabla. Sin esta anotacion la tabla se llamaria "producto"
@Data                      // Lombok: genera getters, setters, toString, equals y hashCode
@NoArgsConstructor         // Lombok: constructor vacio -> new Producto(). JPA lo necesita SI o SI
@AllArgsConstructor        // Lombok: constructor con todos los campos -> new Producto(id, nombre, precio)
public class Producto {

    @Id                                                 // Este campo es la CLAVE PRIMARIA (PRIMARY KEY)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // La BD genera el id sola: IDENTITY (1,1), AUTO_INCREMENT, SEQUENCE
    private Long id;                                    // Long (no long) para que pueda ser null antes de guardarse

    @NotBlank(message = "El nombre es obligatorio") // Validacion: no puede ser null, "" ni "   "
    @Column(nullable = false)                       // En la tabla: columna NOT NULL
    private String nombre;                          // Columna "nombre" -> VARCHAR(255)

    @PositiveOrZero(message = "El precio no puede ser negativo") // Validacion: debe ser >= 0
    private Double precio;                                       // Columna "precio" -> FLOAT
}
