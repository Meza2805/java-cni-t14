/*
 * ============================================================================
 *  PRÁCTICA 01 · RULETA EN CONSOLA
 * ----------------------------------------------------------------------------
 *  Curso      : Fundamentos de Desarrollo Backend con Java
 *  Temporada  : XIV · CI Nicaragua · 2026
 *  Facilitador: Marvin Rafael Meza Pineda
 * ----------------------------------------------------------------------------
 *  Simulación didáctica de una ruleta (números del 0 al 36) que juega con
 *  FICHAS, no con dinero. Integra todo lo visto en la Unidad I:
 *    variables y constantes · Scanner · Math.random() · operadores
 *    if / else · switch · for · while · do-while · break / continue
 * ============================================================================
 */

import java.util.Scanner;

public class Ruleta {

    // "throws InterruptedException" es necesario porque usamos Thread.sleep()
    // para la animación del giro. Por ahora basta con saber que debe ir aquí.
    public static void main(String[] args) throws InterruptedException {

        // ====================================================================
        // PASO 1 · CONSTANTES, VARIABLES Y SCANNER
        // ====================================================================
        final int SALDO_INICIAL = 1000;   // fichas con las que empieza el jugador
        final int PAGO_NUMERO = 35;       // acertar un número paga 35 a 1
        final int PAGO_SIMPLE = 1;        // color o par/impar pagan 1 a 1

        int saldo = SALDO_INICIAL;
        int jugadas = 0;
        int ganadas = 0;
        int perdidas = 0;
        int mayorPremio = 0;

        Scanner teclado = new Scanner(System.in); //Objeto para leer datos desde teclado

        System.out.println("==========================================");
        System.out.println("        RULETA CI NICARAGUA · XIV");
        System.out.println("==========================================");
        System.out.print("¿Cómo te llamas? ");
        String jugador = teclado.nextLine();
        System.out.println("¡Bienvenido, " + jugador + "! Empiezas con " + SALDO_INICIAL + " fichas.");

        int opcion;

        // ====================================================================
        // PASO 4 · MENÚ PRINCIPAL CON DO-WHILE
        // ====================================================================
        // Se repite mientras el jugador no elija salir Y todavía tenga fichas.
        do {
            System.out.println();
            System.out.println("------------- MENÚ -------------");
            System.out.println("Saldo actual: " + saldo + " fichas");
            System.out.println("1. Apostar a un número (paga 35 a 1)");
            System.out.println("2. Apostar a un color  (paga 1 a 1)");
            System.out.println("3. Apostar a par/impar (paga 1 a 1)");
            System.out.println("4. Ver estadísticas");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");
            opcion = teclado.nextInt();

            // Variables para guardar la elección del jugador en esta ronda.
            int numeroElegido = -1;
            String colorElegido = "";
            String paridadElegida = "";

            // ================================================================
            // PASO 5 · ¿A QUÉ APUESTA EL JUGADOR? (switch clásico)
            // ================================================================
            switch (opcion) {
                case 1:
                    // Validación con do-while: se pide al menos una vez y se
                    // repite mientras el número esté fuera del rango 0-36.
                    do {
                        System.out.print("¿A qué número apuestas? (0-36): ");
                        numeroElegido = teclado.nextInt();
                    } while (numeroElegido < 0 || numeroElegido > 36);
                    break;

                case 2:
                    String letraColor;
                    do {
                        System.out.print("¿Rojo o Negro? (R/N): ");
                        letraColor = teclado.next().toUpperCase();
                        // Se repite mientras NO sea "R" Y NO sea "N" (De Morgan).
                    } while (!letraColor.equals("R") && !letraColor.equals("N"));
                    colorElegido = letraColor.equals("R") ? "ROJO" : "NEGRO";
                    break;

                case 3:
                    String letraParidad;
                    do {
                        System.out.print("¿Par o Impar? (P/I): ");
                        letraParidad = teclado.next().toUpperCase();
                    } while (!letraParidad.equals("P") && !letraParidad.equals("I"));
                    paridadElegida = letraParidad.equals("P") ? "PAR" : "IMPAR";
                    break;

                case 4:
                    // PASO 8 · ESTADÍSTICAS
                    System.out.println("\n======= ESTADÍSTICAS =======");
                    System.out.println("Jugadas : " + jugadas);
                    System.out.println("Ganadas : " + ganadas);
                    System.out.println("Perdidas: " + perdidas);
                    if (jugadas > 0) {
                        double porcentaje = (double) ganadas / jugadas * 100;
                        System.out.printf("Efectividad: %.1f%%%n", porcentaje);
                    }
                    System.out.println("Mayor premio: " + mayorPremio + " fichas");
                    continue;   // vuelve al inicio del do-while sin girar la ruleta

                case 0:
                    System.out.println("Saliendo de la mesa...");
                    continue;   // la condición del do-while (opcion != 0) termina el ciclo

                default:
                    System.out.println("Opción no válida.");
                    continue;
            }

            // ================================================================
            // PASO 6 · PEDIR Y VALIDAR LA APUESTA (while)
            // ================================================================
            System.out.print("¿Cuántas fichas apuestas? (1-" + saldo + "): ");
            int apuesta = teclado.nextInt();
            while (apuesta <= 0 || apuesta > saldo) {
                System.out.print("Apuesta inválida. Escribe un valor entre 1 y " + saldo + ": ");
                apuesta = teclado.nextInt();
            }

            // ================================================================
            // PASO 3 · ANIMACIÓN DEL GIRO (for + Thread.sleep)
            // ================================================================
            System.out.print("\nGirando la ruleta: ");
            for (int i = 1; i <= 12; i++) {
                int numeroFalso = (int) (Math.random() * 37);
                System.out.print(numeroFalso + " ");
                Thread.sleep(120);   // pausa de 120 milisegundos
            }
            System.out.println();

            // ================================================================
            // PASO 2 · EL NÚMERO GANADOR CON Math.random()
            // ================================================================
            // Math.random() da 0.0 ... 0.999  ->  * 37  ->  (int)  ->  0 ... 36
            int ganador = (int) (Math.random() * 37);

            // ================================================================
            // PASO 2 · COLOR (switch con varios casos) Y PARIDAD (% y ternario)
            // ================================================================
            String color = switch (ganador) {
                case 0 -> "VERDE";
                case 1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36 -> "ROJO";
                default -> "NEGRO";
            };

            // El 0 no es par ni impar en la ruleta: la casa gana.
            String paridad = (ganador == 0) ? "NINGUNA" : (ganador % 2 == 0 ? "PAR" : "IMPAR");

            System.out.println(">>> Salió el " + ganador + " " + color
                    + (ganador != 0 ? " (" + paridad + ")" : "") + " <<<");

            // ================================================================
            // PASO 7 · ¿GANÓ? (switch como expresión que devuelve boolean)
            // ================================================================
            boolean gano = switch (opcion) {
                case 1 -> ganador == numeroElegido;
                case 2 -> color.equals(colorElegido);
                case 3 -> ganador != 0 && paridad.equals(paridadElegida);
                default -> false;
            };

            int multiplicador = (opcion == 1) ? PAGO_NUMERO : PAGO_SIMPLE;

            // ================================================================
            // PASO 7 · ACTUALIZAR SALDO Y CONTADORES (if / else)
            // ================================================================
            jugadas++;
            if (gano) {
                int premio = apuesta * multiplicador;
                saldo += premio;
                ganadas++;
                if (premio > mayorPremio) {
                    mayorPremio = premio;
                }
                System.out.println("¡GANASTE " + premio + " fichas!");
            } else {
                saldo -= apuesta;
                perdidas++;
                System.out.println("Perdiste " + apuesta + " fichas.");
            }

            if (saldo == 0) {
                System.out.println("\nTe quedaste sin fichas. ¡Fin del juego!");
            }

        } while (opcion != 0 && saldo > 0);

        // ====================================================================
        // PASO 8 · RESUMEN FINAL
        // ====================================================================
        int diferencia = saldo - SALDO_INICIAL;
        System.out.println("\n========== RESUMEN FINAL ==========");
        System.out.println("Jugador     : " + jugador);
        System.out.println("Jugadas     : " + jugadas + "  (ganadas: " + ganadas + ", perdidas: " + perdidas + ")");
        System.out.println("Saldo final : " + saldo + " fichas");

        if (diferencia > 0) {
            System.out.println("Resultado   : ganaste " + diferencia + " fichas.");
        } else if (diferencia < 0) {
            System.out.println("Resultado   : perdiste " + Math.abs(diferencia) + " fichas.");
        } else {
            System.out.println("Resultado   : quedaste igual que al inicio.");
        }
        System.out.println("¡Gracias por jugar, " + jugador + "!");

        teclado.close();
    }
}
