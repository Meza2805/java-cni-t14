/*
 * ============================================================================
 *   PILAR 4 · ABSTRACCIÓN
 * ----------------------------------------------------------------------------
 *  Curso      : Fundamentos de Desarrollo Backend con Java
 *  Temporada  : XIV · CI Nicaragua · 2026
 *  Facilitador: Marvin Rafael Meza Pineda
 * ----------------------------------------------------------------------------
 *  Abstraer es mostrar QUÉ hace algo y esconder CÓMO lo hace.
 *
 *  En este archivo vas a ver, en orden:
 *    1. ClienteCorreo : abstracción con un solo método público
 *    2. Reporte       : una clase abstracta que sirve de plantilla
 *    3. Una subclase nueva que entra sin tocar el código que ya funciona
 *
 *  ¿Cómo ejecutarlo?
 *    - En VS Code: abre este archivo y pulsa el botón ▶ Run.
 *    - En la terminal, dentro de esta carpeta:
 *          javac Abstraccion.java
 *          java Abstraccion
 *
 *  Nota: para que puedas ejecutarlo de una sola vez, todas las clases están
 *  en este archivo. En un proyecto real, cada clase va en su propio .java.
 * ============================================================================
 */

public class Abstraccion {

    public static void main(String[] args) {

        // ====================================================================
        // PASO 1 · ABSTRACCIÓN: UN MÉTODO SIMPLE, DETALLES ESCONDIDOS
        // ====================================================================
        System.out.println("=== 1. Quien usa la clase solo ve enviar() ===");

        ClienteCorreo correo = new ClienteCorreo();
        correo.enviar("ana@correo.com", "Tu matrícula fue aprobada");

        // correo.conectar();   // ERROR: conectar() es private. No hace falta saber que existe.

        // ====================================================================
        // PASO 2 · CLASE ABSTRACTA: LA PLANTILLA DE UN REPORTE
        // ====================================================================
        System.out.println("\n=== 2. Reportes con la misma estructura ===");

        // Reporte r = new Reporte("Genérico");
        // ERROR: Reporte is abstract; cannot be instantiated

        // ====================================================================
        // PASO 3 · POLIMORFISMO SOBRE LA CLASE ABSTRACTA
        // ====================================================================
        // ReporteAsistencia se agregó después: este ciclo no tuvo que cambiar.
        Reporte[] reportes = {
            new ReporteVentas("Cafetería", 18, 6540.50),
            new ReporteInventario("Bodega", 120, 7),
            new ReporteAsistencia("Clase 04", 21, 25)
        };

        for (int i = 0; i < reportes.length; i++) {
            reportes[i].imprimir();
        }
    }
}

// ============================================================================
// CLASE 1 · ClienteCorreo
// La abstracción no siempre necesita la palabra abstract: un método público
// sencillo que esconde varios pasos privados ya es abstracción.
// ============================================================================
class ClienteCorreo {

    public void enviar(String destinatario, String mensaje) {
        conectar();
        autenticar();
        transmitir(destinatario, mensaje);
        desconectar();
    }

    private void conectar() {
        System.out.println("  (abre la conexión con el servidor)");
    }

    private void autenticar() {
        System.out.println("  (inicia sesión en el servidor)");
    }

    private void transmitir(String destinatario, String mensaje) {
        System.out.println("  Correo a " + destinatario + ": " + mensaje);
    }

    private void desconectar() {
        System.out.println("  (cierra la conexión)");
    }
}

// ============================================================================
// CLASE 2 · Reporte (abstracta)
// Puede tener atributos, constructor y métodos normales, igual que cualquier
// clase. Lo que la hace distinta: no se puede instanciar y sus métodos
// abstractos quedan pendientes para las hijas.
// ============================================================================
abstract class Reporte {

    private String titulo;

    public Reporte(String titulo) {     // lo llaman las hijas con super(...)
        this.titulo = titulo;
    }

    // Método abstracto: sin cuerpo y termina en punto y coma.
    // Toda hija concreta está OBLIGADA a implementarlo.
    protected abstract void imprimirDetalle();

    // Método normal: la secuencia está escrita aquí una sola vez y las hijas
    // solo aportan la parte que cambia.
    // final impide que una hija lo sobrescriba y altere la estructura.
    public final void imprimir() {
        System.out.println("---------- " + titulo + " ----------");
        imprimirDetalle();              // se ejecuta la versión del objeto real
        System.out.println();
    }
}

class ReporteVentas extends Reporte {

    private int numeroVentas;
    private double totalVendido;

    public ReporteVentas(String titulo, int numeroVentas, double totalVendido) {
        super(titulo);
        this.numeroVentas = numeroVentas;
        this.totalVendido = totalVendido;
    }

    @Override
    protected void imprimirDetalle() {
        System.out.println("Ventas: " + numeroVentas);
        System.out.println("Total : C$" + totalVendido);
    }
}

class ReporteInventario extends Reporte {

    private int unidades;
    private int productosConStockBajo;

    public ReporteInventario(String titulo, int unidades, int productosConStockBajo) {
        super(titulo);
        this.unidades = unidades;
        this.productosConStockBajo = productosConStockBajo;
    }

    @Override
    protected void imprimirDetalle() {
        System.out.println("Unidades en bodega: " + unidades);
        System.out.println("Con stock bajo    : " + productosConStockBajo);
    }
}

class ReporteAsistencia extends Reporte {

    private int presentes;
    private int matriculados;

    public ReporteAsistencia(String titulo, int presentes, int matriculados) {
        super(titulo);
        this.presentes = presentes;
        this.matriculados = matriculados;
    }

    @Override
    protected void imprimirDetalle() {
        double porcentaje = (double) presentes / matriculados * 100;
        System.out.println("Presentes : " + presentes + " de " + matriculados);
        System.out.println("Asistencia: " + porcentaje + " %");
    }
}
