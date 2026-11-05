/*
 * ============================================================================
 *  MÉTODOS Y FUNCIONES EN JAVA
 * ----------------------------------------------------------------------------
 *  Curso      : Fundamentos de Desarrollo Backend con Java
 *  Temporada  : XIV · CI Nicaragua · 2026
 *  Facilitador: Marvin Rafael Meza Pineda
 * ----------------------------------------------------------------------------
 *  Un MÉTODO es un bloque de código con nombre que realiza una tarea.
 *  Lo escribimos UNA vez y lo podemos LLAMAR (usar) todas las veces que
 *  queramos. En Java no existen "funciones sueltas": todo vive dentro de una
 *  clase, así que técnicamente todo es un método. Por costumbre llamamos
 *  "función" al método que DEVUELVE un valor (como en matemática: f(x) = y).
 *
 *  En este archivo vas a ver, en orden:
 *    1. Método sin parámetros y sin retorno   (void)
 *    2. Método con parámetros                 (datos de entrada)
 *    3. Método que devuelve un valor          (return · "función")
 *    4. Método que devuelve boolean           (preguntas de sí/no)
 *    5. Métodos con if / switch adentro
 *    6. Métodos con ciclos adentro            (while)
 *    7. Sobrecarga                            (mismo nombre, distintos parámetros)
 *    8. Alcance y paso por valor              (variables locales)
 *    9. Todo junto: una calculadora con menú
 *
 *  Partes de un método:
 *
 *      public static  int     sumar  (int a, int b)  {  return a + b;  }
 *      └─modificadores┘ └tipo┘ └nombre┘ └─parámetros─┘    └──cuerpo──┘
 *                      de retorno
 *
 *  ¿Cómo ejecutarlo?
 *    - En VS Code: abre este archivo y pulsa el botón ▶ Run.
 *    - En la terminal, dentro de esta carpeta:
 *          javac Metodos.java
 *          java Metodos
 * ============================================================================
 */

import java.util.Scanner;

public class Metodos {

    // ========================================================================
    // 1 · MÉTODO SIN PARÁMETROS Y SIN RETORNO
    // ========================================================================
    // "void" significa que el método NO devuelve nada: solo hace su tarea.
    // Los paréntesis vacíos () indican que no necesita datos de entrada.
    // Por ahora todos nuestros métodos llevan "static" porque los llamamos
    // desde main, que también es static. (En POO veremos métodos sin static.)
    public static void mostrarBanner() {
        System.out.println("==========================================");
        System.out.println("      MÉTODOS Y FUNCIONES · CI NICARAGUA");
        System.out.println("==========================================");
    }

    // Un método muy útil para no repetir el mismo println en todo el programa.
    public static void separador() {
        System.out.println("------------------------------------------");
    }

    // ========================================================================
    // 2 · MÉTODO CON PARÁMETROS
    // ========================================================================
    // Los PARÁMETROS son variables que el método recibe desde afuera.
    // Cada parámetro se declara con su tipo, igual que una variable.
    public static void saludar(String nombre) {
        System.out.println("¡Hola, " + nombre + "! Bienvenido al curso.");
    }

    // Puede recibir varios parámetros, separados por coma.
    public static void imprimirLinea(char simbolo, int veces) {
        for (int i = 1; i <= veces; i++) {
            System.out.print(simbolo);
        }
        System.out.println();
    }

    // ========================================================================
    // 3 · MÉTODO QUE DEVUELVE UN VALOR ("FUNCIÓN")
    // ========================================================================
    // En lugar de void escribimos el TIPO del valor que se devuelve.
    // "return" entrega el resultado a quien llamó al método y lo termina.
    public static int sumar(int a, int b) {
        int resultado = a + b;
        return resultado;
    }

    // Se puede devolver directamente la expresión, sin variable intermedia.
    public static double areaCirculo(double radio) {
        return Math.PI * radio * radio;
    }

    public static double promedio(double nota1, double nota2, double nota3) {
        return (nota1 + nota2 + nota3) / 3;
    }

    // ========================================================================
    // 4 · MÉTODO QUE DEVUELVE BOOLEAN
    // ========================================================================
    // Muy común para "hacer preguntas". Por convención el nombre empieza
    // con "es", "esta", "tiene", "puede"... para que se lea como pregunta.
    public static boolean esPar(int numero) {
        return numero % 2 == 0;
    }

    public static boolean esMayorDeEdad(int edad) {
        return edad >= 18;
    }

    // ========================================================================
    // 5 · MÉTODOS CON IF / SWITCH ADENTRO
    // ========================================================================
    // Un método puede tener varios return: el primero que se ejecuta lo termina.
    public static String clasificarNota(double nota) {
        if (nota >= 90) {
            return "Excelente";
        } else if (nota >= 80) {
            return "Muy bueno";
        } else if (nota >= 60) {
            return "Aprobado";
        } else {
            return "Reprobado";
        }
    }

    // switch que devuelve directamente el valor de cada caso.
    public static String nombreDelDia(int dia) {
        switch (dia) {
            case 1:  return "Lunes";
            case 2:  return "Martes";
            case 3:  return "Miércoles";
            case 4:  return "Jueves";
            case 5:  return "Viernes";
            case 6:  return "Sábado";
            case 7:  return "Domingo";
            default: return "Día inválido";
        }
    }

    // ========================================================================
    // 6 · MÉTODOS CON CICLOS ADENTRO
    // ========================================================================
    // Factorial: 5! = 5 x 4 x 3 x 2 x 1 = 120
    // Usamos "long" porque el factorial crece muy rápido.
    public static long factorial(int n) {
        long resultado = 1;
        int i = n;
        while (i > 1) {
            resultado *= i;
            i--;
        }
        return resultado;
    }

    // Cuenta cuántos dígitos tiene un número: 4521 -> 4
    public static int contarDigitos(int numero) {
        if (numero == 0) {
            return 1;
        }
        numero = Math.abs(numero);   // los negativos también cuentan
        int digitos = 0;
        while (numero > 0) {
            numero = numero / 10;    // quita el último dígito
            digitos++;
        }
        return digitos;
    }

    // ========================================================================
    // 7 · SOBRECARGA DE MÉTODOS
    // ========================================================================
    // Varios métodos pueden llamarse IGUAL si sus parámetros son distintos
    // (en cantidad o en tipo). Java elige cuál usar según lo que le pasemos.
    public static double area(double lado) {                  // cuadrado
        return lado * lado;
    }

    public static double area(double base, double altura) {   // rectángulo
        return base * altura;
    }

    public static int mayor(int a, int b) {
        return (a > b) ? a : b;
    }

    public static int mayor(int a, int b, int c) {
        // ¡Un método puede llamar a otro método!
        return mayor(mayor(a, b), c);
    }

    // ========================================================================
    // 8 · ALCANCE Y PASO POR VALOR
    // ========================================================================
    // Las variables creadas dentro de un método (incluidos sus parámetros)
    // son LOCALES: solo existen mientras el método se ejecuta.
    // Además, Java pasa una COPIA del valor: cambiar el parámetro aquí
    // NO cambia la variable original que está en main.
    public static void intentarDuplicar(int numero) {
        numero = numero * 2;
        System.out.println("  Dentro del método, numero vale: " + numero);
    }

    // La forma correcta: devolver el nuevo valor y que main lo guarde.
    public static int duplicar(int numero) {
        return numero * 2;
    }

    // ========================================================================
    // 9 · MÉTODOS AUXILIARES PARA LA CALCULADORA
    // ========================================================================
    // Este método lee un número y lo repite hasta que esté en el rango.
    // Antes escribíamos este while en cada lugar donde pedíamos un dato;
    // ahora lo escribimos UNA sola vez y lo reutilizamos.
    public static int leerEnteroEnRango(Scanner teclado, String mensaje, int min, int max) {
        System.out.print(mensaje);
        int valor = teclado.nextInt();
        while (valor < min || valor > max) {
            System.out.print("Valor inválido. Escribe un número entre " + min + " y " + max + ": ");
            valor = teclado.nextInt();
        }
        return valor;
    }

    public static void mostrarMenu() {
        System.out.println();
        separador();
        System.out.println("CALCULADORA");
        System.out.println("1. Sumar");
        System.out.println("2. Restar");
        System.out.println("3. Multiplicar");
        System.out.println("4. Dividir");
        System.out.println("0. Salir");
        separador();
    }

    public static double calcular(int opcion, double a, double b) {
        switch (opcion) {
            case 1:  return a + b;
            case 2:  return a - b;
            case 3:  return a * b;
            case 4:  return a / b;
            default: return 0;
        }
    }

    // ========================================================================
    // MAIN · AQUÍ LLAMAMOS A TODOS LOS MÉTODOS
    // ========================================================================
    // Para LLAMAR a un método escribimos su nombre y, entre paréntesis,
    // los ARGUMENTOS (los valores reales que reciben los parámetros).
    public static void main(String[] args) {

        // ---- 1 · Sin parámetros ni retorno ----
        mostrarBanner();

        // ---- 2 · Con parámetros ----
        saludar("Ana");
        saludar("Carlos");          // el mismo método, otro argumento
        imprimirLinea('*', 20);
        imprimirLinea('=', 10);

        // ---- 3 · Con retorno: el resultado se puede GUARDAR... ----
        System.out.println("\n[3] Métodos que devuelven un valor");
        int total = sumar(8, 5);
        System.out.println("8 + 5 = " + total);

        // ...o usar directamente dentro de otra expresión.
        System.out.println("10 + 20 + 30 = " + sumar(sumar(10, 20), 30));
        System.out.printf("Área de un círculo de radio 3: %.2f%n", areaCirculo(3));
        System.out.printf("Promedio de 85, 90 y 78: %.2f%n", promedio(85, 90, 78));

        // ---- 4 · Boolean: perfectos para usar en un if ----
        System.out.println("\n[4] Métodos que devuelven boolean");
        int numero = 7;
        if (esPar(numero)) {
            System.out.println(numero + " es par");
        } else {
            System.out.println(numero + " es impar");
        }
        System.out.println("¿16 años es mayor de edad? " + esMayorDeEdad(16));

        // ---- 5 · Con if / switch adentro ----
        System.out.println("\n[5] Métodos con decisiones");
        System.out.println("Nota 95 -> " + clasificarNota(95));
        System.out.println("Nota 72 -> " + clasificarNota(72));
        System.out.println("Nota 45 -> " + clasificarNota(45));
        System.out.println("Día 3   -> " + nombreDelDia(3));
        System.out.println("Día 9   -> " + nombreDelDia(9));

        // ---- 6 · Con ciclos adentro ----
        System.out.println("\n[6] Métodos con ciclos");
        System.out.println("5! = " + factorial(5));
        System.out.println("10! = " + factorial(10));
        System.out.println("Dígitos de 4521: " + contarDigitos(4521));

        // ---- 7 · Sobrecarga ----
        System.out.println("\n[7] Sobrecarga");
        System.out.println("Área cuadrado (lado 4)      : " + area(4));
        System.out.println("Área rectángulo (5 x 3)     : " + area(5, 3));
        System.out.println("Mayor entre 4 y 9           : " + mayor(4, 9));
        System.out.println("Mayor entre 4, 12 y 9       : " + mayor(4, 12, 9));

        // ---- 8 · Alcance y paso por valor ----
        System.out.println("\n[8] Paso por valor");
        int valor = 10;
        System.out.println("Antes de llamar, valor = " + valor);
        intentarDuplicar(valor);
        System.out.println("Después de llamar, valor = " + valor + "  <- ¡no cambió!");
        valor = duplicar(valor);
        System.out.println("Usando return, valor = " + valor + "  <- ahora sí");

        // ---- 9 · Todo junto: calculadora con menú ----
        // Fíjate qué corto y legible queda el main gracias a los métodos.
        Scanner teclado = new Scanner(System.in);
        int opcion = -1;

        while (opcion != 0) {
            mostrarMenu();
            opcion = leerEnteroEnRango(teclado, "Elige una opción: ", 0, 4);

            if (opcion != 0) {
                System.out.print("Primer número : ");
                double a = teclado.nextDouble();
                System.out.print("Segundo número: ");
                double b = teclado.nextDouble();

                if (opcion == 4 && b == 0) {
                    System.out.println("No se puede dividir entre cero.");
                } else {
                    System.out.println("Resultado: " + calcular(opcion, a, b));
                }
            }
        }

        System.out.println("¡Hasta pronto!");
        teclado.close();
    }
}
