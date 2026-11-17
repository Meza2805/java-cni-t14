// Paquete de las VISTAS (paginas HTML con Thymeleaf)
package com.example.demo.web;

import com.example.demo.producto.ProductoRepository;
import com.example.demo.proveedor.Proveedor;
import com.example.demo.proveedor.ProveedorRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

// =====================================================================
//  PRACTICA: pagina /proveedores al estilo "Thymeleaf clasico"
//  (copia de CategoriaVistaController cambiando Categoria por Proveedor)
// =====================================================================
@Controller
@RequestMapping("/proveedores")
@RequiredArgsConstructor
public class ProveedorVistaController {

    private final ProveedorRepository repo;
    private final ProductoRepository productoRepo;

    // ===================== Listado + formulario vacio =====================
    @GetMapping                                       // GET /proveedores
    public String listar(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        return cargarPagina(model);
    }

    // ===================== Listado + formulario para editar =====================
    @GetMapping("/{id}/editar")                       // GET /proveedores/2/editar
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        return repo.findById(id)
                .map(p -> {
                    model.addAttribute("proveedor", p);
                    return cargarPagina(model);
                })
                .orElseGet(() -> {
                    flash.addFlashAttribute("error", "No existe el proveedor con id " + id);
                    return "redirect:/proveedores";
                });
    }

    // ===================== Guardar (crear o actualizar) =====================
    @PostMapping("/guardar")                          // POST /proveedores/guardar
    public String guardar(@Valid @ModelAttribute("proveedor") Proveedor proveedor,
                          BindingResult resultado,
                          Model model,
                          RedirectAttributes flash) {

        // Regla UNIQUE del email: no puede tenerlo OTRO proveedor
        repo.findByEmail(proveedor.getEmail())
                .filter(otro -> !Objects.equals(otro.getId(), proveedor.getId()))
                .ifPresent(otro -> resultado.rejectValue("email", "duplicado",
                        "Ya existe un proveedor con ese email"));

        if (resultado.hasErrors()) {
            return cargarPagina(model);                // Misma pagina con los errores (th:errors)
        }

        if (proveedor.getId() == null) {
            repo.save(proveedor);                      // INSERT INTO proveedores ...
            flash.addFlashAttribute("ok", "Proveedor \"" + proveedor.getNombre() + "\" creado");
        } else {
            Proveedor actual = repo.findById(proveedor.getId()).orElse(null);
            if (actual == null) {
                flash.addFlashAttribute("error", "El proveedor ya no existe");
                return "redirect:/proveedores";
            }
            actual.setNombre(proveedor.getNombre());
            actual.setEmail(proveedor.getEmail());
            actual.setTelefono(proveedor.getTelefono());
            repo.save(actual);                         // UPDATE proveedores SET ... WHERE id = ?
            flash.addFlashAttribute("ok", "Proveedor \"" + actual.getNombre() + "\" actualizado");
        }
        return "redirect:/proveedores";                // Post/Redirect/Get
    }

    // ===================== Eliminar =====================
    @PostMapping("/{id}/eliminar")                    // POST /proveedores/2/eliminar
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        if (!repo.existsById(id)) {
            flash.addFlashAttribute("error", "No existe el proveedor con id " + id);
        } else if (productoRepo.existsByProveedorId(id)) {
            flash.addFlashAttribute("error", "No se puede eliminar: el proveedor tiene productos");
        } else {
            repo.deleteById(id);                       // DELETE FROM proveedores WHERE id = ?
            flash.addFlashAttribute("ok", "Proveedor eliminado");
        }
        return "redirect:/proveedores";
    }

    private String cargarPagina(Model model) {
        model.addAttribute("proveedores", repo.listarConProductos()); // 1 consulta con LEFT JOIN FETCH
        return "proveedores";                                         // templates/proveedores.html
    }
}
