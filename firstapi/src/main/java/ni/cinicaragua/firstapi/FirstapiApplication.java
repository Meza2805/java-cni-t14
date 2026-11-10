/*
 * ============================================================================
 *  FIRSTAPI · Primera API REST con Spring Boot
 * ----------------------------------------------------------------------------
 *  Curso      : Fundamentos de Desarrollo Backend con Java
 *  Temporada  : XIV · CI Nicaragua · 2026
 *  Facilitador: Marvin Rafael Meza Pineda
 * ----------------------------------------------------------------------------
 *  Esta es la CLASE PRINCIPAL: el punto de entrada de toda la aplicación.
 *  Al ejecutar main(), Spring Boot:
 *    1. Busca todas las clases con anotaciones (@RestController, etc.)
 *       dentro de este paquete y sus subpaquetes.
 *    2. Las crea y las conecta entre sí automáticamente.
 *    3. Levanta un servidor web (Tomcat) en http://localhost:8080
 *
 *  Organización del proyecto (paquetes):
 *    ni.cinicaragua.firstapi
 *     ┣ controller/  → reciben las peticiones HTTP (los endpoints)
 *     ┗ model/       → clases que representan los datos (Persona, Estudiante)
 *
 *  ¿Cómo ejecutarlo?
 *    - En VS Code: abre este archivo y pulsa ▶ Run encima de main.
 *    - En la terminal, dentro de la carpeta firstapi:
 *          ./mvnw spring-boot:run        (Linux / macOS)
 *          mvnw.cmd spring-boot:run      (Windows)
 * ============================================================================
 */
package ni.cinicaragua.firstapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication activa la configuración automática de Spring Boot
// y le indica que busque componentes a partir de ESTE paquete.
@SpringBootApplication
public class FirstapiApplication {

	public static void main(String[] args) {
		// Arranca Spring Boot y el servidor web.
		SpringApplication.run(FirstapiApplication.class, args);
	}

}
