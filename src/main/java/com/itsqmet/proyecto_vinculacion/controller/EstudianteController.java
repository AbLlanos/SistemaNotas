package com.itsqmet.proyecto_vinculacion.controller;

import com.itsqmet.proyecto_vinculacion.entity.Curso;
import com.itsqmet.proyecto_vinculacion.entity.Estudiante;
import com.itsqmet.proyecto_vinculacion.entity.NivelEducativo;
import com.itsqmet.proyecto_vinculacion.service.CursoService;
import com.itsqmet.proyecto_vinculacion.service.EstudianteService;
import com.itsqmet.proyecto_vinculacion.service.NivelEducativoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class EstudianteController {

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private NivelEducativoService nivelEducativoService;

    @Autowired
    private CursoService cursoService;

    /* ==========================================
       1. Vista principal con filtros por nombre/cedula
       ========================================== */
    @GetMapping("/pages/Admin/estudianteVista")
    public String mostrarEstudianteVista(
            Model model,
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "cedula", required = false) String cedula) {

        List<Estudiante> estudiantes;
        if ((nombre == null || nombre.isEmpty()) && (cedula == null || cedula.isEmpty())) {
            estudiantes = estudianteService.listarTodosEstudiantes();
        } else if (nombre != null && !nombre.isEmpty() && (cedula == null || cedula.isEmpty())) {
            estudiantes = estudianteService.buscarPorNombre(nombre);
        } else if ((nombre == null || nombre.isEmpty()) && cedula != null && !cedula.isEmpty()) {
            estudiantes = estudianteService.buscarPorCedulaFiltro(cedula);
        } else {
            estudiantes = estudianteService.buscarPorNombreYCedula(nombre, cedula);
        }

        model.addAttribute("estudiantes", estudiantes);
        return "pages/Admin/estudianteVista";
    }


    /* ==========================================
       2. Formulario NUEVO Estudiante
       ========================================== */
    @GetMapping("/pages/Admin/estudianteForm")
    public String mostrarEstudianteForm(
            @RequestParam(value = "nivelId", required = false) Long nivelId,
            Model model) {

        Estudiante est = new Estudiante();  // nuevo
        model.addAttribute("estudiante", est);

        // niveles siempre
        model.addAttribute("niveles", nivelEducativoService.listarTodos());

        // cursos filtrados por nivel (o todos si null)
        model.addAttribute("cursos", cursoService.listarPorNivelId(nivelId));

        // para que el select recuerde la selección
        model.addAttribute("nivelSeleccionado", nivelId);

        return "pages/Admin/estudianteForm";
    }

    /* ==========================================
       3. Guardar Estudiante (nuevo o editado)
       ========================================== */
    @PostMapping("/guardarEstudiante")
    public String guardarEstudiante(
            @ModelAttribute Estudiante estudiante,
            @RequestParam(name = "nivelId", required = false) Long nivelId,
            @RequestParam(name = "cursosSeleccionados", required = false) List<Long> cursosIds,
            RedirectAttributes redirectAttributes) {

        try {
            estudiante.setRol("ESTUDIANTE");

            // Nivel
            if (nivelId != null) {
                NivelEducativo nivel = nivelEducativoService.buscarPorId(nivelId).orElse(null);
                estudiante.setNivelEducativo(nivel);
            } else {
                estudiante.setNivelEducativo(null);
            }

            // Cursos
            if (cursosIds == null) cursosIds = Collections.emptyList();
            List<Curso> cursos = cursoService.obtenerCursosPorIds(cursosIds);
            estudiante.setCursos(cursos);

            estudianteService.guardarEstudiante(estudiante);
            redirectAttributes.addFlashAttribute("success", "Estudiante guardado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el estudiante: " + e.getMessage());
        }

        return "redirect:/pages/Admin/estudianteVista";
    }


    /* ==========================================
       4. Editar Estudiante
       ========================================== */
    @GetMapping("/editarEstudiante/{id}")
    public String editarEstudiante(
            @PathVariable Long id,
            @RequestParam(value = "nivelId", required = false) Long nivelId,
            Model model) {

        Estudiante est = estudianteService.buscarEstudiantePorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado: " + id));

        // Si no llega nivelId (porque cargaste directo "editar"), usa el del estudiante
        if (nivelId == null && est.getNivelEducativo() != null) {
            nivelId = est.getNivelEducativo().getId();
        }

        model.addAttribute("estudiante", est);
        model.addAttribute("niveles", nivelEducativoService.listarTodos());
        model.addAttribute("cursos", cursoService.listarPorNivelId(nivelId));
        model.addAttribute("nivelSeleccionado", nivelId);

        return "pages/Admin/estudianteForm";
    }


    /* ==========================================
       5. Eliminar Estudiante
       ========================================== */
    @GetMapping("/eliminarEstudiante/{id}")
    public String eliminarEstudiante(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            estudianteService.eliminarEstudiante(id);
            redirectAttributes.addFlashAttribute("success", "Estudiante eliminado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar: " + e.getMessage());
        }
        return "redirect:/pages/Admin/estudianteVista";
    }
}
