package com.itsqmet.proyecto_vinculacion.controller;

import com.itsqmet.proyecto_vinculacion.entity.Curso;
import com.itsqmet.proyecto_vinculacion.entity.Materia;
import com.itsqmet.proyecto_vinculacion.entity.NivelEducativo;
import com.itsqmet.proyecto_vinculacion.service.CursoService;
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
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @Autowired
    private MateriaService materiaService;

    @Autowired
    private NivelEducativoService nivelEducativoService;

    // 1. Ruta General - Vista principal de cursos
    @GetMapping("/pages/Admin/cursoVista")
    public String mostrarCursoVista(@RequestParam(value = "nombre", required = false)
                                        String nombre,
                                    @RequestParam(value = "nivelEducativo", required = false)
                                    Long nivelId,
                                    Model model) {

        List<Curso> cursos = cursoService.filtrarCursos(nombre, nivelId);
        List<NivelEducativo> niveles = nivelEducativoService.listarTodos();

        model.addAttribute("cursos", cursos);
        model.addAttribute("niveles", niveles);
        model.addAttribute("totalCursos", cursos.size());

        return "pages/Admin/cursoVista";
    }

    // 2. Ruta Form - Formulario para nuevo curso
    @GetMapping("/pages/Admin/cursoForm")
    public String mostrarCursoForm(Model model) {

        List<NivelEducativo> niveles = nivelEducativoService.listarTodos();
        List<Materia> materia = materiaService.listarTodasMaterias();

        model.addAttribute("niveles", niveles);
        model.addAttribute("curso", new Curso());
        model.addAttribute("materias",materia);

        return "pages/Admin/cursoForm";
    }

    // 3. Ruta para editar curso
    @GetMapping("/pages/Admin/cursoForm/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Curso> cursoOptional = cursoService.buscarCursoPorId(id);

        List<NivelEducativo> niveles = nivelEducativoService.listarTodos();
        List<Materia> materia = materiaService.listarTodasMaterias();

        model.addAttribute("niveles", niveles);
        model.addAttribute("curso", new Curso());
        model.addAttribute("materias",materia);

        if (cursoOptional.isPresent()) {
            model.addAttribute("curso", cursoOptional.get());
            return "pages/Admin/cursoForm";
        } else {
            redirectAttributes.addFlashAttribute("error", "Curso no encontrado");
            return "redirect:/pages/Admin/cursoVista";
        }
    }

    // 4. Guardar curso (nuevo o editado)
    @PostMapping("/pages/Admin/cursoGuardar")
    public String guardarCurso(@ModelAttribute Curso curso,
                               @RequestParam(value = "materiasSeleccionadas", required = false) List<Long> materiasIds) {

        if (materiasIds != null && !materiasIds.isEmpty()) {
            List<Materia> materias = materiaService.obtenerMateriasPorIds(materiasIds);
            curso.setMaterias(materias);
        }

        cursoService.guardarCurso(curso);
        return "redirect:/pages/Admin/cursoVista";
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