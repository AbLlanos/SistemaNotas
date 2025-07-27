package com.itsqmet.proyecto_vinculacion.controller;

import com.itsqmet.proyecto_vinculacion.entity.PeriodoAcademico;
import com.itsqmet.proyecto_vinculacion.service.PeriodoAcademicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/pages/Admin")
public class PeriodoController {

    @Autowired
    private PeriodoAcademicoService periodoService;

    // 1. Vista general con filtro opcional
    @GetMapping("/periodoAcademicoVista")
    public String mostrarPeriodoAcademicoVista(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false, defaultValue = "false") boolean mostrarOcultos,
            Model model) {

        List<PeriodoAcademico> periodos = periodoService.listarTodosPeriodosAcademicos();

        // Filtro por nombre
        if (nombre != null && !nombre.isEmpty()) {
            periodos = periodos.stream()
                    .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                    .toList();
        }

        // Filtrar solo visibles si mostrarOcultos = false
        if (!mostrarOcultos) {
            periodos = periodos.stream()
                    .filter(p -> Boolean.TRUE.equals(p.getVisible()))
                    .toList();
        }

        model.addAttribute("periodos", periodos);
        model.addAttribute("paramNombre", nombre);
        model.addAttribute("mostrarOcultos", mostrarOcultos);
        return "pages/Admin/periodoAcademicoVista";
    }

    // Cambiar visibilidad
    @GetMapping("/togglePeriodo/{id}")
    public String toggleVisibilidad(@PathVariable Long id) {
        PeriodoAcademico periodo = periodoService.buscarPorId(id);
        if (periodo != null) {
            periodo.setVisible(!Boolean.TRUE.equals(periodo.getVisible()));
            periodoService.guardar(periodo);
        }
        return "redirect:/pages/Admin/periodoAcademicoVista";
    }

    // 2. Mostrar formulario para crear
    @GetMapping("/periodoAcademicoForm")
    public String mostrarPeriodoAcademicoForm(Model model) {
        model.addAttribute("periodoAcademico", new PeriodoAcademico());
        return "pages/Admin/periodoAcademicoForm";
    }

    // 3. Guardar o actualizar Periodo

    @PostMapping("/guardarPeriodoAcademico")
    public String guardarPeriodoAcademico(@Valid @ModelAttribute("periodoAcademico") PeriodoAcademico periodoAcademico,
                                          BindingResult result,
                                          Model model) {
        if (result.hasErrors()) {
            // Devuelve el formulario con errores
            return "pages/Admin/periodoAcademicoForm";
        }

        // Guardar en base de datos (servicio, repositorio, etc.)
        periodoService.guardar(periodoAcademico);

        return "redirect:/pages/Admin/periodoAcademicoVista";
    }

    // 4. Cargar formulario con datos para editar
    @GetMapping("/editarPeriodo/{id}")
    public String editarPeriodo(@PathVariable Long id, Model model) {
        PeriodoAcademico periodo = periodoService.buscarPorId(id);
        if (periodo == null) {
            return "redirect:/pages/Admin/periodoAcademicoVista";
        }
        model.addAttribute("periodoAcademico", periodo);
        return "pages/Admin/periodoAcademicoForm";
    }

    // 5. Eliminar Periodo
    @GetMapping("/eliminarPeriodo/{id}")
    public String eliminarPeriodo(@PathVariable Long id) {
        periodoService.eliminarPorId(id);
        return "redirect:/pages/Admin/periodoAcademicoVista";
    }
}
