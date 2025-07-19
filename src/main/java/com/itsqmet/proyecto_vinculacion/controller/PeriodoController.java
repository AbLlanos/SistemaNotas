package com.itsqmet.proyecto_vinculacion.controller;

import com.itsqmet.proyecto_vinculacion.entity.PeriodoAcademico;
import com.itsqmet.proyecto_vinculacion.service.PeriodoAcademicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/pages/Admin")
public class PeriodoController {

    @Autowired
    private PeriodoAcademicoService periodoService;

    // 1. Vista general con filtro opcional
    @GetMapping("/periodoAcademicoVista")
    public String mostrarPeriodoAcademicoVista(@RequestParam(required = false) String nombre, Model model) {
        List<PeriodoAcademico> periodos;
        if (nombre != null && !nombre.isEmpty()) {
            periodos = periodoService.listarTodosPeriodosAcademicos().stream()
                    .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                    .toList();
        } else {
            periodos = periodoService.listarTodosPeriodosAcademicos();
        }
        model.addAttribute("periodos", periodos);
        model.addAttribute("param", nombre);
        return "pages/Admin/periodoAcademicoVista";
    }

    // 2. Mostrar formulario para crear
    @GetMapping("/periodoAcademicoForm")
    public String mostrarPeriodoAcademicoForm(Model model) {
        model.addAttribute("periodoAcademico", new PeriodoAcademico());
        return "pages/Admin/periodoAcademicoForm";
    }

    // 3. Guardar o actualizar Periodo
    @PostMapping("/guardarPeriodoAcademico")
    public String guardarPeriodo(@ModelAttribute PeriodoAcademico periodo) {
        periodoService.guardar(periodo);
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
