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
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
                                    @RequestParam(value = "periodoId", required = false) Long periodoId,
                                    @RequestParam(value = "mostrarOcultos", required = false, defaultValue = "false") boolean mostrarOcultos,
                                    Model model) {

        // Obtener cursos filtrados por nombre, periodo y nivel
        List<Curso> cursos = cursoService.filtrarCursosAvanzado(nombre, periodoId, nivelId);

        // Filtrar cursos según visibilidad del periodo (solo mostrar cursos con periodo visible)
        if (!mostrarOcultos) {
            cursos = cursos.stream()
                    .filter(c -> c.getPeriodoAcademico() != null && Boolean.TRUE.equals(c.getPeriodoAcademico().getVisible()))
                    .toList();
        }

        // Listar todos los niveles educativos
        List<NivelEducativo> niveles = nivelEducativoService.listarTodos();

        // Listar solo periodos académicos visibles
        List<PeriodoAcademico> periodos = periodoAcademicoService.listarTodosPeriodosAcademicos()
                .stream()
                .filter(p -> Boolean.TRUE.equals(p.getVisible()))
                .toList();

        model.addAttribute("cursos", cursos);
        model.addAttribute("niveles", niveles);
        model.addAttribute("periodos", periodos);

        model.addAttribute("totalCursos", cursos.size());
        model.addAttribute("paramNombre", nombre);
        model.addAttribute("paramNivelId", nivelId);
        model.addAttribute("paramPeriodoId", periodoId);
        model.addAttribute("mostrarOcultos", mostrarOcultos);

        return "pages/Admin/cursoVista";
    }

// ------------------------------------------------------------
// Para mostrar formulario nuevo curso
@GetMapping("/pages/Admin/cursoForm")
public String mostrarCursoForm(
        @RequestParam(value = "nivelId", required = false) Long nivelId,
        @RequestParam(value = "periodoId", required = false) Long periodoId,
        Model model) {

    List<NivelEducativo> niveles = nivelEducativoService.listarTodos();

    List<PeriodoAcademico> periodos = periodoAcademicoService.listarTodosPeriodosAcademicos()
            .stream()
            .filter(p -> Boolean.TRUE.equals(p.getVisible()))
            .toList();

    List<Materia> materias;
    List<Estudiante> estudiantes;

    if (nivelId != null) {
        materias = materiaService.listarPorNivelId(nivelId)
                .stream()
                .filter(m -> Boolean.TRUE.equals(m.getVisible()) &&
                        m.getPeriodoAcademico() != null &&
                        Boolean.TRUE.equals(m.getPeriodoAcademico().getVisible()))
                .toList();

        estudiantes = estudianteService.listarVisiblesPorNivelId(nivelId);
    } else {
        materias = materiaService.listarTodasMaterias()
                .stream()
                .filter(m -> Boolean.TRUE.equals(m.getVisible()) &&
                        m.getPeriodoAcademico() != null &&
                        Boolean.TRUE.equals(m.getPeriodoAcademico().getVisible()))
                .toList();

        estudiantes = estudianteService.listarVisibles();
    }

    model.addAttribute("niveles", niveles);
    model.addAttribute("periodos", periodos);
    model.addAttribute("materias", materias);
    model.addAttribute("estudiantes", estudiantes);
    model.addAttribute("nivelSeleccionado", nivelId);
    model.addAttribute("periodoSeleccionado", periodoId);
    model.addAttribute("curso", new Curso());
    model.addAttribute("filtroAplicado", nivelId != null);

    return "pages/Admin/cursoForm";  // Asegúrate que el nombre aquí sea el de tu plantilla con el formulario
}

    // Para mostrar formulario editar curso
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

        List<PeriodoAcademico> periodos = periodoAcademicoService.listarTodosPeriodosAcademicos()
                .stream()
                .filter(p -> Boolean.TRUE.equals(p.getVisible()))
                .toList();

        Long nivelId = (overrideNivelId != null)
                ? overrideNivelId
                : (curso.getNivelEducativo() != null ? curso.getNivelEducativo().getId() : null);

        model.addAttribute("nivelSeleccionado", nivelId);

        List<Materia> materias = (nivelId != null) ?
                materiaService.listarPorNivelId(nivelId)
                        .stream()
                        .filter(m -> Boolean.TRUE.equals(m.getVisible()) &&
                                m.getPeriodoAcademico() != null &&
                                Boolean.TRUE.equals(m.getPeriodoAcademico().getVisible()))
                        .toList()
                : Collections.emptyList();

        List<Estudiante> estudiantes = (nivelId != null) ?
                estudianteService.listarVisiblesPorNivelId(nivelId)
                : Collections.emptyList();

        model.addAttribute("niveles", niveles);
        model.addAttribute("periodos", periodos);
        model.addAttribute("materias", materias);
        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("curso", curso);
        model.addAttribute("filtroAplicado", nivelId != null);

        return "pages/Admin/cursoForm";
    }
    // ------------------------------------------------------------
// 4. Guardar curso (nuevo o editado)
// ------------------------------------------------------------

    @PostMapping("/pages/Admin/cursoGuardar")
    @Transactional
    public String guardarCurso(
            @Valid @ModelAttribute Curso curso,
            BindingResult result,
            @RequestParam(value = "materiasSeleccionadas", required = false) List<Long> materiasIds,
            @RequestParam(value = "estudiantesSeleccionados", required = false) List<Long> estudiantesIds,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("periodos", periodoAcademicoService.listarTodosPeriodosAcademicos());
            model.addAttribute("niveles", nivelEducativoService.listarTodos());
            model.addAttribute("materias", materiaService.listarPorNivelId(curso.getNivelEducativo() != null ? curso.getNivelEducativo().getId() : null));
            model.addAttribute("estudiantes", estudianteService.buscarEstudiantePorId(curso.getNivelEducativo() != null ? curso.getNivelEducativo().getId() : null));
            return "pages/Admin/cursoForm";
        }
        try {
            Curso cursoPersistido = (curso.getId() != null)
                    ? cursoService.buscarCursoPorId(curso.getId()).orElse(new Curso())
                    : new Curso();

            List<Estudiante> estudiantesActuales = new ArrayList<>();
            if (cursoPersistido.getEstudiantes() != null) {
                estudiantesActuales.addAll(cursoPersistido.getEstudiantes());
            }

            cursoPersistido.setNombre(curso.getNombre());

            // Cargar PeriodoAcademico completo por id solo si no es null
            if (curso.getPeriodoAcademico() != null && curso.getPeriodoAcademico().getId() != null) {
                PeriodoAcademico pa = periodoAcademicoService.buscarPorId(curso.getPeriodoAcademico().getId());
                cursoPersistido.setPeriodoAcademico(pa);
            } else {
                cursoPersistido.setPeriodoAcademico(null);
            }

            // Cargar NivelEducativo completo por id solo si no es null
            if (curso.getNivelEducativo() != null && curso.getNivelEducativo().getId() != null) {
                Optional<NivelEducativo> optionalNe = nivelEducativoService.buscarPorId(curso.getNivelEducativo().getId());
                if (optionalNe.isPresent()) {
                    cursoPersistido.setNivelEducativo(optionalNe.get());
                } else {
                    cursoPersistido.setNivelEducativo(null);
                }
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
