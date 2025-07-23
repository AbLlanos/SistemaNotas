package com.itsqmet.proyecto_vinculacion.controller;

import com.itsqmet.proyecto_vinculacion.entity.Curso;
import com.itsqmet.proyecto_vinculacion.entity.Estudiante;
import com.itsqmet.proyecto_vinculacion.entity.Materia;
import com.itsqmet.proyecto_vinculacion.entity.NivelEducativo;
import com.itsqmet.proyecto_vinculacion.entity.PeriodoAcademico;
import com.itsqmet.proyecto_vinculacion.service.CursoService;
import com.itsqmet.proyecto_vinculacion.service.EstudianteService;
import com.itsqmet.proyecto_vinculacion.service.MateriaService;
import com.itsqmet.proyecto_vinculacion.service.NivelEducativoService;
import com.itsqmet.proyecto_vinculacion.service.PeriodoAcademicoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class CursoController {

    @Autowired private CursoService cursoService;
    @Autowired private MateriaService materiaService;
    @Autowired private NivelEducativoService nivelEducativoService;
    @Autowired private EstudianteService estudianteService;
    @Autowired private PeriodoAcademicoService periodoAcademicoService; // NUEVO

    // ------------------------------------------------------------
    // 1. Vista principal (lista + filtros)
    // ------------------------------------------------------------
    @GetMapping("/pages/Admin/cursoVista")
    public String mostrarCursoVista(@RequestParam(value = "nombre", required = false) String nombre,
                                    @RequestParam(value = "nivelEducativo", required = false) Long nivelId,
                                    @RequestParam(value = "periodoId", required = false) Long periodoId, // NUEVO
                                    Model model) {

        // filtro avanzado que respeta lo que ya tenías
        List<Curso> cursos = cursoService.filtrarCursosAvanzado(nombre, periodoId, nivelId);

        List<NivelEducativo> niveles = nivelEducativoService.listarTodos();
        List<PeriodoAcademico> periodos = periodoAcademicoService.listarTodosPeriodosAcademicos();

        model.addAttribute("cursos", cursos);
        model.addAttribute("niveles", niveles);
        model.addAttribute("periodos", periodos);

        model.addAttribute("totalCursos", cursos.size());
        model.addAttribute("paramNombre", nombre);
        model.addAttribute("paramNivelId", nivelId);
        model.addAttribute("paramPeriodoId", periodoId);

        return "pages/Admin/cursoVista";
    }

    // ------------------------------------------------------------
    // 2. Formulario nuevo curso (con filtrado opcional por nivel)
    // ------------------------------------------------------------

    @GetMapping("/pages/Admin/cursoForm")
    public String mostrarCursoForm(
            @RequestParam(value = "nivelId", required = false) Long nivelId,
            @RequestParam(value = "periodoId", required = false) Long periodoId,
            Model model) {

        List<NivelEducativo> niveles = nivelEducativoService.listarTodos();
        List<PeriodoAcademico> periodos = periodoAcademicoService.listarTodosPeriodosAcademicos();

        List<Materia> materias;
        List<Estudiante> estudiantes;

        if (nivelId != null) {
            materias = materiaService.listarPorNivelId(nivelId);
            // Cambiar a listar solo visibles en ese nivel
            estudiantes = estudianteService.listarVisiblesPorNivelId(nivelId);
        } else {
            materias = materiaService.listarTodasMaterias();
            // Cambiar a listar solo visibles (sin filtro de nivel)
            estudiantes = estudianteService.listarVisibles();
        }

        model.addAttribute("niveles", niveles);
        model.addAttribute("periodos", periodos);
        model.addAttribute("materias", materias);
        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("nivelSeleccionado", nivelId);
        model.addAttribute("periodoSeleccionado", periodoId);
        model.addAttribute("curso", new Curso()); // curso vacío para crear nuevo

        return "pages/Admin/cursoForm";
    }




    // ----------------------------------------
// Mostrar formulario para editar curso
// ----------------------------------------
    @GetMapping("/pages/Admin/cursoForm/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id,
                                          @RequestParam(value = "nivelId", required = false) Long overrideNivelId,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {

        Optional<Curso> cursoOptional = cursoService.buscarCursoPorId(id);
        if (cursoOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Curso no encontrado.");
            return "redirect:/pages/Admin/cursoVista";
        }

        Curso curso = cursoOptional.get();

        List<NivelEducativo> niveles = nivelEducativoService.listarTodos();
        List<PeriodoAcademico> periodos = periodoAcademicoService.listarTodosPeriodosAcademicos();

        Long nivelId = (overrideNivelId != null)
                ? overrideNivelId
                : (curso.getNivelEducativo() != null ? curso.getNivelEducativo().getId() : null);

        model.addAttribute("nivelSeleccionado", nivelId);

        List<Materia> materias = materiaService.listarPorNivelId(nivelId);

        // También cambiar a estudiantes visibles para ese nivel
        List<Estudiante> estudiantes = estudianteService.listarVisiblesPorNivelId(nivelId);

        model.addAttribute("niveles", niveles);
        model.addAttribute("periodos", periodos);
        model.addAttribute("materias", materias);
        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("nivelSeleccionado", nivelId);
        model.addAttribute("curso", curso);

        return "pages/Admin/cursoForm";
    }

    // ------------------------------------------------------------
// 4. Guardar curso (nuevo o editado)
// ------------------------------------------------------------
    @PostMapping("/pages/Admin/cursoGuardar")
    @Transactional
    public String guardarCurso(@ModelAttribute Curso curso,
                               @RequestParam(value = "materiasSeleccionadas", required = false) List<Long> materiasIds,
                               @RequestParam(value = "estudiantesSeleccionados", required = false) List<Long> estudiantesIds,
                               RedirectAttributes redirectAttributes) {
        try {
            Curso cursoPersistido = (curso.getId() != null)
                    ? cursoService.buscarCursoPorId(curso.getId()).orElse(new Curso())
                    : new Curso();

            List<Estudiante> estudiantesActuales = new ArrayList<>();
            if (cursoPersistido.getEstudiantes() != null) {
                estudiantesActuales.addAll(cursoPersistido.getEstudiantes());
            }

            cursoPersistido.setNombre(curso.getNombre());

            // Cargar PeriodoAcademico completo por id
            if (curso.getPeriodoAcademico() != null && curso.getPeriodoAcademico().getId() != null) {
                PeriodoAcademico pa = periodoAcademicoService.buscarPorId(curso.getPeriodoAcademico().getId());
                cursoPersistido.setPeriodoAcademico(pa);
            } else {
                cursoPersistido.setPeriodoAcademico(null);
            }

            // Cargar NivelEducativo completo por id
            Optional<NivelEducativo> optionalNe = nivelEducativoService.buscarPorId(curso.getNivelEducativo().getId());
            if (optionalNe.isPresent()) {
                cursoPersistido.setNivelEducativo(optionalNe.get());
            } else {
                cursoPersistido.setNivelEducativo(null);
            }

            // Materias
            if (materiasIds != null && !materiasIds.isEmpty()) {
                List<Materia> materias = materiaService.obtenerMateriasPorIds(materiasIds);
                cursoPersistido.setMaterias(materias);
            } else {
                cursoPersistido.setMaterias(new ArrayList<>());
            }

            // Guardar primero para obtener ID si es nuevo
            cursoService.guardarCurso(cursoPersistido);

            if (estudiantesIds != null && !estudiantesIds.isEmpty()) {
                List<Estudiante> estudiantesSeleccionados = estudianteService.obtenerPorIds(estudiantesIds);

                // Detectar estudiantes removidos
                List<Estudiante> estudiantesARemover = new ArrayList<>();
                for (Estudiante actual : estudiantesActuales) {
                    if (!estudiantesIds.contains(actual.getId())) {
                        estudiantesARemover.add(actual);
                    }
                }

                // Remover estudiantes y actualizar relación inversa
                for (Estudiante remover : estudiantesARemover) {
                    if (remover.getCursos() != null) {
                        remover.getCursos().remove(cursoPersistido);
                    }
                }
                estudianteService.guardarTodos(estudiantesARemover);

                // Agregar nuevos estudiantes y actualizar relación inversa
                for (Estudiante nuevo : estudiantesSeleccionados) {
                    if (!estudiantesActuales.contains(nuevo)) {
                        estudiantesActuales.add(nuevo);
                    }
                    if (nuevo.getCursos() == null) {
                        nuevo.setCursos(new ArrayList<>());
                    }
                    if (!nuevo.getCursos().contains(cursoPersistido)) {
                        nuevo.getCursos().add(cursoPersistido);
                    }
                }
                estudianteService.guardarTodos(estudiantesSeleccionados);

                cursoPersistido.setEstudiantes(estudiantesActuales);

            } else {
                // Si no seleccionó estudiantes, remover todos
                for (Estudiante e : estudiantesActuales) {
                    if (e.getCursos() != null) {
                        e.getCursos().remove(cursoPersistido);
                    }
                }
                estudianteService.guardarTodos(estudiantesActuales);
                cursoPersistido.setEstudiantes(new ArrayList<>());
            }

            // Guardar actualización final de curso con estudiantes
            cursoService.guardarCurso(cursoPersistido);

            redirectAttributes.addFlashAttribute("success", "Curso guardado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al guardar el curso: " + e.getMessage());
        }

        return "redirect:/pages/Admin/cursoVista";
    }



    // ------------------------------------------------------------
    // 5. Eliminar curso
    // ------------------------------------------------------------
    @GetMapping("/eliminarCurso/{id}")
    public String eliminarCurso(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Curso> cursoOptional = cursoService.buscarCursoPorId(id);

            if (cursoOptional.isPresent()) {
                cursoService.eliminarCurso(id);
                redirectAttributes.addFlashAttribute("success", "Curso eliminado exitosamente.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Curso no encontrado.");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el curso: " + e.getMessage());
        }

        return "redirect:/pages/Admin/cursoVista";
    }
}
