// Paquete donde vive la clase (debe coincidir con la carpeta)
package com.example.demo.cliente;

// Anotaciones de JPA: @Entity, @Table, @Id, @Column, @GeneratedValue...
import jakarta.persistence.*;
// Validaciones: se revisan cuando el controller usa @Valid
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
// Lombok: genera codigo repetitivo al compilar
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity                   // Esta clase es una ENTIDAD: JPA la mapea a una tabla
@Table(name = "clientes") // Nombre de la tabla en la BD
@Data                     // Lombok: getters, setters, toString, equals y hashCode
@NoArgsConstructor        // Lombok: constructor vacio (obligatorio para JPA)
@AllArgsConstructor       // Lombok: constructor con todos los campos
public class Cliente {

    @Id                                                 // Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autoincremental
    private Long id;

    @NotBlank(message = "El nombre es obligatorio") // No puede venir vacio
    @Column(nullable = false)                       // NOT NULL
    private String nombre;

    @NotBlank(message = "El email es obligatorio")    // No puede venir vacio
    @Email(message = "El email no tiene un formato valido") // Debe tener formato de correo: algo@dominio.com
    @Column(nullable = false, unique = true)          // NOT NULL y UNIQUE: dos clientes no comparten email
    private String email;

    @NotNull(message = "La edad es obligatoria")       // Para numeros se usa @NotNull (no @NotBlank, que es para textos)
    @Min(value = 18, message = "Debe ser mayor de edad") // Valor minimo permitido
    @Max(value = 120, message = "Edad no valida")        // Valor maximo permitido
    private Integer edad;                                // Columna INTEGER

    @Column(name = "activo") // Podemos cambiar el nombre de la columna con "name"
    private Boolean activo = true; // Valor por defecto: si el JSON no lo envia, queda en true
}
