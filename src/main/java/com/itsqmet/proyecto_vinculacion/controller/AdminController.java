package com.itsqmet.proyecto_vinculacion.controller;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;
import com.itsqmet.proyecto_vinculacion.dto.CursoDTO;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.swing.text.Document;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public String vistaAdminEducacionBasica(
            @RequestParam(required = false) String nombrePeriodo,
            @RequestParam(required = false) String nombreCurso,
            @RequestParam(required = false) String nombreMateria,
            @RequestParam(required = false) String cedula,
            @RequestParam(required = false) String nombreTrimestre,
            Model model) {

        String nivel = "EducacionBasica";

        // Filtrar cursos SOLO por Educación Básica
        List<Curso> cursosFiltrados = cursoService.listarTodosCursos().stream()
                .filter(c -> c.getNivelEducativo() != null
                        && nivel.equalsIgnoreCase(c.getNivelEducativo().getNombre()))
                .toList();

        // Filtrar materias por curso si se filtró, si no, filtrar por nivel educativo
        List<Materia> materiasFiltradas;

        if (nombreCurso != null && !nombreCurso.isBlank()) {
            materiasFiltradas = materiaService.listarTodasMaterias().stream()
                    .filter(m -> m.getCursos() != null &&
                            m.getCursos().stream()
                                    .anyMatch(c -> nombreCurso.equalsIgnoreCase(c.getNombre())))
                    .toList();
        } else {
            materiasFiltradas = materiaService.listarTodasMaterias().stream()
                    .filter(m -> m.getNivelEducativo() != null
                            && nivel.equalsIgnoreCase(m.getNivelEducativo().getNombre()))
                    .toList();
        }

        // Listas para filtros base
        List<PeriodoAcademico> aniosLectivos = periodoAcademicoService.listarTodosPeriodosAcademicos();
        List<String> trimestres = trimestreService.listarTodosPeriodos().stream()
                .map(tr -> tr.getNombre())
                .collect(Collectors.toList());

        // Notas filtradas
        List<NotaCompletaDTO> notas = notasService.obtenerNotasCompletas(
                nombrePeriodo, nombreCurso, nombreMateria, cedula, nombreTrimestre
        );

        // Mantener filtros para la vista
        Map<String, Object> param = new HashMap<>();
        param.put("nombrePeriodo", nombrePeriodo);
        param.put("nombreCurso", nombreCurso);
        param.put("nombreMateria", nombreMateria);
        param.put("cedula", cedula);
        param.put("nombreTrimestre", nombreTrimestre);

        model.addAttribute("notas", notas);
        model.addAttribute("aniosLectivos", aniosLectivos);
        model.addAttribute("trimestres", trimestres);
        model.addAttribute("cursos", cursosFiltrados);
        model.addAttribute("materias", materiasFiltradas);
        model.addAttribute("param", param);

        return "pages/Admin/EducacionBasica/educacionBasicaVista";
    }














    //Ruta bachillerato
//Ruta bachillerato
    @GetMapping("/Bachillerato/bachilleratoVista")
    public String vistaBachillerato(
            @RequestParam(required = false) String nombrePeriodo,
            @RequestParam(required = false) String nombreCurso,
            @RequestParam(required = false) String nombreMateria,
            @RequestParam(required = false) String cedula,
            @RequestParam(required = false) String nombreTrimestre,
            Model model) {

        // Nivel esperado (sin espacios y en minúsculas para comparar)
        String nivelFiltro = "BachilleratoGeneral";

        // Filtrar cursos SOLO por Bachillerato General
        List<Curso> cursos = cursoService.listarTodosCursos().stream()
                .filter(c -> {
                    if (c.getNivelEducativo() == null || c.getNivelEducativo().getNombre() == null) {
                        return false;
                    }
                    String nivelActual = c.getNivelEducativo().getNombre().toLowerCase().replace(" ", "");
                    return nivelFiltro.equals(nivelActual);
                })
                .toList();

        // Filtrar materias por curso si hay filtro, o por nivel educativo si no
        List<Materia> materias;

        if (nombreCurso != null && !nombreCurso.isBlank()) {
            // Materias que pertenecen a algún curso con el nombre dado
            materias = materiaService.listarTodasMaterias().stream()
                    .filter(m -> m.getCursos() != null &&
                            m.getCursos().stream()
                                    .anyMatch(c -> nombreCurso.equalsIgnoreCase(c.getNombre())))
                    .toList();
        } else {
            // Materias que pertenecen al nivel educativo "Bachillerato General"
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

        // Trimestres fijos
        List<String> trimestres = List.of("Primer Trimestre", "Segundo Trimestre", "Tercer Trimestre");

        // Notas filtradas
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

        model.addAttribute("aniosLectivos", periodoAcademicoService.listarTodosPeriodosAcademicos());
        model.addAttribute("cursos", cursos);
        model.addAttribute("materias", materias);
        model.addAttribute("trimestres", trimestres);
        model.addAttribute("notas", notas);
        model.addAttribute("param", param);

        return "pages/Admin/Bachillerato/bachilleratoVista";
    }


    @GetMapping("/notas/nuevo")
    public String mostrarFormularioNuevaNota(Model model) {
        NotaCompletaDTO notaCompletaDTO = new NotaCompletaDTO();
        model.addAttribute("nota", notaCompletaDTO);

        model.addAttribute("estudiantes", estudianteService.listarTodosEstudiantes());
        model.addAttribute("periodos", periodoAcademicoService.listarTodosPeriodosAcademicos());
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

        // Listas auxiliares
        model.addAttribute("estudiantes", estudianteService.listarTodosEstudiantes());
        System.out.println("DEBUG: Total estudiantes = " + estudianteService.listarTodosEstudiantes().size());

        model.addAttribute("materias", materiaService.listarTodasMaterias());
        System.out.println("DEBUG: Total materias = " + materiaService.listarTodasMaterias().size());

        model.addAttribute("periodos", periodoAcademicoService.listarTodosPeriodosAcademicos());
        System.out.println("DEBUG: Total periodos = " + periodoAcademicoService.listarTodosPeriodosAcademicos().size());

        model.addAttribute("trimestres", trimestreService.listarTodosPeriodos());
        System.out.println("DEBUG: Total trimestres = " + trimestreService.listarTodosPeriodos().size());

        if (notaCompletaDTO.getPeriodoAcademicoId() != null) {
            System.out.println("DEBUG: Cargando cursos para periodo ID = " + notaCompletaDTO.getPeriodoAcademicoId());
            model.addAttribute("cursos", cursoService.obtenerCursosPorPeriodoID(notaCompletaDTO.getPeriodoAcademicoId()));
        } else {
            System.out.println("DEBUG: No hay PeriodoAcademicoId, listando todos los cursos.");
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

        return "pages/Admin/BachilleratoTecnico/bachilleratoTecnicoVista";
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
        String nivelFiltro = "BachilleratoGeneral";

        List<Curso> cursosPorPeriodo = cursoService.obtenerCursosPorPeriodo(nombrePeriodo);

        return cursosPorPeriodo.stream()
                .filter(c -> c.getNivelEducativo() != null
                        && c.getNivelEducativo().getNombre() != null
                        && c.getNivelEducativo().getNombre().equalsIgnoreCase(nivelFiltro))
                .map(c -> new CursoDTO(c.getId(), c.getNombre()))
                .toList();
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