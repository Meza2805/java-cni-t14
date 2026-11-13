// Paquete donde vive la clase (debe coincidir con la carpeta)
package com.example.demo.cliente;

// Entidad del otro lado de la relacion
import com.example.demo.producto.Producto;
// Para que "favoritos" salga en el JSON de respuesta pero se ignore si viene en el JSON de entrada
import com.fasterxml.jackson.annotation.JsonProperty;
// Anotaciones de JPA: @Entity, @Table, @Id, @Column, @GeneratedValue, @ManyToMany...
import jakarta.persistence.*;
// Validaciones: se revisan cuando el controller usa @Valid
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
// Lombok: genera codigo repetitivo al compilar
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.LinkedHashSet; // Implementacion del Set que conserva el orden de insercion
import java.util.Set;           // Conjunto: no admite repetidos (un producto no puede ser favorito 2 veces)

@Entity                   // Esta clase es una ENTIDAD: JPA la mapea a una tabla
@Table(name = "clientes") // Nombre de la tabla en la BD
@Data                     // Lombok: getters, setters, toString, equals y hashCode
@NoArgsConstructor        // Lombok: constructor vacio (obligatorio para JPA)
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

    // ===================== RELACION: muchos clientes <-> muchos productos =====================
    // @ManyToMany: un cliente tiene MUCHOS productos favoritos, y un producto puede ser
    // favorito de MUCHOS clientes.
    // En una BD relacional esto NO cabe en una columna: se necesita una TABLA INTERMEDIA
    // (tabla puente) con dos claves foraneas:
    //
    //   clientes (id) <-- cliente_favoritos (cliente_id, producto_id) --> productos (id)
    //
    // Cliente es el DUENO de la relacion (aqui esta @JoinTable). Producto no necesita saber nada.
    // Por defecto es LAZY: los favoritos solo se leen de la BD cuando se usan.
    @ManyToMany
    @JoinTable(
            name = "cliente_favoritos",                               // Nombre de la tabla intermedia
            joinColumns = @JoinColumn(name = "cliente_id"),           // FK hacia ESTA entidad (clientes.id)
            inverseJoinColumns = @JoinColumn(name = "producto_id")    // FK hacia la OTRA entidad (productos.id)
    )
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) // Sale en las respuestas; se ignora al crear/actualizar
    @ToString.Exclude                                     // Lombok: no incluirla en toString()
    @EqualsAndHashCode.Exclude                            // Lombok: no incluirla en equals()/hashCode()
    private Set<Producto> favoritos = new LinkedHashSet<>();

    // Constructor para crear clientes sin favoritos: new Cliente(null, "Ana", "ana@correo.com", 25, true)
    public Cliente(Long id, String nombre, String email, Integer edad, Boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.edad = edad;
        this.activo = activo;
    }
}
