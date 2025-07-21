package com.itsqmet.proyecto_vinculacion.controller;

import com.itsqmet.proyecto_vinculacion.entity.Docente;
import com.itsqmet.proyecto_vinculacion.entity.Materia;
import com.itsqmet.proyecto_vinculacion.entity.NivelEducativo;
import com.itsqmet.proyecto_vinculacion.service.DocenteService;
import com.itsqmet.proyecto_vinculacion.service.MateriaService;
import com.itsqmet.proyecto_vinculacion.service.NivelEducativoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class MateriaController {

    @Autowired
    private MateriaService materiaService;

    @Autowired
    private DocenteService docenteService;

    @Autowired
    private NivelEducativoService nivelEducativoService;

    /* ========================
       1. Vista listado
       ======================== */
    @GetMapping("/pages/Admin/materiaVista")
    public String vistaMaterias(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long docenteId,
            Model model) {

        List<Materia> materias = materiaService.filtrarPorNombreYDocente(nombre, docenteId);

        model.addAttribute("materias", materias);
        model.addAttribute("docentes", docenteService.listarTodosDocentes());

        // Guardamos filtros para repoblar
        model.addAttribute("paramNombre", nombre);
        model.addAttribute("paramDocenteId", docenteId);

        return "pages/Admin/materiaVista";
    }

    /* ========================
       2. Form nuevo
       ======================== */
    @GetMapping("/pages/Admin/materiaForm")
    public String formNuevaMateria(Model model) {
        // Modelo base
        Materia materia = new Materia(); // nuevo

        model.addAttribute("materia", materia);
        model.addAttribute("docentes", docenteService.listarTodosDocentes());
        model.addAttribute("niveles", nivelEducativoService.listarTodos());

        // DEBUG opcional: tamaños para ver si llegan datos
        model.addAttribute("docentesCount", docenteService.listarTodosDocentes().size());
        model.addAttribute("nivelesCount", nivelEducativoService.listarTodos().size());

        return "pages/Admin/materiaForm";
    }


    /* ========================
       3. Form editar
       ======================== */
    @GetMapping("/pages/Admin/materiaForm/{id}")
    public String formEditarMateria(@PathVariable Long id,
                                    Model model,
                                    RedirectAttributes redirect) {

        Optional<Materia> materiaOpt = materiaService.buscarMateriaPorId(id);
        if (materiaOpt.isEmpty()) {
            redirect.addFlashAttribute("error", "Materia no encontrada.");
            return "redirect:/pages/Admin/materiaVista";
        }

        model.addAttribute("materia", materiaOpt.get());
        model.addAttribute("docentes", docenteService.listarTodosDocentes());
        model.addAttribute("niveles", nivelEducativoService.listarTodos());
        return "pages/Admin/materiaForm";
    }

    /* ========================
       4. Guardar
       ======================== */
    @PostMapping("/pages/Admin/materiaGuardar")
    public String guardarMateria(@ModelAttribute Materia materia,
                                 @RequestParam(required = false) Long docenteId,
                                 @RequestParam(required = false) Long nivelId,
                                 RedirectAttributes redirect) {
        try {
            // Asociar el docente si se envió un docenteId
            if (docenteId != null) {
                Docente docente = docenteService.buscarDocenteId(docenteId)
                        .orElse(null);
                materia.setDocente(docente);
            }

            // Asociar el nivel educativo si se envió nivelId
            if (nivelId != null) {
                NivelEducativo nivel = new NivelEducativo();
                nivel.setId(nivelId);
                materia.setNivelEducativo(nivel);
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
