package com.itsqmet.proyecto_vinculacion.controller;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;
import com.itsqmet.proyecto_vinculacion.dto.EstudianteOptionDTO;
import com.itsqmet.proyecto_vinculacion.dto.MateriaOptionDTO;
import com.itsqmet.proyecto_vinculacion.dto.NotaCompletaDTO;
import com.itsqmet.proyecto_vinculacion.entity.*;
import com.itsqmet.proyecto_vinculacion.service.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.Document;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/pages/Admin")
public class AdminController {

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


    //1. Sistema de notas

    @GetMapping("/vistaAdmin")
    public String vistaAdmin(){
        return "pages/Admin/vistaAdmin";
    }






    //Ruta educacion inicial

    @GetMapping("/Inicial/educacionInicialVista")
    public String vistaAdminEducaionInicial(
            @RequestParam(required = false) String nombrePeriodo,
            @RequestParam(required = false) String nombreCurso,
            @RequestParam(required = false) String nombreMateria,
            @RequestParam(required = false) String cedula,
            @RequestParam(required = false) String nombreTrimestre,
            Model model) {

        String nombreNivel = "EducacionInicial";

        List<Notas> notas = notasService.buscarNotasPorFiltros(
                nombrePeriodo, nombreCurso, nombreMateria, cedula, nombreTrimestre
        );

        List<Curso> cursosFiltrados = cursoService.findByNivelEducativoNombre(nombreNivel);
        List<Materia> materiasFiltradas = materiaService.findByCursoNivelEducativoNombre(nombreNivel);

        model.addAttribute("notas", notas);
        model.addAttribute("aniosLectivos", periodoAcademicoService.listarTodosPeriodosAcademicos());
        model.addAttribute("trimestres", trimestreService.listarTodosPeriodos());
        model.addAttribute("cursos", cursosFiltrados);
        model.addAttribute("materias", materiasFiltradas);

        return "pages/Admin/Inicial/educacionInicialVista";
    }









    //Ruta educaion basica

    @GetMapping("/EducacionBasica/educacionBasicaVista")
    public String vistaAdminEscuela(
            @RequestParam(required = false) String nombrePeriodo,
            @RequestParam(required = false) String nombreCurso,
            @RequestParam(required = false) String nombreMateria,
            @RequestParam(required = false) String cedula,
            @RequestParam(required = false) String nombreTrimestre,
            Model model) {

        String nombreNivel = "EducacionBasica";

        List<Notas> notas = notasService.buscarNotasPorFiltros(
                nombrePeriodo, nombreCurso, nombreMateria, cedula, nombreTrimestre
        );

        List<Curso> cursosFiltrados = cursoService.findByNivelEducativoNombre(nombreNivel);
        List<Materia> materiasFiltradas = materiaService.findByCursoNivelEducativoNombre(nombreNivel);

        model.addAttribute("notas", notas);
        model.addAttribute("aniosLectivos", periodoAcademicoService.listarTodosPeriodosAcademicos());
        model.addAttribute("trimestres", trimestreService.listarTodosPeriodos());
        model.addAttribute("cursos", cursosFiltrados);
        model.addAttribute("materias", materiasFiltradas);

        return "pages/Admin/EducacionBasica/educacionBasicaVista";
    }














    //Ruta bachillerato

    @GetMapping("/Bachillerato/bachilleratoVista")
    public String vistaBachillerato(
            @RequestParam(required = false) String nombrePeriodo,
            @RequestParam(required = false) String nombreCurso,
            @RequestParam(required = false) String nombreMateria,
            @RequestParam(required = false) String cedula,
            @RequestParam(required = false) String nombreTrimestre,
            Model model) {

        // Combos base
        List<PeriodoAcademico> aniosLectivos = periodoAcademicoService.listarTodosPeriodosAcademicos();
        List<Curso> cursos = cursoService.listarTodosCursos();

        // Materias: si el usuario ya filtró por curso, trae solo esas; si no, trae todas
        List<Materia> materias = (nombreCurso != null && !nombreCurso.isBlank())
                ? materiaService.findByCursoNombre(nombreCurso)
                : materiaService.listarTodasMaterias();

        // Trimestres fijos
        List<String> trimestres = List.of("Primer Trimestre", "Segundo Trimestre", "Tercer Trimestre");

        // Notas (agrupadas)
        List<NotaCompletaDTO> notas = notasService.obtenerNotasCompletas(
                nombrePeriodo, nombreCurso, nombreMateria, cedula, nombreTrimestre
        );

        // Mantener filtros
        Map<String, Object> param = new HashMap<>();
        param.put("nombrePeriodo", nombrePeriodo);
        param.put("nombreCurso", nombreCurso);
        param.put("nombreMateria", nombreMateria);
        param.put("cedula", cedula);
        param.put("nombreTrimestre", nombreTrimestre);

        model.addAttribute("aniosLectivos", aniosLectivos);
        model.addAttribute("cursos", cursos);
        model.addAttribute("materias", materias);
        model.addAttribute("trimestres", trimestres);
        model.addAttribute("notas", notas);
        model.addAttribute("param", param);

        return "pages/Admin/Bachillerato/bachilleratoVista";
    }



    @GetMapping("/notas/nueva")
    public String mostrarFormularioNuevaNota(Model model) {
        NotaCompletaDTO notaCompletaDTO = new NotaCompletaDTO();
        model.addAttribute("nota", notaCompletaDTO);

        model.addAttribute("estudiantes", estudianteService.listarTodosEstudiantes());
        model.addAttribute("periodos", periodoAcademicoService.listarTodosPeriodosAcademicos());
        model.addAttribute("materias", materiaService.listarTodasMaterias());
        model.addAttribute("trimestres", trimestreService.listarTodosPeriodos());
        model.addAttribute("cursos", cursoService.listarTodosCursos()); // Inicialmente todos

        return "pages/Admin/Bachillerato/bachilleratoForm";
    }

    // Guardar nota (tanto nueva como editada)
    @PostMapping("/notas/guardar")
    public String guardarNotaCompleta(@ModelAttribute NotaCompletaDTO notaCompletaDTO) {
        notasService.guardarNotasDesdeFormulario(notaCompletaDTO);
        return "redirect:/Bachillerato/bachilleratoVista";
    }

    @GetMapping("/notas/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        NotaCompletaDTO notaCompletaDTO = notasService.obtenerNotaCompletaPorId(id);
        model.addAttribute("nota", notaCompletaDTO);

        model.addAttribute("estudiantes", estudianteService.listarTodosEstudiantes());
        model.addAttribute("materias", materiaService.listarTodasMaterias());
        model.addAttribute("periodos", periodoAcademicoService.listarTodosPeriodosAcademicos());
        model.addAttribute("trimestres", trimestreService.listarTodosPeriodos());

        if (notaCompletaDTO.getPeriodoAcademicoId() != null) {
            model.addAttribute("cursos", cursoService.obtenerCursosPorPeriodoID(notaCompletaDTO.getPeriodoAcademicoId()));
        } else {
            model.addAttribute("cursos", cursoService.listarTodosCursos());
        }

        return "pages/Admin/Bachillerato/bachilleratoForm";
    }










    //Ruta Bachillerato Tecnico

    @GetMapping("/BachilleratoTecnico/bachilleratoTecnicoVista")
    public String vistaAdminBachilleratoTecnico(
            @RequestParam(required = false) String nombrePeriodo,
            @RequestParam(required = false) String nombreCurso,
            @RequestParam(required = false) String nombreMateria,
            @RequestParam(required = false) String cedula,
            @RequestParam(required = false) String nombreTrimestre,
            Model model) {

        String nombreNivel = "BachilleratoTecnico";

        List<Notas> notas = notasService.buscarNotasPorFiltros(
                nombrePeriodo, nombreCurso, nombreMateria, cedula, nombreTrimestre
        );

        List<Curso> cursosFiltrados = cursoService.findByNivelEducativoNombre(nombreNivel);
        List<Materia> materiasFiltradas = materiaService.findByCursoNivelEducativoNombre(nombreNivel);

        model.addAttribute("notas", notas);
        model.addAttribute("aniosLectivos", periodoAcademicoService.listarTodosPeriodosAcademicos());
        model.addAttribute("trimestres", trimestreService.listarTodosPeriodos());
        model.addAttribute("cursos", cursosFiltrados);
        model.addAttribute("materias", materiasFiltradas);

        return "pages/Admin/Bachillerato/bachilleratoVista";
    }



    @Autowired
    private PDFGeneratorService pdfService;
    //Generar PDF dinamico


    @GetMapping("/admin/generar-pdf")
    public void generarPdf(
            @RequestParam("idNota") Long idNota,
            @RequestParam(value = "periodo", required = false) String periodo,
            @RequestParam(value = "curso", required = false) String curso,
            @RequestParam(value = "materia", required = false) String materia,
            @RequestParam(value = "cedula", required = false) String cedula,
            @RequestParam(value = "trimestre", required = false) String trimestre,
            HttpServletResponse response
    ) throws IOException {

        // Si quieres que prevalezca idNota sobre filtros:
        NotaCompletaDTO unica = notasService.obtenerNotaCompletaPorIdParaPDF(idNota);
        List<NotaCompletaDTO> datos = java.util.Collections.singletonList(unica);

        // (Opcional) Si no se envía idNota válida, podrías caer a filtros:
        // if (unica == null) { datos = notasService.obtenerNotasParaPDF(periodo, curso, materia, cedula, trimestre); }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"notas.pdf\"");

        pdfGeneratorService.generarPdfNotas(datos, trimestre, response.getOutputStream());
    }



    @GetMapping("/admin/reporte-notas")
    public void generarReporteNotas(
            @RequestParam(value = "cedula", required = false) String cedula,
            @RequestParam(value = "periodo", required = false) String periodo,
            @RequestParam(value = "curso", required = false) String curso,
            @RequestParam(value = "trimestre", required = false, defaultValue = "todos") String trimestre,
            HttpServletResponse response
    ) throws IOException {
        // Ajusta este método para filtrar por periodo, cedula y curso
        List<NotaCompletaDTO> notas = notasService.obtenerReporteFinal(periodo, curso, cedula);

        String nombreEstudiante = notas.isEmpty() ? "Estudiante" : notas.get(0).getNombreEstudiante();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=reporte-notas.pdf");

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
        // Si prefieres obtenerlos desde el curso:
        // Curso curso = cursoService.getCursoOrThrow(cursoId);
        // List<Estudiante> ests = curso.getEstudiantes();  // solo si tienes la relación mapeada en Curso
        // Como vimos, usamos EstudianteService:
        List<Estudiante> ests = estudianteService.listarPorCurso(cursoId);
        return ests.stream()
                .map(e -> new EstudianteOptionDTO(
                        e.getCedula(),
                        (e.getNombre() + " " + e.getApellido()).trim()
                ))
                .toList();
    }


    @GetMapping("/periodo/{nombre}/cursos")
    @ResponseBody
    public List<Curso> listarCursosPorPeriodo(@PathVariable String nombre) {
        return cursoService.obtenerCursosPorPeriodo(nombre);
    }

    @GetMapping("/cursos/porPeriodo/{id}")
    @ResponseBody
    public List<Curso> obtenerCursosPorPeriodo(@PathVariable Long id) {
        return cursoService.obtenerCursosPorPeriodoID(id);
    }






}