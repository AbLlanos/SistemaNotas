package com.itsqmet.proyecto_vinculacion.controller;

import com.itsqmet.proyecto_vinculacion.dto.FiltroDTO;
import com.itsqmet.proyecto_vinculacion.dto.NotaCompletaDTO;
import com.itsqmet.proyecto_vinculacion.entity.*;
import com.itsqmet.proyecto_vinculacion.service.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
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

    @GetMapping("/pages/Admin/vistaAdmin")
    public String vistaAdmin(
            @RequestParam(required = false) String nombrePeriodo,
            @RequestParam(required = false) String nombreCurso,
            @RequestParam(required = false) String nombreMateria,
            @RequestParam(required = false) String cedula,
            @RequestParam(required = false) String nombreTrimestre,
            Model model
    ) {
        // Obtener datos para filtros
        List<PeriodoAcademico> aniosLectivos = periodoAcademicoService.listarTodos();
        List<Curso> cursos = cursoService.mostrarCursos();
        List<Materia> materias1 = materiaService.obtenerTodasLasMaterias();
        List<Trimestre> trimestres = trimestreService.obtenerTodos();

        // Obtener notas filtradas (o todas si no hay filtros)
        List<Notas> notas = notasService.buscarNotasPorFiltros(
                nombrePeriodo, nombreCurso, nombreMateria, cedula, nombreTrimestre);

        // Añadir todo al modelo para Thymeleaf
        model.addAttribute("aniosLectivos", aniosLectivos);
        model.addAttribute("cursos", cursos);
        model.addAttribute("materias1", materias1);
        model.addAttribute("trimestres", trimestres);
        model.addAttribute("notas", notas);

        // Parámetros actuales para mantener selección en filtros
        model.addAttribute("param", new FiltroDTO(nombrePeriodo, nombreCurso, nombreMateria, cedula, nombreTrimestre));

        return "pages/Admin/vistaAdmin";
    }














    //Ruta educaion basica

    @GetMapping("/pages/Admin/EducacionBasica/educacionBasicaVista")
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
        model.addAttribute("aniosLectivos", periodoAcademicoService.listarTodos());
        model.addAttribute("trimestres", trimestreService.obtenerTodos());
        model.addAttribute("cursos", cursosFiltrados);
        model.addAttribute("materias", materiasFiltradas);

        return "pages/Admin/EducacionBasica/educacionBasicaVista";
    }














    //Ruta bachillerato

    @GetMapping("/pages/Admin/Bachillerato/bachilleratoVista")
    public String vistaAdminBachillerato(
            @RequestParam(required = false) String nombrePeriodo,
            @RequestParam(required = false) String nombreCurso,
            @RequestParam(required = false) String nombreMateria,
            @RequestParam(required = false) String cedula,
            @RequestParam(required = false) String nombreTrimestre,
            Model model) {

        List<NotaCompletaDTO> notasCompletas = notasService.obtenerNotasCompletas(
                nombrePeriodo, nombreCurso, nombreMateria, cedula, nombreTrimestre);

        model.addAttribute("notas", notasCompletas);

        // Otros atributos para filtros
        model.addAttribute("aniosLectivos", periodoAcademicoService.listarTodos());
        model.addAttribute("trimestres", trimestreService.obtenerTodos());
        model.addAttribute("cursos", cursoService.findByNivelEducativoNombre("BachilleratoGeneral"));
        model.addAttribute("materias", materiaService.findByCursoNivelEducativoNombre("BachilleratoGeneral"));

        return "pages/Admin/Bachillerato/bachilleratoVista";
    }


    @GetMapping("/notas/nuevo")
    public String mostrarFormularioNota(Model model) {
        model.addAttribute("notaCompletaDTO", new NotaCompletaDTO()); // para binding opcional
        model.addAttribute("estudiantes", estudianteService.obtenerTodos());
        model.addAttribute("materias", materiaService.obtenerTodasLasMaterias());
        model.addAttribute("periodos", periodoAcademicoService.listarTodos());
        model.addAttribute("trimestres", trimestreService.obtenerTodos());
        return "pages/Admin/Bachillerato/bachilleratoForm";
    }



    @PostMapping("/notas/guardar")
    public String guardarNotaCompleta(@ModelAttribute NotaCompletaDTO notaCompletaDTO) {
        notasService.guardarNotasDesdeFormulario(notaCompletaDTO);
        return "redirect:/pages/Admin/Bachillerato/bachilleratoVista"; // o la vista que muestre notas
    }


    @GetMapping("/notas/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        Notas nota = notasService.buscarNotaPorId(id);
        model.addAttribute("nota", nota);

        // También carga listas para selects, si tienes
        model.addAttribute("estudiantes", estudianteService.obtenerTodos());
        model.addAttribute("materias", materiaService.obtenerTodasLasMaterias());
        model.addAttribute("trimestres", trimestreService.obtenerTodos());
        model.addAttribute("periodos", periodoAcademicoService.listarTodos());

        return "notas/editarNota"; // plantilla Thymeleaf con el formulario
    }









    //Ruta Bachillerato Tecnico

    @GetMapping("/pages/Admin/BachilleratoTecnico/bachilleratoTecnicoVista")
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
        model.addAttribute("aniosLectivos", periodoAcademicoService.listarTodos());
        model.addAttribute("trimestres", trimestreService.obtenerTodos());
        model.addAttribute("cursos", cursosFiltrados);
        model.addAttribute("materias", materiasFiltradas);

        return "pages/Admin/Bachillerato/bachilleratoVista";
    }

    //Ruta educacion inicial

    @GetMapping("/pages/Admin/Inicial/educacionInicialVista")
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
        model.addAttribute("aniosLectivos", periodoAcademicoService.listarTodos());
        model.addAttribute("trimestres", trimestreService.obtenerTodos());
        model.addAttribute("cursos", cursosFiltrados);
        model.addAttribute("materias", materiasFiltradas);

        return "pages/Admin/Inicial/educacionInicialVista";
    }
















    //Generar PDF dinamico

    @GetMapping("/admin/generar-pdf")
    public void generarPdf(
            @RequestParam Long idNota,
            @RequestParam(required = false) String periodo,
            @RequestParam(required = false) String curso,
            @RequestParam(required = false) String materia,
            @RequestParam(required = false) String cedula,
            @RequestParam(required = false) String trimestre,
            HttpServletResponse response) throws IOException {

        Notas nota = notasService.buscarNotaPorId(idNota);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=notas_estudiante.pdf");

        pdfGeneratorService.generarDesdeNota(nota, periodo, curso, materia, cedula, trimestre, response.getOutputStream());
    }

}