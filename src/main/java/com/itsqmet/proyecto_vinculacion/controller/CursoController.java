package com.itsqmet.proyecto_vinculacion.controller;

import com.itsqmet.proyecto_vinculacion.entity.Curso;
import com.itsqmet.proyecto_vinculacion.service.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class CursoController {

    @Autowired
    private CursoService cursoService;

    // 1. Ruta General - Vista principal de cursos
    @GetMapping("/pages/Admin/cursoVista")
    public String mostrarCursoVista(Model model) {
        List<Curso> cursos = cursoService.mostrarCursos();
        model.addAttribute("cursos", cursos);
        model.addAttribute("totalCursos", cursoService.obtenerNumeroCursos());
        return "pages/Admin/cursoVista";
    }

    // 2. Ruta Form - Formulario para nuevo curso
    @GetMapping("/pages/Admin/cursoForm")
    public String mostrarCursoForm(Model model) {
        model.addAttribute("curso", new Curso());
        return "pages/Admin/cursoForm";
    }

    // 3. Ruta para editar curso
    @GetMapping("/pages/Admin/cursoForm/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Curso> cursoOptional = cursoService.buscarCursoPorId(id);

        if (cursoOptional.isPresent()) {
            model.addAttribute("curso", cursoOptional.get());
            return "pages/Admin/cursoForm";
        } else {
            redirectAttributes.addFlashAttribute("error", "Curso no encontrado");
            return "redirect:/pages/Admin/cursoVista";
        }
    }

    // 4. Guardar curso (nuevo o editado)
    @PostMapping("/guardarCurso")
    public String guardarCurso(@ModelAttribute Curso curso, RedirectAttributes redirectAttributes) {
        try {
            cursoService.guardarCurso(curso);
            String mensaje = curso.getId() == null ? "Curso creado exitosamente" : "Curso actualizado exitosamente";
            redirectAttributes.addFlashAttribute("success", mensaje);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el curso: " + e.getMessage());
        }

        return "redirect:/pages/Admin/cursoVista";
    }

    // 5. Ver detalles de un curso
    @GetMapping("/pages/Admin/cursoDetalle/{id}")
    public String verDetalleCurso(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Curso> cursoOptional = cursoService.buscarCursoPorId(id);

        if (cursoOptional.isPresent()) {
            Curso curso = cursoService.obtenerCursoConMaterias(id);
            model.addAttribute("curso", curso);
            return "pages/Admin/cursoDetalle";
        } else {
            redirectAttributes.addFlashAttribute("error", "Curso no encontrado");
            return "redirect:/pages/Admin/cursoVista";
        }
    }

    // 6. Eliminar curso
    @GetMapping("/eliminarCurso/{id}")
    public String eliminarCurso(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Curso> cursoOptional = cursoService.buscarCursoPorId(id);

            if (cursoOptional.isPresent()) {
                cursoService.eliminarCurso(id);
                redirectAttributes.addFlashAttribute("success", "Curso eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Curso no encontrado");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el curso: " + e.getMessage());
        }

        return "redirect:/pages/Admin/cursoVista";
    }
}