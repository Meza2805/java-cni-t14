/*
 * ============================================================================
 *   INTERFACES: PROGRAMAR CONTRA UN CONTRATO
 * ----------------------------------------------------------------------------
 *  Curso      : Fundamentos de Desarrollo Backend con Java
 *  Temporada  : XIV · CI Nicaragua · 2026
 *  Facilitador: Marvin Rafael Meza Pineda
 * ----------------------------------------------------------------------------
 *  Una interfaz dice QUÉ debe saber hacer una clase, sin decir CÓMO. La clase
 *  que la firma con implements queda obligada a cumplir ese contrato.
 *
 *  En este archivo vas a ver, en orden:
 *    1. Imprimible, Exportable : clases sin parentesco con la misma capacidad
 *    2. Constantes, métodos default y static dentro de una interfaz
 *    3. LibroRepositorio       : un contrato con dos implementaciones
 *    4. BibliotecaServicio     : recibe el contrato por el constructor
 *                                (la idea en la que se apoya Spring)
 *
 *  ¿Cómo ejecutarlo?
 *    - En VS Code: abre este archivo y pulsa el botón ▶ Run.
 *    - En la terminal, dentro de esta carpeta:
 *          javac Interfaces.java
 *          java Interfaces
 *
 *  Nota: para que puedas ejecutarlo de una sola vez, todas las clases están
 *  en este archivo. En un proyecto real, cada clase va en su propio .java.
 * ============================================================================
 */

public class Interfaces {

    public static void main(String[] args) {

        // ====================================================================
        // PASO 1 · UNA CAPACIDAD COMPARTIDA ENTRE CLASES SIN PARENTESCO
        // ====================================================================
        System.out.println("=== 1. Todo lo que se puede imprimir ===");

        // Una Factura no "es una" Etiqueta, pero las dos "pueden" imprimirse.
        Imprimible[] cola = {
            new Factura(1001, 1350),
            new Etiqueta("Frágil")
        };

        for (int i = 0; i < cola.length; i++) {
            cola[i].imprimir();
        }

        // ====================================================================
        // PASO 2 · VARIAS INTERFACES, DEFAULT Y STATIC
        // ====================================================================
        System.out.println("\n=== 2. Exportar datos ===");

        // Factura implementa dos interfaces: se puede ver como cualquiera de ellas.
        Exportable[] datos = {
            new Factura(1002, 480),
            new Contacto("Luis", "8888-1234")
        };

        for (int i = 0; i < datos.length; i++) {
            datos[i].mostrarExportacion();      // método default: viene ya escrito
        }

        System.out.println(Exportable.unir("uno", "dos"));  // método static: se llama con la interfaz

        // ====================================================================
        // PASO 3 · EL SERVICIO DEPENDE DEL CONTRATO, NO DE LA CLASE
        // ====================================================================
        System.out.println("\n=== 3. Biblioteca con repositorio en memoria ===");

        // Aquí se decide qué implementación se usa. BibliotecaServicio no lo sabe.
        LibroRepositorio enMemoria = new RepositorioEnMemoria();
        BibliotecaServicio biblioteca = new BibliotecaServicio(enMemoria);

        biblioteca.registrar("978-1", "Don Quijote");
        biblioteca.registrar("978-2", "Azul...");
        biblioteca.registrar("978-1", "Otro con el mismo ISBN");    // rechazado
        biblioteca.mostrar("978-2");
        biblioteca.mostrar("000-0");                                // no existe
        System.out.println("Libros guardados: " + enMemoria.contar());

        // ====================================================================
        // PASO 4 · CAMBIAR LA IMPLEMENTACIÓN SIN TOCAR EL SERVICIO
        // ====================================================================
        System.out.println("\n=== 4. El mismo servicio con un repositorio de prueba ===");

        BibliotecaServicio dePrueba = new BibliotecaServicio(new RepositorioDePrueba());
        dePrueba.mostrar("cualquier-isbn");
    }
}

// ============================================================================
// CONTRATO 1 · Imprimible
// ============================================================================
interface Imprimible {
    void imprimir();        // public y abstract por defecto
}

// ============================================================================
// CONTRATO 2 · Exportable: lo que una interfaz puede tener además
// ============================================================================
interface Exportable {

    // Atributo de interfaz: siempre es constante (public static final)
    String SEPARADOR = ";";

    String exportar();

    // default: método con cuerpo. Quien implementa lo hereda o lo sobrescribe.
    default void mostrarExportacion() {
        System.out.println("Exportando -> " + exportar());
    }

    // static: pertenece a la interfaz, no a los objetos
    static String unir(String a, String b) {
        return a + SEPARADOR + b;
    }
}

// ============================================================================
// CLASES QUE FIRMAN LOS CONTRATOS
// ============================================================================

// Una clase solo puede extender a UNA clase, pero puede implementar VARIAS interfaces.
class Factura implements Imprimible, Exportable {

    private int numero;
    private double total;

    public Factura(int numero, double total) {
        this.numero = numero;
        this.total = total;
    }

    @Override
    public void imprimir() {
        System.out.println("Factura #" + numero + " por C$" + total);
    }

    @Override
    public String exportar() {
        return Exportable.unir(String.valueOf(numero), String.valueOf(total));
    }
}

class Etiqueta implements Imprimible {

    private String texto;

    public Etiqueta(String texto) {
        this.texto = texto;
    }

    @Override
    public void imprimir() {
        System.out.println("[ " + texto + " ]");
    }
}

class Contacto implements Exportable {

    private String nombre;
    private String telefono;

    public Contacto(String nombre, String telefono) {
        this.nombre = nombre;
        this.telefono = telefono;
    }

    @Override
    public String exportar() {
        return Exportable.unir(nombre, telefono);
    }
}

// ============================================================================
// EL CASO BACKEND · Un repositorio guarda y busca datos
// ============================================================================

// record: datos inmutables que solo se transportan (Lectura 03)
record Libro(String isbn, String titulo) { }

// El contrato: qué se puede hacer con los libros guardados, no dónde se guardan.
interface LibroRepositorio {
    void guardar(Libro libro);
    Libro buscarPorIsbn(String isbn);   // devuelve null si no lo encuentra
    int contar();
}

// Implementación 1: guarda en un arreglo. Se pierde al cerrar el programa.
// Más adelante, otra implementación podría guardar en MySQL.
class RepositorioEnMemoria implements LibroRepositorio {

    private Libro[] libros = new Libro[100];
    private int cantidad = 0;

    @Override
    public void guardar(Libro libro) {
        if (cantidad < libros.length) {
            libros[cantidad] = libro;
            cantidad++;
        }
    }

    @Override
    public Libro buscarPorIsbn(String isbn) {
        for (int i = 0; i < cantidad; i++) {
            if (libros[i].isbn().equals(isbn)) {
                return libros[i];
            }
        }
        return null;
    }

    @Override
    public int contar() {
        return cantidad;
    }
}

// Implementación 2: no guarda nada y siempre responde lo mismo.
// Sirve para probar el servicio sin depender de una base de datos.
class RepositorioDePrueba implements LibroRepositorio {

    @Override
    public void guardar(Libro libro) {
        System.out.println("  (prueba: no se guarda " + libro.titulo() + ")");
    }

    @Override
    public Libro buscarPorIsbn(String isbn) {
        return new Libro(isbn, "Libro de prueba");
    }

    @Override
    public int contar() {
        return 1;
    }
}

// ============================================================================
// El servicio tiene las reglas del negocio. Solo conoce el contrato.
// Recibir lo que necesita por el constructor se llama INYECCIÓN DE DEPENDENCIAS.
// ============================================================================
class BibliotecaServicio {

    private final LibroRepositorio repositorio;

    public BibliotecaServicio(LibroRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public boolean registrar(String isbn, String titulo) {
        if (repositorio.buscarPorIsbn(isbn) != null) {
            System.out.println("  [X] Ya existe un libro con ISBN " + isbn + ".");
            return false;
        }
        repositorio.guardar(new Libro(isbn, titulo));
        System.out.println("  [OK] Registrado: " + titulo);
        return true;
    }

    public void mostrar(String isbn) {
        Libro libro = repositorio.buscarPorIsbn(isbn);
        if (libro == null) {
            System.out.println("  [X] No hay ningún libro con ISBN " + isbn + ".");
            return;
        }
        System.out.println("  " + libro);   // record ya trae un toString() legible
    }
}
