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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
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



    @GetMapping("/Bachillerato/bachilleratoVista")
    public String vistaBachillerato(
            @RequestParam(required = false) String nombrePeriodo,
            @RequestParam(required = false) String nombreCurso,  // <-- aquí nombreCurso en lugar de cursoId
            @RequestParam(required = false) String nombreMateria,
            @RequestParam(required = false) String cedula,
            @RequestParam(required = false) String nombreTrimestre,
            @RequestParam(required = false, defaultValue = "false") boolean mostrarOcultos,
            Model model) {

        String nivelFiltro = "bachilleratogeneral";

        // Obtener cursos filtrados por nivel educativo
        List<Curso> cursos = cursoService.listarTodosCursos().stream()
                .filter(c -> c.getNivelEducativo() != null
                        && c.getNivelEducativo().getNombre() != null
                        && c.getNivelEducativo().getNombre().toLowerCase().replace(" ", "").equals(nivelFiltro))
                .toList();

        // Obtener materias filtrando por curso nombre (si hay) o por nivel educativo
        List<Materia> materias;
        if (nombreCurso != null && !nombreCurso.isBlank()) {
            materias = materiaService.listarTodasMaterias().stream()
                    .filter(m -> m.getCursos() != null &&
                            m.getCursos().stream()
                                    .anyMatch(c -> c.getNombre().equalsIgnoreCase(nombreCurso)))
                    .toList();
        } else {
            materias = materiaService.listarTodasMaterias().stream()
                    .filter(m -> m.getNivelEducativo() != null
                            && m.getNivelEducativo().getNombre() != null
                            && m.getNivelEducativo().getNombre().toLowerCase().replace(" ", "").equals(nivelFiltro))
                    .toList();
        }

        List<String> trimestres = List.of("Primer Trimestre", "Segundo Trimestre", "Tercer Trimestre");

        // Obtener notas con filtro por nombreCurso (String) y nivel educativo
        List<NotaCompletaDTO> notas = notasService.obtenerNotasCompletas(
                nombrePeriodo,
                nombreCurso,
                nombreMateria,
                cedula,
                nombreTrimestre,
                nivelFiltro);

        // Filtrar notas solo para cursos del nivel educativo actual
        Set<String> nombresCursos = cursos.stream()
                .map(Curso::getNombre)
                .collect(Collectors.toSet());

        notas = notas.stream()
                .filter(n -> n.getNombreCurso() != null && nombresCursos.contains(n.getNombreCurso()))
                .toList();

        if (!mostrarOcultos) {
            List<String> cedulasVisibles = estudianteService.listarVisibles().stream()
                    .filter(e -> e.getNivelEducativo() != null
                            && e.getNivelEducativo().getNombre() != null
                            && e.getNivelEducativo().getNombre().toLowerCase().replace(" ", "").equals(nivelFiltro))
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

        //MODIFICAR
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

        return "pages/Admin/Bachillerato/bachilleratoForm";
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
        List<NotaCompletaDTO> notas = notasService.obtenerReporteFinal(periodo, curso, cedula);

        if (notas.isEmpty()) {
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("No se encontraron notas para los filtros indicados. Debe cerrar esta ventana e intentar con un registroe existente");
            return;
        }

        String nombreEstudiante = notas.get(0).getNombreCompletoEstudiante();
        String periodoSanitizado = (periodo != null) ? periodo.replaceAll("[^a-zA-Z0-9]", "_") : "periodo";
        String trimestreSanitizado = (trimestre != null) ? trimestre.replaceAll("[^a-zA-Z0-9]", "_") : "trimestre";
        String nombreEstudianteSanitizado = (nombreEstudiante != null) ? nombreEstudiante.replaceAll("[^a-zA-Z0-9]", "_") : "estudiante";
        String nombreArchivo = String.format("reporte_notas_%s_%s_%s.pdf", periodoSanitizado, trimestreSanitizado, nombreEstudianteSanitizado);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=" + nombreArchivo);

        try {
            pdfGeneratorService.generarReporteNotas(nombreEstudiante, periodo, notas, trimestre, response.getOutputStream());
            response.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
            response.reset();
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("Error al generar el PDF: " + e.getMessage());
        }
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

        List<Curso> cursos = cursoService.obtenerCursosPorPeriodoVisible(nombrePeriodo);
        System.out.println("📦 Cursos totales encontrados: " + cursos.size());

        List<CursoDTO> cursosFiltrados = cursos.stream()
                .filter(curso -> curso.getNivelEducativo() != null &&
                        //MODIFICAR
                        "Bachillerato General".equals(curso.getNivelEducativo().getNombre()))
                .map(curso -> new CursoDTO(curso.getId(), curso.getNombre()))
                .collect(Collectors.toList());

        System.out.println("🎯 Cursos filtrados: " + cursosFiltrados.size());
        return cursosFiltrados;
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




    @GetMapping("/admin/validar-reporte-notas")
    @ResponseBody
    public ResponseEntity<?> validarReporteNotas(
            @RequestParam(value = "cedula", required = false) String cedula,
            @RequestParam(value = "periodo", required = false) String periodo,
            @RequestParam(value = "curso", required = false) String curso
    ) {
        List<NotaCompletaDTO> notas = notasService.obtenerReporteFinal(periodo, curso, cedula);
        if (notas.isEmpty()) {
            return ResponseEntity.ok(Collections.singletonMap("mensaje", "No se encontraron notas para los filtros seleccionados."));
        } else {
            return ResponseEntity.ok(Collections.singletonMap("exito", true));
        }
    }



}
