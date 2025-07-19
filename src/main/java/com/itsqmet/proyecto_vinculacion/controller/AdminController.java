package com.itsqmet.proyecto_vinculacion.controller;

import com.itsqmet.proyecto_vinculacion.dto.NotaCompletaDTO;
import com.itsqmet.proyecto_vinculacion.entity.*;
import com.itsqmet.proyecto_vinculacion.service.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

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
    public String vistaAdminBachillerato(
            @RequestParam(required = false) String nombrePeriodo,
            @RequestParam(required = false) String nombreCurso,
            @RequestParam(required = false) String nombreMateria,
            @RequestParam(required = false) String cedula,
            @RequestParam(required = false) String nombreTrimestre,
            Model model) {

        // Obtener las notas aplicando los filtros seleccionados.
        List<NotaCompletaDTO> notasCompletas = notasService.obtenerNotasCompletas(
                nombrePeriodo, nombreCurso, nombreMateria, cedula, nombreTrimestre);

        // Se añade la lista de notas filtradas al modelo para mostrar en la tabla.
        model.addAttribute("notas", notasCompletas);

        // Cargar datos de filtros dinámicos: periodos, trimestres, cursos y materias.
        model.addAttribute("aniosLectivos", periodoAcademicoService.listarTodosPeriodosAcademicos());
        model.addAttribute("trimestres", trimestreService.listarTodosPeriodos());
        model.addAttribute("cursos", cursoService.findByNivelEducativoNombre("BachilleratoGeneral"));
        model.addAttribute("materias", materiaService.findByCursoNivelEducativoNombre("BachilleratoGeneral"));



        return "pages/Admin/Bachillerato/bachilleratoVista";
    }


    @GetMapping("/notas/nuevo")
    public String mostrarFormularioNota(Model model) {
        model.addAttribute("nota", new NotaCompletaDTO());
        model.addAttribute("estudiantes", estudianteService.listarTodosEstudiantes());
        model.addAttribute("materias", materiaService.listarTodasMaterias());
        model.addAttribute("periodos", periodoAcademicoService.listarTodosPeriodosAcademicos());
        model.addAttribute("trimestres", trimestreService.listarTodosPeriodos());
        return "pages/Admin/Bachillerato/bachilleratoForm";
    }



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