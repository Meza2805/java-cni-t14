/*
 * ============================================================================
 *  PRÁCTICA 03 · CAJERO AUTOMÁTICO
 * ----------------------------------------------------------------------------
 *  Curso      : Fundamentos de Desarrollo Backend con Java
 *  Temporada  : XIV · CI Nicaragua · 2026
 *  Facilitador: Marvin Rafael Meza Pineda
 * ----------------------------------------------------------------------------
 *  Simulación sencilla de un cajero: pide un PIN, muestra un menú y permite
 *  consultar saldo, depositar y retirar dinero.
 *  Temas que practica:
 *    variables y constantes · Scanner · operadores
 *    if / else if / else · while · switch
 * ============================================================================
 */

import java.util.Scanner;

public class CajeroAutomatico {

    public static void main(String[] args) {

        // ====================================================================
        // PASO 1 · CONSTANTES, VARIABLES Y SCANNER
        // ====================================================================
        final int PIN_CORRECTO = 1234;
        final int MAX_INTENTOS_PIN = 3;
        final double LIMITE_RETIRO = 5000;   // máximo que se puede retirar por operación

        double saldo = 2500;
        int intentosPin = 0;
        boolean accesoPermitido = false;

        Scanner teclado = new Scanner(System.in);

        System.out.println("==========================================");
        System.out.println("      CAJERO AUTOMÁTICO · CI NICARAGUA");
        System.out.println("==========================================");

        // ====================================================================
        // PASO 2 · VALIDAR EL PIN (while + if)
        // ====================================================================
        while (intentosPin < MAX_INTENTOS_PIN && !accesoPermitido) {
            System.out.print("Ingresa tu PIN: ");
            int pin = teclado.nextInt();
            intentosPin++;

            if (pin == PIN_CORRECTO) {
                accesoPermitido = true;
            } else {
                int restantes = MAX_INTENTOS_PIN - intentosPin;
                System.out.println("PIN incorrecto. Te quedan " + restantes + " intento(s).");
            }
        }

        if (!accesoPermitido) {
            System.out.println("\nTarjeta bloqueada. Comunícate con tu banco.");
            teclado.close();
            return;   // termina el programa aquí
        }

        System.out.println("\n¡Acceso concedido!");

        // ====================================================================
        // PASO 3 · MENÚ PRINCIPAL (while + switch)
        // ====================================================================
        int opcion = -1;

        while (opcion != 0) {
            System.out.println();
            System.out.println("------------- MENÚ -------------");
            System.out.println("1. Consultar saldo");
            System.out.println("2. Depositar");
            System.out.println("3. Retirar");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");
            opcion = teclado.nextInt();

            switch (opcion) {
                case 1:
                    System.out.printf("Tu saldo es: C$ %.2f%n", saldo);
                    break;

                case 2:
                    System.out.print("¿Cuánto deseas depositar? C$ ");
                    double deposito = teclado.nextDouble();

                    if (deposito > 0) {
                        saldo += deposito;
                        System.out.printf("Depósito exitoso. Nuevo saldo: C$ %.2f%n", saldo);
                    } else {
                        System.out.println("El monto debe ser mayor que cero.");
                    }
                    break;

                case 3:
                    System.out.print("¿Cuánto deseas retirar? C$ ");
                    double retiro = teclado.nextDouble();

                    // if / else if / else: se revisan las reglas en orden.
                    if (retiro <= 0) {
                        System.out.println("El monto debe ser mayor que cero.");
                    } else if (retiro > LIMITE_RETIRO) {
                        System.out.println("No puedes retirar más de C$ " + LIMITE_RETIRO + " por operación.");
                    } else if (retiro > saldo) {
                        System.out.println("Fondos insuficientes.");
                    } else {
                        saldo -= retiro;
                        System.out.printf("Retira tu dinero. Nuevo saldo: C$ %.2f%n", saldo);
                    }
                    break;

                case 0:
                    System.out.println("Gracias por usar el cajero. ¡Hasta pronto!");
                    break;

                default:
                    System.out.println("Opción no válida.");
            }
        }

        teclado.close();
    }
}
