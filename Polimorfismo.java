/*
 * ============================================================================
 *   PILAR 3 · POLIMORFISMO
 * ----------------------------------------------------------------------------
 *  Curso      : Fundamentos de Desarrollo Backend con Java
 *  Temporada  : XIV · CI Nicaragua · 2026
 *  Facilitador: Marvin Rafael Meza Pineda
 * ----------------------------------------------------------------------------
 *  Polimorfismo significa "muchas formas": un mismo mensaje (llamar al mismo
 *  método) produce respuestas distintas según el objeto real que lo recibe.
 *
 *  En este archivo vas a ver, en orden:
 *    1. El problema: un método lleno de if que crece con cada tipo nuevo
 *    2. Variable del padre, objeto del hijo (ligadura dinámica)
 *    3. Un arreglo y un método que tratan a todos por igual
 *    4. Cuando necesitas algo propio del hijo: instanceof
 *    5. La trampa: la sobrecarga la decide el compilador
 *
 *  ¿Cómo ejecutarlo?
 *    - En VS Code: abre este archivo y pulsa el botón ▶ Run.
 *    - En la terminal, dentro de esta carpeta:
 *          javac Polimorfismo.java
 *          java Polimorfismo
 *
 *  Nota: para que puedas ejecutarlo de una sola vez, todas las clases están
 *  en este archivo. En un proyecto real, cada clase va en su propio .java.
 * ============================================================================
 */

public class Polimorfismo {

    public static void main(String[] args) {

        // ====================================================================
        // PASO 1 · EL PROBLEMA: UN IF POR CADA TIPO
        // ====================================================================
        System.out.println("=== 1. Sin polimorfismo ===");

        CalculadoraConIf calculadora = new CalculadoraConIf();
        System.out.println("Moto, 3 kg : C$" + calculadora.calcularCosto("moto", 3));
        System.out.println("Dron, 3 kg : C$" + calculadora.calcularCosto("dron", 3)); // tipo nuevo: da 0

        // ====================================================================
        // PASO 2 · VARIABLE DEL PADRE, OBJETO DEL HIJO
        // ====================================================================
        System.out.println("\n=== 2. Variable del padre, objeto del hijo ===");

        // El tipo de la variable (Envio) decide QUÉ métodos puedes llamar.
        // El objeto real (EnvioMoto) decide QUÉ VERSIÓN se ejecuta.
        Envio envio = new EnvioMoto("Masaya", 3);
        System.out.println("Costo: C$" + envio.calcularCosto());    // versión de EnvioMoto

        // La misma variable puede apuntar luego a otro objeto de la familia
        envio = new EnvioCamion("Estelí", 100);
        System.out.println("Costo: C$" + envio.calcularCosto());    // versión de EnvioCamion

        // ====================================================================
        // PASO 3 · UN ARREGLO Y UN MÉTODO PARA TODOS
        // ====================================================================
        System.out.println("\n=== 3. Un despacho que no pregunta el tipo ===");

        Envio[] envios = {
            new Envio("León", 2),
            new EnvioMoto("Masaya", 3),
            new EnvioCamion("Estelí", 100),
            new EnvioExpress("Granada", 2, 4)
        };

        Despacho despacho = new Despacho();
        despacho.imprimirGuias(envios);
        System.out.println("Total del día: C$" + despacho.calcularTotal(envios));

        // ====================================================================
        // PASO 4 · ALGO PROPIO DEL HIJO: instanceof
        // ====================================================================
        System.out.println("\n=== 4. Métodos propios del hijo ===");

        Envio urgente = new EnvioExpress("Rivas", 1, 6);

        // urgente.getHorasLimite();   // ERROR: la variable es de tipo Envio y
        //                             // Envio no tiene ese método.

        // instanceof revisa el objeto real. Si coincide, "express" ya viene
        // convertida al tipo EnvioExpress y se puede usar dentro del if.
        if (urgente instanceof EnvioExpress express) {
            System.out.println("Debe llegar en " + express.getHorasLimite() + " horas.");
        }

        // ====================================================================
        // PASO 5 · LA TRAMPA: SOBRECARGA VS. SOBRESCRITURA
        // ====================================================================
        System.out.println("\n=== 5. La sobrecarga la decide el compilador ===");

        EnvioExpress comoExpress = new EnvioExpress("Jinotega", 1, 8);
        Envio comoEnvio = comoExpress;      // el MISMO objeto, otra variable

        // Sobrecarga: se elige al COMPILAR, según el tipo de la variable.
        despacho.describir(comoExpress);    // versión (EnvioExpress)
        despacho.describir(comoEnvio);      // versión (Envio), aunque el objeto es express

        // Sobrescritura: se elige al EJECUTAR, según el objeto real.
        System.out.println("Tipo real: " + comoEnvio.getTipo());    // Express
    }
}

// ============================================================================
// CLASE 1 · CalculadoraConIf  ✗  (lo que queremos evitar)
// Cada tipo nuevo obliga a volver a abrir este método y agregar otro if.
// ============================================================================
class CalculadoraConIf {

    public double calcularCosto(String tipo, double pesoKg) {
        if (tipo.equals("estandar")) {
            return 50 + pesoKg * 10;
        } else if (tipo.equals("moto")) {
            return 30 + pesoKg * 8;
        } else if (tipo.equals("camion")) {
            return 50 + pesoKg * 10 + 200;
        }
        return 0;       // si se nos olvidó un tipo, nadie avisa: cobra 0
    }
}

// ============================================================================
// CLASE 2 · Envio (superclase) → EnvioMoto, EnvioCamion, EnvioExpress
// ============================================================================
class Envio {

    private String destino;
    private double pesoKg;

    public Envio(String destino, double pesoKg) {
        this.destino = destino;
        this.pesoKg = pesoKg;
    }

    public String getDestino() {
        return destino;
    }

    public double getPesoKg() {
        return pesoKg;
    }

    public String getTipo() {
        return "Estándar";
    }

    public double calcularCosto() {
        return 50 + pesoKg * 10;
    }

    // Este método está escrito UNA sola vez, pero getTipo() y calcularCosto()
    // ejecutan la versión del objeto real: el padre llama al código del hijo.
    public void imprimirGuia() {
        System.out.println(getTipo() + " a " + destino + ": C$" + calcularCosto());
    }
}

class EnvioMoto extends Envio {

    public EnvioMoto(String destino, double pesoKg) {
        super(destino, pesoKg);
    }

    @Override
    public String getTipo() {
        return "Moto";
    }

    @Override
    public double calcularCosto() {
        return 30 + getPesoKg() * 8;    // reemplaza por completo la regla del padre
    }
}

class EnvioCamion extends Envio {

    public EnvioCamion(String destino, double pesoKg) {
        super(destino, pesoKg);
    }

    @Override
    public String getTipo() {
        return "Camión";
    }

    @Override
    public double calcularCosto() {
        return super.calcularCosto() + 200;     // reutiliza la regla del padre y agrega
    }
}

class EnvioExpress extends Envio {

    private int horasLimite;

    public EnvioExpress(String destino, double pesoKg, int horasLimite) {
        super(destino, pesoKg);
        this.horasLimite = horasLimite;
    }

    public int getHorasLimite() {       // solo existe en EnvioExpress
        return horasLimite;
    }

    @Override
    public String getTipo() {
        return "Express";
    }

    @Override
    public double calcularCosto() {
        return super.calcularCosto() * 2;
    }
}

// ============================================================================
// CLASE 3 · Despacho
// Solo conoce a Envio. Si mañana aparece EnvioDron, esta clase no cambia.
// ============================================================================
class Despacho {

    public void imprimirGuias(Envio[] envios) {
        for (int i = 0; i < envios.length; i++) {
            envios[i].imprimirGuia();
        }
    }

    public double calcularTotal(Envio[] envios) {
        double total = 0;
        for (int i = 0; i < envios.length; i++) {
            total += envios[i].calcularCosto();     // cada objeto responde a su manera
        }
        return total;
    }

    // Dos versiones con el mismo nombre y distinto parámetro: SOBRECARGA.
    public void describir(Envio envio) {
        System.out.println("describir(Envio): un envío a " + envio.getDestino());
    }

    public void describir(EnvioExpress envio) {
        System.out.println("describir(EnvioExpress): urgente, " + envio.getHorasLimite() + " horas");
    }
}
