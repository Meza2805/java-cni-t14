// Paquete de las VISTAS (paginas HTML con Thymeleaf)
package com.example.demo.web;

import com.example.demo.categoria.CategoriaRepository;
import com.example.demo.cliente.ClienteRepository;
import com.example.demo.producto.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;           // @Controller: devuelve VISTAS (HTML), no JSON
import org.springframework.ui.Model;                        // "Mochila" de datos que viaja del controller a la plantilla
import org.springframework.web.bind.annotation.GetMapping;

// =====================================================================
//  @Controller vs @RestController
// =====================================================================
//  @RestController -> el metodo devuelve DATOS y Spring los convierte en JSON.
//  @Controller     -> el metodo devuelve el NOMBRE de una plantilla.
//                     "index" = src/main/resources/templates/index.html
@Controller
@RequiredArgsConstructor
public class InicioController {

    private final CategoriaRepository categoriaRepo;
    private final ProductoRepository productoRepo;
    private final ClienteRepository clienteRepo;

    // ===================== Pagina de inicio =====================
    @GetMapping("/")                                           // GET http://localhost:8080/
    public String inicio(Model model) {
        model.addAttribute("totalCategorias", categoriaRepo.count()); // En la plantilla: ${totalCategorias}
        model.addAttribute("totalProductos", productoRepo.count());
        model.addAttribute("totalClientes", clienteRepo.count());
        return "index";                                        // Thymeleaf busca templates/index.html
    }
}
