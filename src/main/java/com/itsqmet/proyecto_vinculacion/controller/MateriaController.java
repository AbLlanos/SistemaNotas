package com.itsqmet.proyecto_vinculacion.controller;

import com.itsqmet.proyecto_vinculacion.entity.Docente;
import com.itsqmet.proyecto_vinculacion.entity.Materia;
import com.itsqmet.proyecto_vinculacion.entity.NivelEducativo;
import com.itsqmet.proyecto_vinculacion.entity.PeriodoAcademico;
import com.itsqmet.proyecto_vinculacion.service.DocenteService;
import com.itsqmet.proyecto_vinculacion.service.MateriaService;
import com.itsqmet.proyecto_vinculacion.service.NivelEducativoService;
import com.itsqmet.proyecto_vinculacion.service.PeriodoAcademicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class MateriaController {

    @Autowired
    private MateriaService materiaService;

    @Autowired
    private DocenteService docenteService;

    @Autowired
    private NivelEducativoService nivelEducativoService;

    @Autowired
    private PeriodoAcademicoService periodoAcademicoService;

    /* ========================
       1. Vista listado
       ======================== */
    @GetMapping("/pages/Admin/materiaVista")
    public String vistaMaterias(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long docenteId,
            @RequestParam(required = false) Long periodoId,                  // <-- Nuevo parámetro
            @RequestParam(required = false, defaultValue = "false") boolean mostrarOcultos,
            Model model) {

        List<Materia> materias = materiaService.filtrarPorNombreDocenteYPeriodo(nombre, docenteId, periodoId);

        if (!mostrarOcultos) {
            materias = materias.stream()
                    .filter(Materia::getVisible)
                    .toList();
        }

        model.addAttribute("materias", materias);
        model.addAttribute("docentes", docenteService.listarTodosDocentes());
        model.addAttribute("periodos", periodoAcademicoService.listarTodosPeriodosAcademicos()); // lista de periodos
        model.addAttribute("paramNombre", nombre);
        model.addAttribute("paramDocenteId", docenteId);
        model.addAttribute("paramPeriodoId", periodoId);   // <-- Parámetro para vista
        model.addAttribute("paramMostrarOcultos", mostrarOcultos);

        return "pages/Admin/materiaVista";
    }


    /* ========================
       2. Form nuevo
       ======================== */
    @GetMapping("/pages/Admin/materiaForm")
    public String formNuevaMateria(Model model) {
        Materia materia = new Materia();
        model.addAttribute("materia", materia);

        // Filtrar solo docentes visibles
        List<Docente> docentesVisibles = docenteService.listarTodosDocentes()
                .stream()
                .filter(d -> Boolean.TRUE.equals(d.getVisible()))
                .toList();

        model.addAttribute("docentes", docentesVisibles);
        model.addAttribute("niveles", nivelEducativoService.listarTodos());
        model.addAttribute("periodos", periodoAcademicoService.listarPeriodosVisibles());
        return "pages/Admin/materiaForm";
    }

    /* ========================
       3. Form editar
       ======================== */
    @GetMapping("/pages/Admin/materiaForm/{id}")
    public String formEditarMateria(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Optional<Materia> materiaOpt = materiaService.buscarMateriaPorId(id);
        if (materiaOpt.isEmpty()) {
            redirect.addFlashAttribute("error", "Materia no encontrada.");
            return "redirect:/pages/Admin/materiaVista";
        }

        Materia materia = materiaOpt.get();

        // Obtener todos los docentes visibles
        List<Docente> docentesVisibles = docenteService.listarTodosDocentes()
                .stream()
                .filter(d -> Boolean.TRUE.equals(d.getVisible()))
                .collect(Collectors.toList());

        // Incluir el docente asignado aunque esté oculto
        Docente docenteAsignado = materia.getDocente();
        if (docenteAsignado != null && !docentesVisibles.contains(docenteAsignado)) {
            docentesVisibles.add(docenteAsignado);
        }

        // Obtener periodos visibles
        List<PeriodoAcademico> periodosVisibles = periodoAcademicoService.listarPeriodosVisibles();

        // Incluir el periodo académico asignado aunque esté oculto
        PeriodoAcademico periodoAsignado = materia.getPeriodoAcademico();
        if (periodoAsignado != null && !periodosVisibles.contains(periodoAsignado)) {
            periodosVisibles.add(periodoAsignado);
        }

        model.addAttribute("materia", materia);
        model.addAttribute("docentes", docentesVisibles);
        model.addAttribute("niveles", nivelEducativoService.listarTodos());
        model.addAttribute("periodos", periodosVisibles);
        return "pages/Admin/materiaForm";
    }

    /* ========================
       4. Guardar
       ======================== */
    @PostMapping("/pages/Admin/materiaGuardar")
    public String guardarMateria(@ModelAttribute Materia materia,
                                 RedirectAttributes redirect) {
        try {
            // Asociar docente si tiene ID
            if (materia.getDocente() != null && materia.getDocente().getId() != null) {
                Docente docente = docenteService.buscarDocenteId(materia.getDocente().getId())
                        .orElse(null);
                materia.setDocente(docente);
            } else {
                materia.setDocente(null);
            }

            // Asociar nivel educativo si tiene ID
            if (materia.getNivelEducativo() != null && materia.getNivelEducativo().getId() != null) {
                NivelEducativo nivel = nivelEducativoService.buscarPorId(materia.getNivelEducativo().getId())
                        .orElse(null);
                materia.setNivelEducativo(nivel);
            } else {
                materia.setNivelEducativo(null);
            }

            // Guardar materia
            materiaService.guardarMateria(materia);
            redirect.addFlashAttribute("success", "¡La materia se guardó correctamente!");

        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al guardar la materia: " + e.getMessage());
        }
        return "redirect:/pages/Admin/materiaVista";
    }



    /* ========================
       5. Eliminar
       ======================== */
    @GetMapping("/eliminarMateria/{id}")
    public String eliminarMateria(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            materiaService.eliminarMateria(id);
            redirect.addFlashAttribute("success", "Materia eliminada correctamente.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al eliminar materia: " + e.getMessage());
        }
        return "redirect:/pages/Admin/materiaVista";
    }
}
