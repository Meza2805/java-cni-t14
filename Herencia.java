/*
 * ============================================================================
 *   PILAR 2 · HERENCIA
 * ----------------------------------------------------------------------------
 *  Curso      : Fundamentos de Desarrollo Backend con Java
 *  Temporada  : XIV · CI Nicaragua · 2026
 *  Facilitador: Marvin Rafael Meza Pineda
 * ----------------------------------------------------------------------------
 *  La herencia crea una clase nueva a partir de otra que ya existe. La hija
 *  recibe los atributos y métodos del padre y solo agrega lo NUEVO.
 *  Úsala solo cuando la frase "X ES UN Y" tenga sentido.
 *
 *  En este archivo vas a ver, en orden:
 *    1. Empleado → Desarrollador, Gerente   (extends, super, protected)
 *    2. Vehiculo → Moto, Camion             (constructores en cadena, toString)
 *    3. Cuenta   → CuentaAhorro, CuentaCorriente
 *                  (herencia + encapsulamiento trabajando juntos)
 *
 *  ¿Cómo ejecutarlo?
 *    - En VS Code: abre este archivo y pulsa el botón ▶ Run.
 *    - En la terminal, dentro de esta carpeta:
 *          javac Herencia.java
 *          java Herencia
 *
 *  Nota: para que puedas ejecutarlo de una sola vez, todas las clases están
 *  en este archivo. En un proyecto real, cada clase va en su propio .java.
 * ============================================================================
 */

public class Herencia {

    public static void main(String[] args) {

        // ====================================================================
        // PASO 1 · EMPLEADOS: extends, super y protected
        // ====================================================================
        System.out.println("=== 1. Empleados ===");

        Desarrollador dev = new Desarrollador("Carla", 1200, "Java");
        Gerente gerente = new Gerente("Jorge", 1500, 400);

        dev.presentarse();          // heredado de Empleado
        dev.programar();            // propio de Desarrollador

        gerente.presentarse();      // heredado de Empleado
        gerente.dirigirReunion();   // propio de Gerente

        System.out.println("Pago de Carla: C$" + dev.calcularPago());       // solo salario
        System.out.println("Pago de Jorge: C$" + gerente.calcularPago());   // salario + bono

        // ====================================================================
        // PASO 2 · VEHÍCULOS: constructores en cadena y toString()
        // ====================================================================
        System.out.println("\n=== 2. Vehículos ===");

        Moto moto = new Moto("Honda", 2022, 150);
        Camion camion = new Camion("Volvo", 2019, 12);

        moto.hacerCaballito();      // rechazado: todavía no está encendida
        moto.encender();            // heredado de Vehiculo
        moto.hacerCaballito();

        camion.encender();
        camion.cargar(8);
        camion.cargar(6);           // rechazado: supera la capacidad

        // println llama a toString(), que cada hija sobrescribió
        System.out.println(moto);
        System.out.println(camion);

        // ====================================================================
        // PASO 3 · CUENTAS: herencia + encapsulamiento
        // ====================================================================
        System.out.println("\n=== 3. Cuentas bancarias ===");

        CuentaAhorro ahorro = new CuentaAhorro("Ana", 1000, 5);
        ahorro.depositar(500);      // heredado de Cuenta
        ahorro.retirar(2000);       // rechazado: no tiene sobregiro
        ahorro.aplicarInteres();    // propio de CuentaAhorro

        CuentaCorriente corriente = new CuentaCorriente("Luis", 300, 500);
        corriente.retirar(600);     // permitido: usa C$300 de sobregiro
        corriente.retirar(300);     // rechazado: solo le quedan C$200 de sobregiro

        System.out.println("Saldo de " + ahorro.getTitular() + ": C$" + ahorro.getSaldo());
        System.out.println("Saldo de " + corriente.getTitular() + ": C$" + corriente.getSaldo());
    }
}

// ============================================================================
// EJEMPLO 1 · Empleado (superclase) → Desarrollador, Gerente (subclases)
// ============================================================================
class Empleado {

    // protected: las subclases pueden usarlos; el resto del proyecto, no
    protected String nombre;
    protected double salario;

    public Empleado(String nombre, double salario) {
        this.nombre = nombre;
        this.salario = salario;
    }

    public void presentarse() {
        System.out.println("Hola, soy " + nombre + ".");
    }

    public double calcularPago() {
        return salario;
    }
}

class Desarrollador extends Empleado {

    private String lenguaje;        // solo declaramos lo NUEVO

    public Desarrollador(String nombre, double salario, String lenguaje) {
        super(nombre, salario);     // primera línea: llama al constructor de Empleado
        this.lenguaje = lenguaje;
    }

    public void programar() {
        // "nombre" viene de Empleado; se puede usar porque es protected
        System.out.println(nombre + " está programando en " + lenguaje + ".");
    }
}

class Gerente extends Empleado {

    private double bono;

    public Gerente(String nombre, double salario, double bono) {
        super(nombre, salario);
        this.bono = bono;
    }

    public void dirigirReunion() {
        System.out.println(nombre + " está dirigiendo la reunión semanal.");
    }

    // Sobrescribimos el método del padre y reutilizamos su versión con super
    @Override
    public double calcularPago() {
        return super.calcularPago() + bono;
    }
}

// ============================================================================
// EJEMPLO 2 · Vehiculo (superclase) → Moto, Camion (subclases)
// ============================================================================
class Vehiculo {

    protected String marca;
    protected int anio;
    private boolean encendido;      // private: ni las hijas lo cambian directamente

    public Vehiculo(String marca, int anio) {
        this.marca = marca;
        this.anio = anio;
    }

    public void encender() {
        if (encendido) {
            System.out.println(marca + " ya estaba encendido.");
            return;
        }
        encendido = true;
        System.out.println(marca + ": motor encendido.");
    }

    public boolean isEncendido() {
        return encendido;
    }

    // toString() viene de Object, la clase padre de TODAS las clases.
    // Al sobrescribirlo, println(objeto) muestra algo legible.
    @Override
    public String toString() {
        return marca + " (" + anio + ")";
    }
}

class Moto extends Vehiculo {

    private int cilindrada;

    public Moto(String marca, int anio, int cilindrada) {
        super(marca, anio);
        this.cilindrada = cilindrada;
    }

    public void hacerCaballito() {
        // "encendido" es private en Vehiculo: la hija lo consulta con el getter
        if (!isEncendido()) {
            System.out.println("Primero enciende la moto.");
            return;
        }
        System.out.println("¡La " + marca + " levanta la llanta delantera!");
    }

    @Override
    public String toString() {
        return "Moto " + super.toString() + " · " + cilindrada + " cc";
    }
}

class Camion extends Vehiculo {

    private double capacidadToneladas;
    private double cargaActual;

    public Camion(String marca, int anio, double capacidadToneladas) {
        super(marca, anio);
        this.capacidadToneladas = capacidadToneladas;
    }

    public void cargar(double toneladas) {
        if (toneladas <= 0 || cargaActual + toneladas > capacidadToneladas) {
            System.out.println("No caben " + toneladas + " t en el " + marca
                    + " (lleva " + cargaActual + " de " + capacidadToneladas + " t).");
            return;
        }
        cargaActual += toneladas;
        System.out.println(marca + " cargado con " + toneladas + " t.");
    }

    @Override
    public String toString() {
        return "Camión " + super.toString() + " · carga " + cargaActual + "/" + capacidadToneladas + " t";
    }
}

// ============================================================================
// EJEMPLO 3 · Cuenta (superclase) → CuentaAhorro, CuentaCorriente (subclases)
// Herencia y encapsulamiento juntos: el saldo es private en Cuenta, así que
// las hijas solo pueden cambiarlo con depositar() y retirar().
// ============================================================================
class Cuenta {

    private String titular;
    private double saldo;

    public Cuenta(String titular, double saldoInicial) {
        this.titular = titular;
        if (saldoInicial > 0) {
            this.saldo = saldoInicial;
        }
    }

    public void depositar(double monto) {
        if (monto <= 0) {
            System.out.println("  [X] Depósito rechazado: monto inválido.");
            return;
        }
        saldo += monto;
        System.out.println("  [OK] " + titular + " depositó C$" + monto + ". Saldo: C$" + saldo);
    }

    public boolean retirar(double monto) {
        if (monto <= 0 || !puedeRetirar(monto)) {
            System.out.println("  [X] Retiro de C$" + monto + " rechazado para " + titular + ".");
            return false;
        }
        saldo -= monto;
        System.out.println("  [OK] " + titular + " retiró C$" + monto + ". Saldo: C$" + saldo);
        return true;
    }

    // protected: cada tipo de cuenta puede cambiar ESTA regla,
    // pero nadie fuera de la familia de clases puede llamarla.
    protected boolean puedeRetirar(double monto) {
        return monto <= saldo;
    }

    public String getTitular() {
        return titular;
    }

    public double getSaldo() {
        return saldo;
    }
}

class CuentaAhorro extends Cuenta {

    private double porcentajeInteres;   // 5 significa 5 %

    public CuentaAhorro(String titular, double saldoInicial, double porcentajeInteres) {
        super(titular, saldoInicial);
        this.porcentajeInteres = porcentajeInteres;
    }

    public void aplicarInteres() {
        // saldo es private en Cuenta: usamos getSaldo() para leerlo
        // y depositar() para modificarlo
        double interes = getSaldo() * porcentajeInteres / 100;
        System.out.println("  Interés del " + porcentajeInteres + " %: C$" + interes);
        depositar(interes);
    }
}

class CuentaCorriente extends Cuenta {

    private double limiteSobregiro;

    public CuentaCorriente(String titular, double saldoInicial, double limiteSobregiro) {
        super(titular, saldoInicial);
        this.limiteSobregiro = limiteSobregiro;
    }

    // Cambia la regla del padre: puede quedar en negativo hasta el límite
    @Override
    protected boolean puedeRetirar(double monto) {
        return monto <= getSaldo() + limiteSobregiro;
    }
}
