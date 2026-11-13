// Paquete donde vive la clase (debe coincidir con la carpeta)
package com.example.demo.categoria;

// Entidad del otro lado de la relacion
import com.example.demo.producto.Producto;
// Evita que el JSON entre en un ciclo infinito (categoria -> productos -> categoria -> ...)
import com.fasterxml.jackson.annotation.JsonIgnore;
// Anotaciones de JPA: @Entity, @Table, @Id, @Column, @GeneratedValue, @OneToMany...
import jakarta.persistence.*;
// Validaciones: se revisan cuando el controller usa @Valid
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// Lombok: genera codigo repetitivo al compilar
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList; // Implementacion de la lista de productos
import java.util.List;      // Lista de productos de la categoria

@Entity                     // Esta clase es una ENTIDAD: JPA la mapea a una tabla
@Table(name = "categorias") // Nombre de la tabla en la BD
@Data                       // Lombok: getters, setters, toString, equals y hashCode
@NoArgsConstructor          // Lombok: constructor vacio (obligatorio para JPA)
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

    // ===================== RELACION: una categoria -> muchos productos =====================
    // @OneToMany: UNA categoria tiene MUCHOS productos. Es el lado INVERSO de la relacion.
    // mappedBy = "categoria": la clave foranea NO esta aqui, esta en el campo "categoria" de Producto.
    // Por eso esta lista NO crea ninguna columna en la tabla "categorias".
    // Por defecto es LAZY: los productos solo se cargan de la BD cuando alguien usa esta lista.
    @OneToMany(mappedBy = "categoria")
    @JsonIgnore                 // No se envia en el JSON: evita el ciclo categoria -> productos -> categoria...
    @ToString.Exclude           // Lombok: no incluirla en toString() (tambien evitaria un ciclo)
    @EqualsAndHashCode.Exclude  // Lombok: no incluirla en equals()/hashCode()
    private List<Producto> productos = new ArrayList<>();

    // Constructor para crear categorias sin productos: new Categoria(null, "Hogar", "Muebles...")
    public Categoria(Long id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}
