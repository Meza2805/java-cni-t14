package ni.cinicaragua.firstapi;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

/*
 * ============================================================================
 *  PRUEBAS DE LOS ENDPOINTS con MockMvc
 * ----------------------------------------------------------------------------
 *  MockMvc simula peticiones HTTP SIN abrir el navegador ni Postman.
 *  Cada prueba sigue el patrón:
 *      perform(petición)  →  andExpect(lo que esperamos recibir)
 *
 *  Ejecutar:  ./mvnw test
 * ============================================================================
 */
@SpringBootTest
@AutoConfigureMockMvc
// Reinicia la aplicación después de cada prueba para que la lista de
// estudiantes vuelva a tener sus 3 datos iniciales.
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EndpointsTests {

    @Autowired
    private MockMvc mockMvc;

    // ---------------------------- HelloController ----------------------------

    @Test
    void saludarPorNombreIncluyeElNombre() throws Exception {
        mockMvc.perform(get("/hello/Rafael"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hola Rafael, bienvenido al curso de Spring Boot"));
    }

    @Test
    void personaDevuelveTodosLosCamposEnJson() throws Exception {
        // jsonPath("$.campo") lee un campo del JSON de respuesta.
        mockMvc.perform(get("/persona"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombres").value("Rafael"))
                .andExpect(jsonPath("$.sexo").value("Masculino"))
                .andExpect(jsonPath("$.correo").value("rafael@correo.com"));
    }

    // -------------------------- EstudianteController -------------------------

    @Test
    void listarDevuelveLosEstudiantesIniciales() throws Exception {
        mockMvc.perform(get("/api/estudiantes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void buscarIdInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/api/estudiantes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearEstudianteValidoDevuelve201() throws Exception {
        String json = """
                { "nombres": "Mario", "apellidos": "Ruiz",
                  "correo": "mario@correo.com", "edad": 16 }
                """;
        mockMvc.perform(post("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4));
    }

    @Test
    void crearEstudianteConCorreoInvalidoDevuelve400() throws Exception {
        String json = """
                { "nombres": "Mario", "apellidos": "Ruiz",
                  "correo": "esto-no-es-un-correo", "edad": 16 }
                """;
        mockMvc.perform(post("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eliminarEstudianteDevuelve204() throws Exception {
        mockMvc.perform(delete("/api/estudiantes/1"))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/estudiantes/1"))
                .andExpect(status().isNotFound());
    }
}
