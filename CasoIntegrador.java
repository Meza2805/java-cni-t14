/*
 * ============================================================================
 *   CASO INTEGRADOR · MATRÍCULA DE CURSOS
 * ----------------------------------------------------------------------------
 *  Curso      : Fundamentos de Desarrollo Backend con Java
 *  Temporada  : XIV · CI Nicaragua · 2026
 *  Facilitador: Marvin Rafael Meza Pineda
 * ----------------------------------------------------------------------------
 *  Abstracción, polimorfismo e interfaces trabajando en un mismo sistema:
 *    - Curso (abstracta)    : lo común de todos los cursos
 *    - Certificable         : una capacidad que solo algunos cursos tienen
 *    - Notificador          : un contrato que el servicio recibe por constructor
 *    - ServicioMatricula    : las reglas; trata a todos los cursos por igual
 *
 *  ¿Cómo ejecutarlo?
 *    - En VS Code: abre este archivo y pulsa el botón ▶ Run.
 *    - En la terminal, dentro de esta carpeta:
 *          javac CasoIntegrador.java
 *          java CasoIntegrador
 *
 *  Nota: para que puedas ejecutarlo de una sola vez, todas las clases están
 *  en este archivo. En un proyecto real, cada clase va en su propio .java.
 * ============================================================================
 */

public class CasoIntegrador {

    public static void main(String[] args) {

        // ====================================================================
        // PASO 1 · EL CATÁLOGO: UN ARREGLO DEL TIPO ABSTRACTO
        // ====================================================================
        System.out.println("=== Catálogo ===");

        Curso[] catalogo = {
            new CursoPresencial("Backend con Java", 30, 2),
            new CursoVirtual("Git y GitHub", 20, 50),
            new Charla("Tu primer empleo en tecnología", 2, 1)
        };

        for (int i = 0; i < catalogo.length; i++) {
            catalogo[i].mostrarFicha();         // polimorfismo
        }

        // ====================================================================
        // PASO 2 · MATRÍCULAS AVISANDO POR CORREO
        // ====================================================================
        System.out.println("\n=== Matrículas con aviso por correo ===");

        // Aquí se decide cómo se avisa. El servicio solo conoce el contrato.
        ServicioMatricula porCorreo = new ServicioMatricula(new NotificadorEmail());

        porCorreo.matricular("Ana", "ana@correo.com", catalogo[0]);
        porCorreo.matricular("Luis", "luis@correo.com", catalogo[2]);
        porCorreo.matricular("Rosa", "rosa@correo.com", catalogo[2]);   // sin cupo

        // ====================================================================
        // PASO 3 · CAMBIAR A SMS SIN TOCAR EL SERVICIO
        // ====================================================================
        System.out.println("\n=== Matrículas con aviso por SMS ===");

        ServicioMatricula porSms = new ServicioMatricula(new NotificadorSms());

        porSms.matricular("Carla", "8888-1234", catalogo[1]);

        System.out.println("\nRecaudado por correo: C$" + porCorreo.getRecaudado());
        System.out.println("Recaudado por SMS   : C$" + porSms.getRecaudado());
    }
}

// ============================================================================
// CONTRATOS
// ============================================================================
interface Notificador {
    void enviar(String destino, String mensaje);
}

interface Certificable {
    String emitirCertificado(String estudiante);
}

class NotificadorEmail implements Notificador {

    @Override
    public void enviar(String destino, String mensaje) {
        System.out.println("  Email a " + destino + ": " + mensaje);
    }
}

class NotificadorSms implements Notificador {

    @Override
    public void enviar(String destino, String mensaje) {
        System.out.println("  SMS al " + destino + ": " + mensaje);
    }
}

// ============================================================================
// JERARQUÍA DE CURSOS
// ============================================================================
abstract class Curso {

    private String nombre;
    private int horas;
    private int cupos;

    public Curso(String nombre, int horas, int cupos) {
        this.nombre = nombre;
        this.horas = horas;
        this.cupos = cupos;
    }

    // Lo que cada tipo de curso resuelve a su manera
    public abstract double calcularPrecio();
    public abstract String getModalidad();

    // Lo que es igual para todos
    public boolean reservarCupo() {
        if (cupos <= 0) {
            return false;
        }
        cupos--;
        return true;
    }

    public void mostrarFicha() {
        System.out.println(nombre + " | " + getModalidad() + " | " + horas + " h | C$"
                + calcularPrecio() + " | cupos: " + cupos);
    }

    public String getNombre() {
        return nombre;
    }

    public int getHoras() {
        return horas;
    }
}

// extends e implements juntos: ES UN Curso y PUEDE emitir certificado
class CursoPresencial extends Curso implements Certificable {

    public CursoPresencial(String nombre, int horas, int cupos) {
        super(nombre, horas, cupos);
    }

    @Override
    public double calcularPrecio() {
        return getHoras() * 40 + 150;   // C$150 de materiales
    }

    @Override
    public String getModalidad() {
        return "Presencial";
    }

    @Override
    public String emitirCertificado(String estudiante) {
        return "Certificado de " + getHoras() + " horas para " + estudiante;
    }
}

class CursoVirtual extends Curso implements Certificable {

    public CursoVirtual(String nombre, int horas, int cupos) {
        super(nombre, horas, cupos);
    }

    @Override
    public double calcularPrecio() {
        return getHoras() * 25;
    }

    @Override
    public String getModalidad() {
        return "Virtual";
    }

    @Override
    public String emitirCertificado(String estudiante) {
        return "Constancia digital para " + estudiante;
    }
}

// Es un Curso, pero no firma Certificable: una charla no da certificado
class Charla extends Curso {

    public Charla(String nombre, int horas, int cupos) {
        super(nombre, horas, cupos);
    }

    @Override
    public double calcularPrecio() {
        return 0;
    }

    @Override
    public String getModalidad() {
        return "Charla";
    }
}

// ============================================================================
// SERVICIO · Las reglas del negocio
// ============================================================================
class ServicioMatricula {

    private final Notificador notificador;
    private double recaudado;

    public ServicioMatricula(Notificador notificador) {
        this.notificador = notificador;
    }

    public boolean matricular(String estudiante, String contacto, Curso curso) {
        if (!curso.reservarCupo()) {
            notificador.enviar(contacto, curso.getNombre() + " ya no tiene cupos.");
            return false;
        }

        recaudado += curso.calcularPrecio();    // no pregunta qué tipo de curso es
        notificador.enviar(contacto, "Matrícula confirmada en " + curso.getNombre() + ".");

        // No todos los cursos tienen esta capacidad: aquí sí se pregunta
        if (curso instanceof Certificable certificable) {
            System.out.println("  " + certificable.emitirCertificado(estudiante));
        }
        return true;
    }

    public double getRecaudado() {
        return recaudado;
    }
}
