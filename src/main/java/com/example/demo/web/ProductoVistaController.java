// Paquete de las VISTAS (paginas HTML con Thymeleaf)
package com.example.demo.web;

import com.example.demo.categoria.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// =====================================================================
//  PRODUCTOS: Thymeleaf + los ENDPOINTS REST que ya teniamos
// =====================================================================
//  Este controller SOLO entrega la pagina (el "cascaron") y la lista de categorias
//  para el <select>. Los datos de la tabla y las operaciones (crear, editar,
//  eliminar) los hace el navegador llamando a la API con fetch():
//
//    GET    /api/productos/listar                 -> llena la tabla (DataTables)
//    POST   /api/productos/crear                  -> boton Guardar (producto nuevo)
//    PUT    /api/productos/actualizar/{id}        -> boton Guardar (editar)
//    PUT    /api/productos/{id}/categoria/{catId} -> cambiar la categoria
//    DELETE /api/productos/{id}/categoria         -> quitar la categoria
//    DELETE /api/productos/eliminar/{id}          -> boton Eliminar (con SweetAlert2)
//
//  El codigo JavaScript esta en static/js/productos.js
@Controller
@RequiredArgsConstructor
public class ProductoVistaController {

    private final CategoriaRepository categoriaRepo;

    @GetMapping("/productos")                                   // GET /productos  (la API es /api/productos)
    public String productos(Model model) {
        model.addAttribute("categorias", categoriaRepo.findAll()); // Para las <option> del <select> (th:each)
        return "productos";                                     // templates/productos.html
    }
}
