package com.itsqmet.proyecto_vinculacion.controller;

import com.itsqmet.proyecto_vinculacion.entity.Curso;
import com.itsqmet.proyecto_vinculacion.entity.Estudiante;
import com.itsqmet.proyecto_vinculacion.entity.NivelEducativo;
import com.itsqmet.proyecto_vinculacion.service.CursoService;
import com.itsqmet.proyecto_vinculacion.service.EstudianteService;
import com.itsqmet.proyecto_vinculacion.service.NivelEducativoService;
import com.itsqmet.proyecto_vinculacion.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class EstudianteController {

    @Autowired private EstudianteService estudianteService;
    @Autowired private NivelEducativoService nivelEducativoService;
    @Autowired private CursoService cursoService;
    @Autowired
    private UsuarioService usuarioService;



    /* ==========================================
       1. Vista principal con filtros + visibilidad
       ========================================== */
    @GetMapping("/pages/Admin/estudianteVista")
    public String mostrarEstudianteVista(
            Model model,
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "cedula", required = false) String cedula,
            @RequestParam(name = "mostrarOcultos", required = false, defaultValue = "false") boolean mostrarOcultos) {

        List<Estudiante> estudiantes;

        if (mostrarOcultos) {

            if ((nombre == null || nombre.isEmpty()) && (cedula == null || cedula.isEmpty())) {
                estudiantes = estudianteService.listarTodosEstudiantes();
            } else if (nombre != null && !nombre.isEmpty() && (cedula == null || cedula.isEmpty())) {
                estudiantes = estudianteService.buscarPorNombre(nombre);
            } else if ((nombre == null || nombre.isEmpty()) && cedula != null && !cedula.isEmpty()) {
                estudiantes = estudianteService.buscarPorCedulaFiltro(cedula);
            } else {
                estudiantes = estudianteService.buscarPorNombreYCedula(nombre, cedula);
            }
        } else {

            if ((nombre == null || nombre.isEmpty()) && (cedula == null || cedula.isEmpty())) {
                estudiantes = estudianteService.listarVisibles();
            } else if (nombre != null && !nombre.isEmpty() && (cedula == null || cedula.isEmpty())) {
                estudiantes = estudianteService.buscarVisiblesPorNombre(nombre);
            } else if ((nombre == null || nombre.isEmpty()) && cedula != null && !cedula.isEmpty()) {
                estudiantes = estudianteService.buscarVisiblesPorCedulaFiltro(cedula);
            } else {
                estudiantes = estudianteService.buscarVisiblesPorNombreYCedula(nombre, cedula);
            }
        }

        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("paramNombre", nombre);
        model.addAttribute("paramCedula", cedula);
        model.addAttribute("mostrarOcultos", mostrarOcultos);

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
        est.setVisible(true); // por defecto visible
        model.addAttribute("estudiante", est);

        model.addAttribute("niveles", nivelEducativoService.listarTodos());
        model.addAttribute("cursos", cursoService.listarPorNivelId(nivelId));
        model.addAttribute("nivelSeleccionado", nivelId);

        return "pages/Admin/estudianteForm";
    }

    /* ==========================================
   3. Guardar Estudiante (nuevo o editado)
   ========================================== */
    @PostMapping("/pages/Admin/guardarEstudiante")
    public String guardarEstudiante(@ModelAttribute("estudiante") Estudiante estudiante,
                                    BindingResult result,
                                    RedirectAttributes redirectAttributes,
                                    Model model,
                                    @RequestParam(value = "nivelId", required = false) Long nivelId) {

        boolean esEdicion = estudiante.getId() != null;

        // Verificar duplicado por email
        Optional<Estudiante> estudiantePorEmail = estudianteService.buscarPorEmail(estudiante.getEmail());
        if (estudiantePorEmail.isPresent()) {
            Estudiante existente = estudiantePorEmail.get();
            if (!esEdicion || !existente.getId().equals(estudiante.getId())) {
                result.rejectValue("email", "error.estudiante", "Ya existe un estudiante con este correo.");
            }
        }

        // Verificar duplicado por cédula
        Optional<Estudiante> estudiantePorCedula = estudianteService.buscarOptionalPorCedula(estudiante.getCedula());
        if (estudiantePorCedula.isPresent()) {
            Estudiante existente = estudiantePorCedula.get();
            if (!esEdicion || !existente.getId().equals(estudiante.getId())) {
                result.rejectValue("cedula", "error.estudiante", "Ya existe un estudiante con esta cédula.");
            }
        }

        // Si hay errores, recargar datos del formulario
        if (result.hasErrors()) {
            model.addAttribute("niveles", nivelEducativoService.listarTodos());
            model.addAttribute("cursos", nivelId != null ? cursoService.listarPorNivelId(nivelId) : Collections.emptyList());
            model.addAttribute("nivelSeleccionado", nivelId);
            return "pages/Admin/estudianteForm";
        }

        // Asignar NivelEducativo antes de guardar
        Optional<NivelEducativo> nivelOpt = nivelEducativoService.buscarNivelPorId(nivelId);
        if (nivelOpt.isPresent()) {
            estudiante.setNivelEducativo(nivelOpt.get());
        } else {
            result.reject("nivelId", "Nivel educativo no válido.");
        }

        if (esEdicion) {
            Estudiante estudianteExistente = estudianteService.buscarEstudiantePorId(estudiante.getId()).orElse(null);
            if (estudianteExistente != null) {
                if (estudiante.getCursos() == null || estudiante.getCursos().isEmpty()) {
                    estudiante.setCursos(estudianteExistente.getCursos());
                }
            }
        }


        try {
            estudiante.setRol("ESTUDIANTE");
            estudianteService.guardarEstudiante(estudiante);
            redirectAttributes.addFlashAttribute("success", "Estudiante guardado correctamente.");
            return "redirect:/pages/Admin/estudianteVista";
        } catch (DataIntegrityViolationException ex) {
            result.reject("error.general", "Ya existe un estudiante con ese email o cédula.");
            model.addAttribute("niveles", nivelEducativoService.listarTodos());
            model.addAttribute("cursos", nivelId != null ? cursoService.listarPorNivelId(nivelId) : Collections.emptyList());
            model.addAttribute("nivelSeleccionado", nivelId);
            return "pages/Admin/estudianteForm";
        }
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
