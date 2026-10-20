/*
 * ============================================================================
 *  PRÁCTICA 02 · ADIVINA EL NÚMERO
 * ----------------------------------------------------------------------------
 *  Curso      : Fundamentos de Desarrollo Backend con Java
 *  Temporada  : XIV · CI Nicaragua · 2026
 *  Facilitador: Marvin Rafael Meza Pineda
 * ----------------------------------------------------------------------------
 *  La computadora "piensa" un número del 1 al 100 y el jugador intenta
 *  adivinarlo. En cada intento recibe una pista: "más alto" o "más bajo".
 *  Temas que practica:
 *    variables y constantes · Scanner · Math.random()
 *    if / else if / else · while · switch
 * ============================================================================
 */

import java.util.Scanner;

public class AdivinaNumero {

    public static void main(String[] args) {

        // ====================================================================
        // PASO 1 · CONSTANTES, VARIABLES Y SCANNER
        // ====================================================================
        final int MAXIMO = 100;          // el número secreto va de 1 a MAXIMO
        final int MAX_INTENTOS = 7;      // oportunidades que tiene el jugador

        Scanner teclado = new Scanner(System.in);

        // Math.random() da 0.0 ... 0.999  ->  * 100  ->  (int)  ->  0 ... 99  ->  + 1  ->  1 ... 100
        int secreto = (int) (Math.random() * MAXIMO) + 1;

        int intentos = 0;
        boolean adivino = false;

        System.out.println("==========================================");
        System.out.println("      ADIVINA EL NÚMERO · CI NICARAGUA");
        System.out.println("==========================================");
        System.out.println("Pensé un número entre 1 y " + MAXIMO + ".");
        System.out.println("Tienes " + MAX_INTENTOS + " intentos para adivinarlo.");

        // ====================================================================
        // PASO 2 · CICLO PRINCIPAL CON WHILE
        // ====================================================================
        // Se repite mientras queden intentos Y el jugador no haya adivinado.
        while (intentos < MAX_INTENTOS && !adivino) {

            System.out.print("\nIntento " + (intentos + 1) + " de " + MAX_INTENTOS + ". Tu número: ");
            int numero = teclado.nextInt();

            // ================================================================
            // PASO 3 · VALIDAR EL RANGO (if)
            // ================================================================
            // Un número fuera de rango NO gasta intento.
            if (numero < 1 || numero > MAXIMO) {
                System.out.println("Ese número no está entre 1 y " + MAXIMO + ". Intenta de nuevo.");
                continue;
            }

            intentos++;

            // ================================================================
            // PASO 4 · DAR LA PISTA (if / else if / else)
            // ================================================================
            if (numero == secreto) {
                adivino = true;
                System.out.println("¡CORRECTO! El número era " + secreto + ".");
            } else if (numero < secreto) {
                System.out.println("Más ALTO");
            } else {
                System.out.println("Más BAJO");
            }
        }

        // ====================================================================
        // PASO 5 · RESULTADO FINAL
        // ====================================================================
        System.out.println("\n========== RESULTADO ==========");

        if (adivino) {
            System.out.println("Lo lograste en " + intentos + " intento(s).");

            // switch clásico: una calificación según los intentos usados.
            // Varios case seguidos sin break comparten el mismo bloque.
            switch (intentos) {
                case 1:
                    System.out.println("Calificación: ¡Increíble! ¿Lees mentes?");
                    break;
                case 2:
                case 3:
                    System.out.println("Calificación: Excelente");
                    break;
                case 4:
                case 5:
                    System.out.println("Calificación: Muy bien");
                    break;
                default:
                    System.out.println("Calificación: ¡Por poco!");
            }
        } else {
            System.out.println("Se acabaron los intentos. El número era " + secreto + ".");
        }

        teclado.close();
    }
}
