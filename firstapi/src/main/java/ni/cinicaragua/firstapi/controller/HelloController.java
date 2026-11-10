package ni.cinicaragua.firstapi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ni.cinicaragua.firstapi.model.Persona;

/*
 * ============================================================================
 *  CONTROLADOR: HelloController · Nuestros primeros endpoints
 * ----------------------------------------------------------------------------
 *  Un CONTROLADOR recibe las peticiones HTTP que llegan a la API y decide
 *  qué responder. Cada método anotado con @GetMapping es un ENDPOINT.
 *
 *  @RestController = esta clase es un controlador REST: lo que devuelva cada
 *                    método se envía al cliente (texto o JSON).
 *
 *  Endpoints de esta clase (todos usan el verbo GET):
 *
 *    GET /hello                  → texto fijo
 *    GET /hello/{nombre}         → dato en la RUTA        (@PathVariable)
 *    GET /saludo?nombre=Ana      → dato en la QUERY       (@RequestParam)
 *    GET /persona                → un objeto  → JSON  { ... }
 *    GET /personas               → una lista  → JSON  [ {...}, {...} ]
 *
 *  Pruébalos en el navegador, en Postman o con curl:
 *      curl http://localhost:8080/hello/Rafael
 * ============================================================================
 */
@RestController
public class HelloController {

    // ------------------------------------------------------------------------
    // 1 · ENDPOINT BÁSICO: devuelve un texto
    // ------------------------------------------------------------------------
    // GET http://localhost:8080/hello  →  "Hola, bienvenido a Spring Boot"
    @GetMapping("/hello")
    public String saludar() {
        return "Hola, bienvenido a Spring Boot";
    }

    // ------------------------------------------------------------------------
    // 2 · @PathVariable: el dato viene DENTRO de la ruta
    // ------------------------------------------------------------------------
    // GET http://localhost:8080/hello/Rafael
    //   → "Hola Rafael, bienvenido al curso de Spring Boot"
    // Las llaves {nombre} indican una parte variable de la URL; Spring copia
    // su valor en el parámetro que tiene el mismo nombre.
    @GetMapping("/hello/{nombre}")
    public String saludarPorNombre(@PathVariable String nombre) {
        return "Hola " + nombre + ", bienvenido al curso de Spring Boot";
    }

    // ------------------------------------------------------------------------
    // 3 · @RequestParam: el dato viene DESPUÉS del signo "?"
    // ------------------------------------------------------------------------
    // GET http://localhost:8080/saludo?nombre=Ana  → "Buenos días, Ana"
    // GET http://localhost:8080/saludo             → "Buenos días, estudiante"
    // defaultValue se usa cuando el cliente no envía el parámetro.
    //
    // ¿Cuándo usar cada uno?
    //   @PathVariable → para IDENTIFICAR un recurso      /estudiantes/5
    //   @RequestParam → para FILTRAR u opciones extra     /estudiantes?nombre=Ana
    @GetMapping("/saludo")
    public String saludarConParametro(
            @RequestParam(defaultValue = "estudiante") String nombre) {
        return "Buenos días, " + nombre;
    }

    // ------------------------------------------------------------------------
    // 4 · Devolver UN objeto → Spring lo convierte en JSON
    // ------------------------------------------------------------------------
    // GET http://localhost:8080/persona
    @GetMapping("/persona")
    public Persona obtenerPersona() {
        return new Persona("Rafael", "Pineda", "Masculino", "rafael@correo.com");
    }

    // ------------------------------------------------------------------------
    // 5 · Devolver una LISTA → Spring la convierte en un arreglo JSON [ ]
    // ------------------------------------------------------------------------
    // GET http://localhost:8080/personas
    // List.of(...) crea una lista que no se puede modificar (inmutable).
    @GetMapping("/personas")
    public List<Persona> obtenerPersonas() {
        return List.of(
                new Persona("Rafael", "Pineda", "Masculino", "rafael@correo.com"),
                new Persona("Juan", "López", "Masculino", "juan@correo.com"),
                new Persona("Pedro", "Martínez", "Masculino", "pedro@correo.com"),
                new Persona("Susana", "García", "Femenino", "susana@correo.com"),
                new Persona("María", "Hernández", "Femenino", "maria@correo.com"),
                new Persona("José", "Ruiz", "Masculino", "jose@correo.com"),
                new Persona("Raúl", "Castillo", "Masculino", "raul@correo.com"));
    }
}
