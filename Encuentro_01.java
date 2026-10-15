/*
 * ============================================================================
 *  ENCUENTRO 01 · PRIMEROS PASOS CON JAVA
 * ----------------------------------------------------------------------------
 *  Curso      : Fundamentos de Desarrollo Backend con Java
 *  Temporada  : XIV · CI Nicaragua · 2026
 *  Facilitador: Marvin Rafael Meza Pineda
 * ----------------------------------------------------------------------------
 *  En este archivo vas a ver, en orden:
 *    1. Imprimir en pantalla        (System.out.println / print / printf)
 *    2. Variables y tipos de datos  (int, double, boolean, char, String...)
 *    3. Leer datos del teclado      (la clase Scanner)
 *    4. Decisiones con if / else if / else
 *    5. Decisiones múltiples con switch
 *    6. Ciclo for
 *    7. Ciclo while
 *    8. Ciclo do-while (con un menú)
 *
 *  ¿Cómo ejecutarlo?
 *    - En VS Code: abre este archivo y pulsa el botón ▶ Run.
 *    - En la terminal, dentro de esta carpeta:
 *          javac Encuentro.java      (compila y genera Encuentro.class)
 *          java Encuentro            (la JVM ejecuta el programa)
 * ============================================================================
 */

// "import" trae a nuestro programa una clase que vive en otro paquete de Java.
// Scanner está en el paquete java.util y nos permite leer lo que el usuario escribe.
import java.util.Scanner;

// En Java todo el código vive dentro de una CLASE.
// Regla de oro: el nombre de la clase pública debe ser IGUAL al nombre del archivo
// (Encuentro  ->  Encuentro.java), respetando mayúsculas y minúsculas.
public class Encuentro {

    // El método main es el PUNTO DE ENTRADA: es lo primero que ejecuta la JVM.
    //   public  -> se puede llamar desde fuera de la clase
    //   static  -> pertenece a la clase, no hace falta crear un objeto
    //   void    -> no devuelve ningún valor
    //   String[] args -> recibe argumentos desde la terminal (no los usaremos hoy)
    public static void main(String[] args) {

        // ====================================================================
        // 1. IMPRIMIR EN PANTALLA
        // ====================================================================

        // println imprime el texto y SALTA a la siguiente línea.
        System.out.println("==============================================");
        System.out.println("   ¡Bienvenido al Encuentro 01 de Java!");
        System.out.println("==============================================");

        // print imprime el texto pero NO salta de línea.
        System.out.print("Esto queda ");
        System.out.print("en la misma línea.");
        System.out.println(); // un println vacío solo hace un salto de línea

        // Secuencias de escape: caracteres especiales dentro de un texto.
        //   \n -> salto de línea     \t -> tabulación     \" -> comillas dobles
        System.out.println("Línea 1\nLínea 2");
        System.out.println("Columna A\tColumna B");
        System.out.println("Java dice: \"Hola, CI Nicaragua\"");
        System.out.println();


        // ====================================================================
        // 2. VARIABLES Y TIPOS DE DATOS
        // ====================================================================
        // Una variable es un espacio en memoria con un nombre.
        // Sintaxis:   tipo nombreVariable = valor;

        // --- Tipos primitivos (los más usados) ---
        int horasCurso = 30;           // números enteros
        double notaMinima = 70.0;      // números con decimales
        boolean hayClase = true;       // solo puede ser true (verdadero) o false (falso)
        char seccion = 'A';            // UN solo carácter, va entre comillas SIMPLES
        long poblacion = 7_000_000L;   // enteros muy grandes; el sufijo L indica long
                                       // (los guiones bajos _ solo ayudan a leer el número)

        // --- String: no es primitivo, es una CLASE para manejar texto ---
        // Va entre comillas DOBLES y se escribe con S mayúscula.
        String curso = "Fundamentos de Backend con Java";

        // --- var: Java deduce el tipo según el valor asignado ---
        var temporada = "XIV";         // Java deduce que es String

        // --- final: crea una CONSTANTE, su valor ya no puede cambiar ---
        // Por convención, las constantes se escriben en MAYÚSCULAS.
        final int SEMANAS = 6;
        // SEMANAS = 7;   // <- si quitas el comentario, el compilador marcará ERROR

        // El operador + une (concatena) textos con otros valores.
        System.out.println("--- Datos del curso ---");
        System.out.println("Curso      : " + curso);
        System.out.println("Temporada  : " + temporada);
        System.out.println("Duración   : " + horasCurso + " horas en " + SEMANAS + " semanas");
        System.out.println("Sección    : " + seccion);
        System.out.println("¿Hay clase?: " + hayClase);
        System.out.println("Población NI (aprox.): " + poblacion);

        // printf permite dar FORMATO a la salida:
        //   %s -> texto   %d -> entero   %.1f -> decimal con 1 decimal   %n -> salto de línea
        System.out.printf("Nota mínima para aprobar: %.1f%n", notaMinima);

        // Operaciones aritméticas:  +  -  *  /  %
        int clasesPorSemana = 2;
        int totalClases = SEMANAS * clasesPorSemana;          // 6 * 2 = 12
        double horasPorClase = (double) horasCurso / totalClases; // 30 / 12 = 2.5
        // (double) es un "casting": convierte el int a double para no perder decimales.
        // Sin el casting, 30 / 12 entre enteros daría 2 (se pierde el .5).
        System.out.println("Total de encuentros: " + totalClases);
        System.out.println("Horas por encuentro: " + horasPorClase);
        System.out.println("30 % 12 (residuo)  : " + (horasCurso % totalClases));
        System.out.println();


        // ====================================================================
        // 3. LEER DATOS DEL TECLADO CON SCANNER
        // ====================================================================
        // Creamos un objeto Scanner conectado a System.in (el teclado).
        Scanner teclado = new Scanner(System.in);

        System.out.println("--- Vamos a conocernos ---");

        // nextLine() lee una línea completa de texto (incluye espacios).
        System.out.print("¿Cuál es tu nombre? ");
        String nombre = teclado.nextLine();

        // nextInt() lee un número entero.
        System.out.print("¿Cuántos años tienes? ");
        int edad = teclado.nextInt();

        // nextDouble() lee un número decimal.
        // OJO: según la configuración regional de tu computadora, puede que debas
        // escribir el decimal con coma (85,5) en lugar de punto (85.5).
        System.out.print("¿Qué nota sacaste en tu último examen? ");
        double nota = teclado.nextDouble();

        // ⚠ TRUCO IMPORTANTE:
        // nextInt() y nextDouble() leen el número pero dejan pendiente el "Enter".
        // Si después usamos nextLine(), leería ese Enter vacío y se "saltaría" la pregunta.
        // Por eso limpiamos el Enter pendiente con un nextLine() extra:
        teclado.nextLine();

        System.out.println("\n¡Mucho gusto, " + nombre + "!");
        System.out.println();


        // ====================================================================
        // 4. DECISIONES CON IF / ELSE IF / ELSE
        // ====================================================================
        // if evalúa una condición (algo que da true o false).
        // Operadores de comparación:  ==  !=  >  <  >=  <=
        // Operadores lógicos:         &&  (y)     ||  (o)     !  (no)

        System.out.println("--- Evaluando tu nota ---");

        if (nota >= 90) {
            System.out.println("Excelente, " + nombre + ". ¡Sigue así!");
        } else if (nota >= notaMinima) {
            System.out.println("Aprobado. Buen trabajo, " + nombre + ".");
        } else {
            System.out.println("Reprobado. ¡No te rindas, vamos a mejorar!");
        }

        // Condición combinada con && (las DOS condiciones deben ser verdaderas).
        // Requisito del curso: tener 14 años cumplidos.
        if (edad >= 14 && hayClase) {
            System.out.println("Cumples la edad para este curso y hoy hay clase.");
        }

        // Operador ternario: un if/else corto que DEVUELVE un valor.
        //   condición ? valorSiEsVerdadero : valorSiEsFalso
        String etapa = (edad >= 18) ? "mayor de edad" : "menor de edad";
        System.out.println("Eres " + etapa + ".");
        System.out.println();


        // ====================================================================
        // 5. DECISIONES MÚLTIPLES CON SWITCH
        // ====================================================================
        // switch compara UNA variable contra varios valores fijos.

        System.out.print("Escribe un número de día (1 = lunes ... 7 = domingo): ");
        int dia = teclado.nextInt();

        // --- Forma moderna (Java 14+): con flecha ->, sin necesidad de break ---
        // Aquí switch DEVUELVE un valor que guardamos en la variable nombreDia.
        String nombreDia = switch (dia) {
            case 1 -> "lunes";
            case 2 -> "martes";
            case 3 -> "miércoles";
            case 4 -> "jueves";
            case 5 -> "viernes";
            case 6, 7 -> "fin de semana";  // varios casos en una sola línea
            default -> "un día que no existe";
        };
        System.out.println("Elegiste: " + nombreDia);

        // --- Forma clásica: con dos puntos : y break ---
        // break es OBLIGATORIO para salir del switch; si lo olvidas, el programa
        // "cae" y ejecuta también los casos de abajo.
        switch (dia) {
            case 2:
            case 4:
                System.out.println("¡Hoy hay clase de Java! (martes y jueves)");
                break;
            case 6:
            case 7:
                System.out.println("Descansa... pero repasa un poquito.");
                break;
            default:
                System.out.println("Hoy no hay clase: aprovecha para practicar.");
                break;
        }
        System.out.println();


        // ====================================================================
        // 6. CICLO FOR
        // ====================================================================
        // Se usa cuando SABEMOS cuántas veces repetir.
        //   for (inicio; condición; incremento) { ... }

        System.out.println("--- Calendario del curso (ciclo for) ---");
        for (int semana = 1; semana <= SEMANAS; semana++) {
            // semana++ es lo mismo que: semana = semana + 1
            System.out.println("Semana " + semana + ": martes y jueves");
        }

        // Ejemplo con el número que dio el usuario: su tabla de multiplicar.
        System.out.print("\n¿Qué tabla de multiplicar quieres ver? ");
        int tabla = teclado.nextInt();
        for (int i = 1; i <= 10; i++) {
            // %2d reserva 2 espacios para el número, así las columnas quedan alineadas.
            System.out.printf("%d x %2d = %d%n", tabla, i, tabla * i);
        }
        System.out.println();


        // ====================================================================
        // 7. CICLO WHILE
        // ====================================================================
        // Repite MIENTRAS la condición sea verdadera.
        // Si la condición es falsa desde el inicio, NO se ejecuta ni una vez.
        // Se usa cuando NO sabemos exactamente cuántas vueltas habrá.

        System.out.println("--- ¿Cuántos números hay que sumar para pasar 100? (while) ---");
        int suma = 0;
        int numero = 0;
        while (suma <= 100) {
            numero++;            // pasamos al siguiente número: 1, 2, 3...
            suma += numero;      // suma += numero es lo mismo que: suma = suma + numero
        }
        System.out.println("Sumando del 1 al " + numero + " se obtiene " + suma);

        // ⚠ Cuidado con el CICLO INFINITO: si dentro del while nada cambia la condición,
        // el programa nunca termina. Para detenerlo en la terminal: Ctrl + C.
        System.out.println();


        // ====================================================================
        // 8. CICLO DO-WHILE (MENÚ INTERACTIVO)
        // ====================================================================
        // Ejecuta el bloque AL MENOS UNA VEZ y después revisa la condición.
        // Es perfecto para menús: siempre queremos mostrar el menú la primera vez.

        int opcion;  // la declaramos afuera para poder usarla en la condición del while

        do {
            System.out.println("========== MENÚ ==========");
            System.out.println("1. Saludar");
            System.out.println("2. Ver mi información");
            System.out.println("3. Contar hacia atrás");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");
            opcion = teclado.nextInt();

            switch (opcion) {
                case 1 -> System.out.println("¡Hola, " + nombre + "! Bienvenido al backend.");
                case 2 -> {
                    // Si un caso tiene varias instrucciones, se agrupan con llaves { }.
                    System.out.println("Nombre: " + nombre);
                    System.out.println("Edad  : " + edad + " años");
                    System.out.println("Nota  : " + nota);
                }
                case 3 -> {
                    // for que cuenta hacia atrás: empieza en 5 y RESTA 1 cada vuelta.
                    for (int c = 5; c >= 1; c--) {
                        System.out.print(c + "... ");
                    }
                    System.out.println("¡Despegue!");
                }
                case 0 -> System.out.println("Saliendo del programa...");
                default -> System.out.println("Opción no válida, intenta de nuevo.");
            }
            System.out.println();

        } while (opcion != 0);   // se repite mientras la opción sea distinta de 0


        // ====================================================================
        // CIERRE
        // ====================================================================
        // Es buena práctica cerrar el Scanner cuando ya no lo usamos,
        // para liberar el recurso (el teclado).
        teclado.close();

        System.out.println("¡Gracias por participar, " + nombre + "! Nos vemos el próximo encuentro.");
    }
}
