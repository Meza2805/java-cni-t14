package ni.cinicaragua.firstapi.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/*
 * ============================================================================
 *  MODELO: Estudiante
 * ----------------------------------------------------------------------------
 *  Representa a un estudiante del curso. Lo usa EstudianteController para
 *  practicar los 5 verbos HTTP de un CRUD (Crear, Leer, Actualizar, Borrar).
 *
 *  NUEVO: VALIDACIONES
 *  Las anotaciones sobre cada atributo son REGLAS que deben cumplir los datos
 *  que llegan a la API. Si el controlador usa @Valid y alguna regla no se
 *  cumple, Spring responde automáticamente "400 Bad Request" sin que
 *  tengamos que escribir ningún if.
 *
 *      @NotBlank  → no puede ser null, vacío "" ni solo espacios "   "
 *      @Email     → debe tener formato de correo: algo@dominio.com
 *      @Min/@Max  → número mínimo y máximo permitido
 * ============================================================================
 */
public class Estudiante {

    // El id lo asigna el SERVIDOR, no el cliente. Por eso no lleva validación.
    private Long id;

    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    private String correo;

    @Min(value = 14, message = "La edad mínima es 14 años")
    @Max(value = 99, message = "La edad máxima es 99 años")
    private int edad;

    // ------------------------------------------------------------------------
    // CONSTRUCTORES
    // ------------------------------------------------------------------------
    public Estudiante() {
    }

    public Estudiante(Long id, String nombres, String apellidos, String correo, int edad) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.edad = edad;
    }

    // ------------------------------------------------------------------------
    // GETTERS Y SETTERS
    // ------------------------------------------------------------------------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }
}
