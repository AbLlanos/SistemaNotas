package com.itsqmet.proyecto_vinculacion.controller;

import com.itsqmet.proyecto_vinculacion.dto.CursoDTO;
import com.itsqmet.proyecto_vinculacion.dto.EstudianteOptionDTO;
import com.itsqmet.proyecto_vinculacion.dto.MateriaOptionDTO;
import com.itsqmet.proyecto_vinculacion.dto.NotaCompletaDTO;
import com.itsqmet.proyecto_vinculacion.entity.Curso;
import com.itsqmet.proyecto_vinculacion.entity.Estudiante;
import com.itsqmet.proyecto_vinculacion.entity.Materia;
import com.itsqmet.proyecto_vinculacion.entity.Notas;
import com.itsqmet.proyecto_vinculacion.service.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pages/Admin")
public class BachilleratoTecnicoController {

    @Autowired
    private NotasService notasService;

    @Autowired
    private CursoService cursoService;

    @Autowired
    private MateriaService materiaService;

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private TrimestreService trimestreService;

    @Autowired
    private PeriodoAcademicoService periodoAcademicoService;

    @Autowired
    private NivelEducativoService nivelEducativoService;

    @Autowired
    private PDFGeneratorService pdfGeneratorService;




    //Ruta Bachillerato Tecnico
    @GetMapping("/BachilleratoTecnico/bachilleratoTecnicoVista")
    public String vistaAdminBachilleratoTecnico(
            @RequestParam(required = false) String nombrePeriodo,
            @RequestParam(required = false) String nombreCurso,
            @RequestParam(required = false) String nombreMateria,
            @RequestParam(required = false) String cedula,
            @RequestParam(required = false) String nombreTrimestre,
            @RequestParam(required = false, defaultValue = "false") boolean mostrarOcultos,
            Model model) {

        String nivelFiltro = "bachilleratotecnicoencomputacion"; // sin espacios ni mayúsculas

        List<Curso> cursos = cursoService.listarTodosCursos().stream()
                .filter(c -> {
                    if (c.getNivelEducativo() == null || c.getNivelEducativo().getNombre() == null) {
                        return false;
                    }
                    String nivelActual = c.getNivelEducativo().getNombre().toLowerCase().replace(" ", "");
                    return nivelFiltro.equals(nivelActual);
                })
                .toList();

        List<Materia> materias;
        if (nombreCurso != null && !nombreCurso.isBlank()) {
            materias = materiaService.listarTodasMaterias().stream()
                    .filter(m -> m.getCursos() != null &&
                            m.getCursos().stream()
                                    .anyMatch(c -> nombreCurso.equalsIgnoreCase(c.getNombre())))
                    .toList();
        } else {
            materias = materiaService.listarTodasMaterias().stream()
                    .filter(m -> {
                        if (m.getNivelEducativo() == null || m.getNivelEducativo().getNombre() == null) {
                            return false;
                        }
                        String nivelMat = m.getNivelEducativo().getNombre().toLowerCase().replace(" ", "");
                        return nivelFiltro.equals(nivelMat);
                    })
                    .toList();
        }

        List<String> trimestres = List.of("Primer Trimestre", "Segundo Trimestre", "Tercer Trimestre");

        List<NotaCompletaDTO> notas = notasService.obtenerNotasCompletas(
                nombrePeriodo, nombreCurso, nombreMateria, cedula, nombreTrimestre, nivelFiltro
        );

        // Filtrar estudiantes visibles Y del nivel Bachillerato Técnico en Computación
        if (!mostrarOcultos) {
            List<String> cedulasVisibles = estudianteService.listarVisibles().stream()
                    .filter(e -> {
                        if (e.getNivelEducativo() == null || e.getNivelEducativo().getNombre() == null) return false;
                        String nivelEst = e.getNivelEducativo().getNombre().toLowerCase().replace(" ", "");
                        return nivelFiltro.equals(nivelEst);
                    })
                    .map(Estudiante::getCedula)
                    .toList();

            notas = notas.stream()
                    .filter(n -> cedulasVisibles.contains(n.getCedulaEstudiante()))
                    .toList();
        }

        Map<String, Object> param = new HashMap<>();
        param.put("nombrePeriodo", nombrePeriodo);
        param.put("nombreCurso", nombreCurso);
        param.put("nombreMateria", nombreMateria);
        param.put("cedula", cedula);
        param.put("nombreTrimestre", nombreTrimestre);

        model.addAttribute("aniosLectivos", periodoAcademicoService.listarPeriodosVisibles());
        model.addAttribute("cursos", cursos);
        model.addAttribute("materias", materias);
        model.addAttribute("trimestres", trimestres);
        model.addAttribute("notas", notas);
        model.addAttribute("param", param);
        model.addAttribute("mostrarOcultos", mostrarOcultos);

        return "pages/Admin/BachilleratoTecnico/bachilleratoTecnicoVista";
    }






    @GetMapping("/notas/nuevoBachilleratoTecnico")
    public String mostrarFormularioNuevaNotaTecnico(Model model) {
        NotaCompletaDTO notaCompletaDTO = new NotaCompletaDTO();
        model.addAttribute("nota", notaCompletaDTO);

        model.addAttribute("estudiantes", estudianteService.listarTodosEstudiantes());
        model.addAttribute("periodos", periodoAcademicoService.listarPeriodosVisibles());
        model.addAttribute("materias", materiaService.listarTodasMaterias());
        model.addAttribute("trimestres", trimestreService.listarTodosPeriodos());

        //MODIFICAR
        String nivelFiltro = "bachilleratotecnicoencomputacion";

        List<Curso> cursosBachillerato = cursoService.listarTodosCursos().stream()
                .filter(c -> {
                    if (c.getNivelEducativo() == null || c.getNivelEducativo().getNombre() == null) {
                        return false;
                    }
                    String nivelActual = c.getNivelEducativo().getNombre().toLowerCase().replace(" ", "");
                    return nivelFiltro.equals(nivelActual);
                })
                .toList();

        model.addAttribute("cursos", cursosBachillerato);

        return "pages/Admin/BachilleratoTecnico/bachilleratoTecnicoForm";
    }

    // Guardar nota (tanto nueva como editada)
    @PostMapping("/notas/guardarTecnico")
    public String guardarNotas(
            @ModelAttribute("nota") NotaCompletaDTO form,
            Model model) {
        try {
            notasService.guardarNotasDesdeFormulario(form);
            model.addAttribute("exito", "¡Las notas se guardaron correctamente!");
        } catch (Exception e) {
            model.addAttribute("error", "Error al guardar las notas: " + e.getMessage());
        }

        // Recargar datos del formulario
        model.addAttribute("nota", form);
        model.addAttribute("estudiantes", estudianteService.listarTodosEstudiantes());
        model.addAttribute("materias", materiaService.listarTodasMaterias());
        model.addAttribute("periodos", periodoAcademicoService.listarTodosPeriodosAcademicos());
        model.addAttribute("trimestres", trimestreService.listarTodosPeriodos());
        model.addAttribute("cursos", cursoService.listarTodosCursos());


        return "pages/Admin/BachilleratoTecnico/bachilleratoTecnicoForm";
    }

    @GetMapping("/notas/editarTecnico/{id}")
    public String mostrarFormularioEditar111(@PathVariable("id") Long id, Model model) {
        System.out.println("DEBUG: Entrando a mostrarFormularioEditar con idNota=" + id);

        NotaCompletaDTO notaCompletaDTO = notasService.obtenerNotaCompletaPorId(id);
        System.out.println("DEBUG: NotaCompletaDTO obtenido: " + notaCompletaDTO);

        if (notaCompletaDTO != null) {
            System.out.println("DEBUG: Estudiante = " + notaCompletaDTO.getNombreCompletoEstudiante() +
                    " | Cedula = " + notaCompletaDTO.getCedulaEstudiante() +
                    " | Materia = " + notaCompletaDTO.getAreaMateria());
        }

        model.addAttribute("nota", notaCompletaDTO);

        // Listas auxiliares para selects
        model.addAttribute("estudiantes", estudianteService.listarTodosEstudiantes());
        model.addAttribute("materias", materiaService.listarTodasMaterias());
        model.addAttribute("periodos", periodoAcademicoService.listarTodosPeriodosAcademicos());
        model.addAttribute("trimestres", trimestreService.listarTodosPeriodos());

        // Cargar cursos filtrados por PeriodoAcademicoId si existe, o todos si no
        if (notaCompletaDTO.getPeriodoAcademicoId() != null) {
            model.addAttribute("cursos", cursoService.obtenerCursosPorPeriodoID(notaCompletaDTO.getPeriodoAcademicoId()));
        } else {
            model.addAttribute("cursos", cursoService.listarTodosCursos());
        }

        return "pages/Admin/BachilleratoTecnico/bachilleratoTecnicoForm";
    }





    @GetMapping("/periodo/{nombrePeriodo}/cursosTecnico")
    @ResponseBody
    public List<CursoDTO> listarCursosPorPeriodoFiltrados(@PathVariable String nombrePeriodo) {
        System.out.println("📥 Entrando a listarCursosPorPeriodoFiltrados con periodo: " + nombrePeriodo);

        List<Curso> cursos = cursoService.obtenerCursosPorPeriodoVisible(nombrePeriodo);
        System.out.println("📦 Cursos totales encontrados: " + cursos.size());

        List<CursoDTO> cursosFiltrados = cursos.stream()
                .filter(curso -> curso.getNivelEducativo() != null &&
                        //MODIFICAR
                        "Bachillerato Tecnico en Computacion".equals(curso.getNivelEducativo().getNombre()))
                .map(curso -> new CursoDTO(curso.getId(), curso.getNombre()))
                .collect(Collectors.toList());

        System.out.println("🎯 Cursos filtrados: " + cursosFiltrados.size());
        return cursosFiltrados;
    }



}
