// Paquete donde vive la clase
package com.example.demo.config;

import org.springframework.http.ResponseEntity;                          // Respuesta HTTP: codigo + cuerpo
import org.springframework.web.bind.MethodArgumentNotValidException;     // Error que lanza @Valid cuando falla
import org.springframework.web.bind.annotation.ExceptionHandler;         // Metodo que atiende una excepcion
import org.springframework.web.bind.annotation.RestController;           // Para limitar el alcance a los @RestController
import org.springframework.web.bind.annotation.RestControllerAdvice;     // "Asesor" global de los controllers REST

import java.util.LinkedHashMap; // Mapa que conserva el orden de insercion
import java.util.Map;           // { "campo": "mensaje" }

// =====================================================================
//  Manejo de errores de VALIDACION para la API REST (/api/...)
// =====================================================================
// Cuando un @RestController recibe un JSON con @Valid y algun dato no cumple
// (@NotBlank, @Email, @Min...), Spring lanza MethodArgumentNotValidException.
// Este metodo la atrapa y responde 400 con un JSON sencillo:
//
//   { "nombre": "El nombre es obligatorio", "precio": "El precio no puede ser negativo" }
//
// Asi las paginas (productos.js, relaciones.js) pueden mostrar el mensaje en SweetAlert.
// annotations = RestController.class: solo aplica a los controllers REST, no a las vistas Thymeleaf.
@RestControllerAdvice(annotations = RestController.class)
public class ErroresApi {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()                                  // Cada campo que fallo...
                .forEach(e -> errores.putIfAbsent(e.getField(), e.getDefaultMessage())); // ...con su mensaje
        return ResponseEntity.badRequest().body(errores);                        // 400 Bad Request
    }
}
