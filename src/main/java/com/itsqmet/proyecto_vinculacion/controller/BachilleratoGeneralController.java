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
public class BachilleratoGeneralController {

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

    @Autowired
    private PDFGeneratorService pdfService;







    //Ruta bachillerato
    @GetMapping("/Bachillerato/bachilleratoVista")
    public String vistaBachillerato(
            @RequestParam(required = false) String nombrePeriodo,
            @RequestParam(required = false) String nombreCurso,
            @RequestParam(required = false) String nombreMateria,
            @RequestParam(required = false) String cedula,
            @RequestParam(required = false) String nombreTrimestre,
            @RequestParam(required = false, defaultValue = "false") boolean mostrarOcultos,
            Model model) {

        String nivelFiltro = "bachilleratogeneral";

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
                nombrePeriodo, nombreCurso, nombreMateria, cedula, nombreTrimestre
        );

        // Filtrar notas para que solo aparezcan estudiantes visibles si mostrarOcultos == false
        if (!mostrarOcultos) {
            List<String> cedulasVisibles = estudianteService.listarVisibles()
                    .stream()
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

        return "pages/Admin/Bachillerato/bachilleratoVista";
    }


    @GetMapping("/notas/nuevo")
    public String mostrarFormularioNuevaNota(Model model) {
        NotaCompletaDTO notaCompletaDTO = new NotaCompletaDTO();
        model.addAttribute("nota", notaCompletaDTO);

        model.addAttribute("estudiantes", estudianteService.listarTodosEstudiantes());
        model.addAttribute("periodos", periodoAcademicoService.listarPeriodosVisibles());
        model.addAttribute("materias", materiaService.listarTodasMaterias());
        model.addAttribute("trimestres", trimestreService.listarTodosPeriodos());

        // Filtrar cursos por Bachillerato General también aquí
        String nivelFiltro = "bachilleratogeneral";
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

        return "pages/Admin/Bachillerato/bachilleratoForm";
    }

    // Guardar nota (tanto nueva como editada)
    @PostMapping("/notas/guardar")
    public String guardarNotas(
            @ModelAttribute("nota") NotaCompletaDTO form,
            RedirectAttributes redirectAttributes) {
        try {
            notasService.guardarNotasDesdeFormulario(form);
            redirectAttributes.addFlashAttribute("exito", "¡Las notas se guardaron correctamente!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar las notas: " + e.getMessage());
        }
        return "redirect:/pages/Admin/Bachillerato/bachilleratoVista";
    }

    @GetMapping("/notas/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
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

        return "pages/Admin/Bachillerato/bachilleratoForm";
    }






    @GetMapping("/admin/reporte-notas")
    public void generarReporteNotas(
            @RequestParam(value = "cedula", required = false) String cedula,
            @RequestParam(value = "periodo", required = false) String periodo,
            @RequestParam(value = "curso", required = false) String curso,
            @RequestParam(value = "trimestre", required = false, defaultValue = "todos") String trimestre,
            HttpServletResponse response
    ) throws IOException {

        // Obtén la lista completa de notas con filtro periodo, curso y cédula
        List<NotaCompletaDTO> notas = notasService.obtenerReporteFinal(periodo, curso, cedula);

        if (notas.isEmpty()) {
            // Manejar caso sin datos, tal vez lanzar excepción o enviar PDF vacío
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No se encontraron notas para los filtros indicados.");
            return;
        }

        // Extrae nombre del estudiante del primer registro (asumimos que es el mismo para todas las notas)
        String nombreEstudiante = notas.get(0).getNombreEstudiante();

        // Configura respuesta HTTP para PDF en línea
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=reporte-notas.pdf");

        // Llama al servicio que genera el PDF y escribe en el output stream de la respuesta HTTP
        pdfGeneratorService.generarReporteNotas(nombreEstudiante, periodo, notas, trimestre, response.getOutputStream());
    }


    //Rutas generales

    @GetMapping("/{cursoId}/materias")
    @ResponseBody
    public List<MateriaOptionDTO> getMateriasPorCurso(@PathVariable Long cursoId) {
        Curso curso = cursoService.buscarCursoPorId(cursoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));
        return curso.getMaterias().stream()
                .map(m -> new MateriaOptionDTO(m.getId(), m.getNombre()))
                .toList();
    }

    // --- AJAX: estudiantes por curso ---
    @GetMapping("/cursos/{cursoId}/estudiantes")
    @ResponseBody
    public List<EstudianteOptionDTO> getEstudiantesPorCurso(@PathVariable Long cursoId) {
        System.out.println("DEBUG: Consultando estudiantes del curso con ID=" + cursoId);

        List<Estudiante> ests = estudianteService.listarPorCurso(cursoId);

        System.out.println("DEBUG: Estudiantes encontrados = " + ests.size());
        for (Estudiante e : ests) {
            System.out.println(" - " + e.getCedula() + " | " + e.getNombre() + " " + e.getApellido());
        }

        return ests.stream()
                .map(e -> new EstudianteOptionDTO(
                        e.getCedula(),
                        (e.getNombre() + " " + e.getApellido()).trim()
                ))
                .toList();
    }



    @GetMapping("/cursos/porPeriodo/{id}")
    @ResponseBody
    public List<Curso> obtenerCursosPorPeriodo(@PathVariable Long id) {
        return cursoService.obtenerCursosPorPeriodoID(id);
    }

    @GetMapping("/periodo/{nombrePeriodo}/cursos")
    @ResponseBody
    public List<CursoDTO> listarCursosPorPeriodoFiltrados(@PathVariable String nombrePeriodo) {
        System.out.println("📥 Entrando a listarCursosPorPeriodoFiltrados con periodo: " + nombrePeriodo);
        return cursoService.obtenerCursosPorPeriodoVisible(nombrePeriodo)
                .stream()
                .map(curso -> new CursoDTO(curso.getId(), curso.getNombre()))
                .collect(Collectors.toList());
    }


    @GetMapping("/notas")
    public String listarNotas(
            @RequestParam(required = false) Long cursoId,
            Model model) {

        List<Notas> notas = (cursoId != null)
                ? notasService.obtenerNotasPorCurso(cursoId)
                : notasService.listarTodasLasNotas();

        model.addAttribute("notas", notas);
        return "pages/Admin/notas/listar";
    }





}
