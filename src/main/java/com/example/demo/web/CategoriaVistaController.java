// Paquete de las VISTAS (paginas HTML con Thymeleaf)
package com.example.demo.web;

import com.example.demo.categoria.Categoria;
import com.example.demo.categoria.CategoriaRepository;
import com.example.demo.producto.ProductoRepository;
import jakarta.validation.Valid;                                        // Activa las validaciones de la entidad
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;                       // Devuelve VISTAS, no JSON
import org.springframework.ui.Model;                                    // Datos para la plantilla
import org.springframework.validation.BindingResult;                    // Resultado de la validacion del formulario
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;  // Mensajes "flash" que sobreviven a un redirect

import java.util.Objects;

// =====================================================================
//  CATEGORIAS: el estilo "Thymeleaf clasico" (MVC del lado del servidor)
// =====================================================================
//  - El controller llena el Model y Thymeleaf arma TODA la pagina en el servidor.
//  - El formulario se envia con un POST normal de HTML (sin JavaScript).
//  - Despues de guardar se hace redirect (patron Post/Redirect/Get) para que
//    al recargar la pagina no se envie el formulario dos veces.
//  Compara con ProductoVistaController, donde la pagina usa los endpoints REST.
@Controller

@RequestMapping("/categorias")   // Prefijo de las paginas de categorias (la API sigue en /api/categorias)
@RequiredArgsConstructor
public class CategoriaVistaController {

    private final CategoriaRepository repo;
    private final ProductoRepository productoRepo;

    // ===================== Listado + formulario vacio =====================
    @GetMapping                                       // GET /categorias
    public String listar(Model model) {
        model.addAttribute("categoria", new Categoria()); // Objeto vacio que llenara el formulario (th:object)
        return cargarPagina(model);
    }

    // ===================== Listado + formulario con una categoria para editar =====================
    @GetMapping("/{id}/editar")                       // GET /categorias/3/editar
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        return repo.findById(id)
                .map(c -> {
                    model.addAttribute("categoria", c);   // El formulario aparece lleno con sus datos
                    return cargarPagina(model);
                })
                .orElseGet(() -> {
                    flash.addFlashAttribute("error", "No existe la categoría con id " + id);
                    return "redirect:/categorias";
                });
    }

    // ===================== Guardar (crear o actualizar) =====================
    // @ModelAttribute("categoria"): Spring arma un objeto Categoria con los campos del formulario
    //                               (name="nombre", name="descripcion", name="id").
    // @Valid: revisa @NotBlank, @Size...  BindingResult: guarda los errores EN LUGAR de lanzar excepcion.
    // IMPORTANTE: BindingResult debe ir justo despues del objeto validado.
    @PostMapping("/guardar")                          // POST /categorias/guardar
    public String guardar(@Valid @ModelAttribute("categoria") Categoria categoria,
                          BindingResult resultado,
                          Model model,
                          RedirectAttributes flash) {

        // Regla UNIQUE: no puede existir OTRA categoria con el mismo nombre
        repo.findByNombre(categoria.getNombre())
                .filter(otra -> !Objects.equals(otra.getId(), categoria.getId()))
                .ifPresent(otra -> resultado.rejectValue("nombre", "duplicado",
                        "Ya existe una categoría con ese nombre"));

        if (resultado.hasErrors()) {          // Si algo fallo, volvemos a mostrar la pagina con los errores
            return cargarPagina(model);       // th:errors="*{nombre}" pinta cada mensaje debajo de su campo
        }

        if (categoria.getId() == null) {      // Sin id -> es NUEVA
            repo.save(categoria);             // INSERT INTO categorias ...
            flash.addFlashAttribute("ok", "Categoría \"" + categoria.getNombre() + "\" creada");
        } else {                              // Con id -> ACTUALIZAR la que ya existe
            Categoria actual = repo.findById(categoria.getId()).orElse(null);
            if (actual == null) {
                flash.addFlashAttribute("error", "La categoría ya no existe");
                return "redirect:/categorias";
            }
            actual.setNombre(categoria.getNombre());
            actual.setDescripcion(categoria.getDescripcion());
            repo.save(actual);                // UPDATE categorias SET ... WHERE id = ?
            flash.addFlashAttribute("ok", "Categoría \"" + actual.getNombre() + "\" actualizada");
        }
        return "redirect:/categorias";        // El navegador hace un GET nuevo a /categorias
    }

    // ===================== Eliminar =====================
    // Un formulario HTML solo sabe enviar GET y POST, por eso eliminamos con POST (no con DELETE).
    // La confirmacion la pide SweetAlert2 en el navegador (data-confirmar en la plantilla).
    @PostMapping("/{id}/eliminar")                    // POST /categorias/3/eliminar
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        if (!repo.existsById(id)) {
            flash.addFlashAttribute("error", "No existe la categoría con id " + id);
        } else if (productoRepo.existsByCategoriaId(id)) {   // Integridad referencial (igual que en la API)
            flash.addFlashAttribute("error", "No se puede eliminar: la categoría tiene productos");
        } else {
            repo.deleteById(id);                             // DELETE FROM categorias WHERE id = ?
            flash.addFlashAttribute("ok", "Categoría eliminada");
        }
        return "redirect:/categorias";
    }

    // Datos comunes de la pagina: la tabla de categorias CON sus productos (1 sola consulta)
    private String cargarPagina(Model model) {
        model.addAttribute("categorias", repo.listarConProductos());
        return "categorias";                                  // templates/categorias.html
    }
}
