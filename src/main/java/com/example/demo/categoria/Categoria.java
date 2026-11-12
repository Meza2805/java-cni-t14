// Paquete donde vive la clase (debe coincidir con la carpeta)
package com.example.demo.categoria;

// Anotaciones de JPA: @Entity, @Table, @Id, @Column, @GeneratedValue...
import jakarta.persistence.*;
// Validaciones: se revisan cuando el controller usa @Valid
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// Lombok: genera codigo repetitivo al compilar
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity                     // Esta clase es una ENTIDAD: JPA la mapea a una tabla
@Table(name = "categorias") // Nombre de la tabla en la BD
@Data                       // Lombok: getters, setters, toString, equals y hashCode
@NoArgsConstructor          // Lombok: constructor vacio (obligatorio para JPA)
@AllArgsConstructor         // Lombok: constructor con todos los campos
public class Categoria {

    @Id                                                 // Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autoincremental: la BD asigna 1, 2, 3...
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")                       // No puede venir vacio
    @Size(max = 50, message = "El nombre no puede superar 50 caracteres") // Validacion de largo maximo
    @Column(nullable = false, unique = true, length = 50)                 // NOT NULL, UNIQUE (no se repite) y VARCHAR(50)
    private String nombre;

    @Size(max = 200, message = "La descripcion no puede superar 200 caracteres") // Opcional, pero con largo maximo
    @Column(length = 200)                                                        // VARCHAR(200), admite NULL
    private String descripcion;
}
