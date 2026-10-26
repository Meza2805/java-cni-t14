/*
 * ============================================================================
 *   PILAR 1 · ENCAPSULAMIENTO
 * ----------------------------------------------------------------------------
 *  Curso      : Fundamentos de Desarrollo Backend con Java
 *  Temporada  : XIV · CI Nicaragua · 2026
 *  Facilitador: Marvin Rafael Meza Pineda
 * ----------------------------------------------------------------------------
 *  Encapsular es ocultar los datos de un objeto (private) y permitir que se
 *  cambien SOLO a través de métodos que validan las reglas (public).
 *
 *  En este archivo vas a ver, en orden:
 *    1. El problema: una clase con atributos públicos
 *    2. CuentaBancaria : sin setSaldo(), el saldo solo cambia con reglas
 *    3. Producto       : getters y setters que validan
 *    4. Usuario        : un dato que NUNCA sale del objeto (la contraseña)
 *
 *  ¿Cómo ejecutarlo?
 *    - En VS Code: abre este archivo y pulsa el botón ▶ Run.
 *    - En la terminal, dentro de esta carpeta:
 *          javac Encapsulamiento.java
 *          java Encapsulamiento
 *
 *  Nota: para que puedas ejecutarlo de una sola vez, todas las clases están
 *  en este archivo. En un proyecto real, cada clase va en su propio .java.
 * ============================================================================
 */

public class Encapsulamiento {

    public static void main(String[] args) {

        // ====================================================================
        // PASO 1 · EL PROBLEMA: ATRIBUTOS PÚBLICOS
        // ====================================================================
        System.out.println("=== 1. Sin encapsulamiento ===");

        CuentaInsegura insegura = new CuentaInsegura();
        insegura.titular = "Ana";
        insegura.saldo = 500;
        insegura.saldo = -3000;        // compila, corre... y nadie lo impide
        System.out.println(insegura.titular + " tiene un saldo de C$" + insegura.saldo);

        // ====================================================================
        // PASO 2 · CUENTA BANCARIA ENCAPSULADA
        // ====================================================================
        System.out.println("\n=== 2. Cuenta bancaria encapsulada ===");

        CuentaBancaria cuenta = new CuentaBancaria("Ana", 500);
        cuenta.depositar(250);
        cuenta.depositar(-100);        // rechazado: monto inválido
        cuenta.retirar(2000);          // rechazado: fondos insuficientes
        cuenta.retirar(300);

        System.out.println("Saldo final de " + cuenta.getTitular() + ": C$" + cuenta.getSaldo());
        System.out.println("Movimientos realizados: " + cuenta.getMovimientos());

        // Quita el comentario de la siguiente línea y compila: verás el error
        // "saldo has private access in CuentaBancaria".
        // cuenta.saldo = -3000;

        // ====================================================================
        // PASO 3 · PRODUCTO: GETTERS Y SETTERS CON VALIDACIÓN
        // ====================================================================
        System.out.println("\n=== 3. Producto de una tienda ===");

        Producto teclado = new Producto("Teclado mecánico", 1200, 10);
        teclado.setPrecio(-50);        // rechazado
        teclado.setPrecio(1100);       // aceptado
        teclado.vender(3);
        teclado.vender(20);            // rechazado: no hay suficiente stock
        teclado.reabastecer(5);
        teclado.mostrar();

        // ====================================================================
        // PASO 4 · USUARIO: LA CONTRASEÑA NUNCA SALE DEL OBJETO
        // ====================================================================
        System.out.println("\n=== 4. Usuario de un sistema ===");

        Usuario usuario = new Usuario("mmeza", "Java2026!");
        usuario.iniciarSesion("12345");          // incorrecta
        usuario.iniciarSesion("Java2026!");      // correcta
        usuario.cambiarContrasena("Java2026!", "123");          // rechazada: muy corta
        usuario.cambiarContrasena("Java2026!", "Backend#14");   // aceptada

        // Tres intentos fallidos bloquean la cuenta
        usuario.iniciarSesion("hola");
        usuario.iniciarSesion("hola123");
        usuario.iniciarSesion("qwerty");
        usuario.iniciarSesion("Backend#14");     // ya no deja entrar: está bloqueado

        System.out.println("¿" + usuario.getNombreUsuario() + " está bloqueado? " + usuario.isBloqueado());

        // No existe getContrasena(): desde aquí es imposible leerla.
        // System.out.println(usuario.contrasena);   // ERROR: private access
    }
}

// ============================================================================
// CLASE 1 · CuentaInsegura  ✗  (lo que NO debes hacer)
// ============================================================================
class CuentaInsegura {
    String titular;     // sin modificador: cualquiera en la carpeta puede cambiarlo
    double saldo;
}

// ============================================================================
// CLASE 2 · CuentaBancaria  ✓
// ============================================================================
class CuentaBancaria {

    // Atributos privados: solo los métodos de esta clase pueden tocarlos
    private String titular;
    private double saldo;
    private int movimientos;

    public CuentaBancaria(String titular, double saldoInicial) {
        this.titular = titular;
        if (saldoInicial > 0) {
            this.saldo = saldoInicial;
        }
    }

    public void depositar(double monto) {
        if (monto <= 0) {
            System.out.println("  [X] Depósito rechazado: el monto debe ser mayor que 0.");
            return;     // sale del método sin cambiar nada
        }
        saldo += monto;
        movimientos++;
        System.out.println("  [OK] Depósito de C$" + monto + ". Saldo: C$" + saldo);
    }

    public boolean retirar(double monto) {
        if (monto <= 0 || monto > saldo) {
            System.out.println("  [X] Retiro de C$" + monto + " rechazado: fondos insuficientes.");
            return false;
        }
        saldo -= monto;
        movimientos++;
        System.out.println("  [OK] Retiro de C$" + monto + ". Saldo: C$" + saldo);
        return true;
    }

    // Getters: permiten LEER, no modificar
    public String getTitular() {
        return titular;
    }

    public double getSaldo() {
        return saldo;
    }

    public int getMovimientos() {
        return movimientos;
    }

    // No hay setSaldo() a propósito: el saldo solo cambia con depositar() y retirar().
}

// ============================================================================
// CLASE 3 · Producto
// ============================================================================
class Producto {

    private String nombre;
    private double precio;
    private int stock;

    public Producto(String nombre, double precio, int stock) {
        this.nombre = nombre;
        setPrecio(precio);          // reutilizamos la validación del setter
        if (stock >= 0) {
            this.stock = stock;
        }
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    // Un buen setter no solo asigna: VALIDA
    public void setPrecio(double precio) {
        if (precio <= 0) {
            System.out.println("  [X] Precio inválido para " + nombre + ": C$" + precio);
            return;
        }
        this.precio = precio;
        System.out.println("  [OK] Nuevo precio de " + nombre + ": C$" + precio);
    }

    public int getStock() {
        return stock;
    }

    // En lugar de setStock(), el stock cambia con operaciones del negocio
    public void vender(int cantidad) {
        if (cantidad <= 0 || cantidad > stock) {
            System.out.println("  [X] No se pueden vender " + cantidad + " unidades (stock: " + stock + ").");
            return;
        }
        stock -= cantidad;
        System.out.println("  [OK] Vendidas " + cantidad + " unidades. Quedan " + stock + ".");
    }

    public void reabastecer(int cantidad) {
        if (cantidad > 0) {
            stock += cantidad;
            System.out.println("  [OK] Llegaron " + cantidad + " unidades. Stock: " + stock + ".");
        }
    }

    public double calcularValorInventario() {
        return precio * stock;
    }

    public void mostrar() {
        System.out.println(nombre + " | C$" + precio + " | " + stock + " en stock | inventario: C$"
                + calcularValorInventario());
    }
}

// ============================================================================
// CLASE 4 · Usuario
// ============================================================================
class Usuario {

    private final int MAX_INTENTOS = 3;     // constante: no puede cambiar

    private String nombreUsuario;
    private String contrasena;              // sin getter: nunca sale del objeto
    private int intentosFallidos;
    private boolean bloqueado;

    public Usuario(String nombreUsuario, String contrasena) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
    }

    public boolean iniciarSesion(String intento) {
        if (bloqueado) {
            System.out.println("  [X] Usuario bloqueado. Contacta al administrador.");
            return false;
        }

        if (contrasena.equals(intento)) {      // los String se comparan con equals()
            intentosFallidos = 0;
            System.out.println("  [OK] Bienvenido, " + nombreUsuario + ".");
            return true;
        }

        intentosFallidos++;
        if (intentosFallidos >= MAX_INTENTOS) {
            bloqueado = true;
            System.out.println("  [X] Contraseña incorrecta. Usuario bloqueado.");
        } else {
            int restantes = MAX_INTENTOS - intentosFallidos;
            System.out.println("  [X] Contraseña incorrecta. Te quedan " + restantes + " intento(s).");
        }
        return false;
    }

    public void cambiarContrasena(String actual, String nueva) {
        if (!contrasena.equals(actual)) {
            System.out.println("  [X] La contraseña actual no coincide.");
            return;
        }
        if (nueva.length() < 8) {
            System.out.println("  [X] La nueva contraseña debe tener al menos 8 caracteres.");
            return;
        }
        contrasena = nueva;
        System.out.println("  [OK] Contraseña actualizada.");
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public boolean isBloqueado() {      // para boolean, la convención es isAlgo()
        return bloqueado;
    }
}
