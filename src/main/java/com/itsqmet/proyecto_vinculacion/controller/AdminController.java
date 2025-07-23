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










}