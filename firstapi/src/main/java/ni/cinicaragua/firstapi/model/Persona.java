package ni.cinicaragua.firstapi.model;

/*
 * ============================================================================
 *  MODELO: Persona
 * ----------------------------------------------------------------------------
 *  Un MODELO es una clase que representa un dato de nuestra aplicación.
 *  Cuando un endpoint devuelve un objeto Persona, Spring Boot (con la
 *  librería Jackson) lo convierte AUTOMÁTICAMENTE a JSON:
 *
 *      new Persona("Rafael", "Pineda", "Masculino", "rafael@correo.com")
 *
 *                              ▼  se convierte en  ▼
 *
 *      {
 *        "nombres": "Rafael",
 *        "apellidos": "Pineda",
 *        "sexo": "Masculino",
 *        "correo": "rafael@correo.com"
 *      }
 *
 *  ⚠ IMPORTANTE: Jackson arma el JSON usando los GETTERS.
 *    - getNombres()  → genera la propiedad "nombres"
 *    - GetSexo()     → ✗ NO funciona: debe empezar con "get" en minúscula.
 *    Si un atributo no tiene getter correcto, NO aparece en el JSON.
 *
 *  Convenciones de Java que aplicamos aquí:
 *    - Nombre de clase en PascalCase      → Persona  (no "persona")
 *    - Atributos y métodos en camelCase   → nombres, getNombres()
 *    - Atributos private + getters/setters → ENCAPSULAMIENTO
 * ============================================================================
 */
public class Persona {

    // ------------------------------------------------------------------------
    // ATRIBUTOS (private: solo se acceden mediante getters y setters)
    // ------------------------------------------------------------------------
    private String nombres;
    private String apellidos;
    private String sexo;      // "Masculino" o "Femenino"
    private String correo;

    // ------------------------------------------------------------------------
    // CONSTRUCTORES
    // ------------------------------------------------------------------------

    // Constructor vacío: Jackson lo necesita para convertir JSON → objeto
    // (por ejemplo, cuando un cliente nos ENVÍA una persona con POST).
    public Persona() {
    }

    // Constructor con todos los datos: para crear objetos en una sola línea.
    public Persona(String nombres, String apellidos, String sexo, String correo) {
        this.nombres = nombres;     // "this.nombres" = el atributo de la clase
        this.apellidos = apellidos; // "apellidos"    = el parámetro recibido
        this.sexo = sexo;
        this.correo = correo;
    }

    // ------------------------------------------------------------------------
    // GETTERS (leer) Y SETTERS (modificar)
    // ------------------------------------------------------------------------
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

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
