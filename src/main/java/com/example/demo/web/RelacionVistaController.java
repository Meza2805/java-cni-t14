// Paquete de las VISTAS (paginas HTML con Thymeleaf)
package com.example.demo.web;

import com.example.demo.categoria.CategoriaRepository;
import com.example.demo.cliente.ClienteRepository;
import com.example.demo.producto.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// =====================================================================
//  RELACIONES: ver en una pagina lo que hicimos con JPA
// =====================================================================
//  - @OneToMany  : cada categoria con sus productos       (listarConProductos -> LEFT JOIN FETCH)
//  - @ManyToMany : cada cliente con sus favoritos          (listarConFavoritos -> LEFT JOIN FETCH)
//  - GROUP BY    : total y precio promedio por categoria   (resumen -> LEFT JOIN + GROUP BY + DTO)
//  Agregar y quitar favoritos usa los endpoints REST (static/js/relaciones.js):
//    POST   /api/clientes/{id}/favoritos/{productoId}
//    DELETE /api/clientes/{id}/favoritos/{productoId}
@Controller
@RequiredArgsConstructor
public class RelacionVistaController {

    private final CategoriaRepository categoriaRepo;
    private final ClienteRepository clienteRepo;
    private final ProductoRepository productoRepo;

    @GetMapping("/relaciones")                                           // GET /relaciones
    public String relaciones(Model model) {
        model.addAttribute("categorias", categoriaRepo.listarConProductos()); // 1 consulta con JOIN FETCH
        model.addAttribute("clientes", clienteRepo.listarConFavoritos());     // 1 consulta con JOIN FETCH
        model.addAttribute("resumen", categoriaRepo.resumen());               // LEFT JOIN + GROUP BY
        model.addAttribute("productos", productoRepo.findAll());              // Para el <select> de favoritos
        return "relaciones";                                                  // templates/relaciones.html
    }
}
