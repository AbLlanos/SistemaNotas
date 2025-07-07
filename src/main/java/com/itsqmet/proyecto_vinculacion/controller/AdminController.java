package com.itsqmet.proyecto_vinculacion.controller;

import com.itsqmet.proyecto_vinculacion.entity.Notas;
import com.itsqmet.proyecto_vinculacion.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private NotasService notasService;

    @Autowired
    private CursoService cursoService;

    @Autowired
    private MateriaService materiaService;

    @Autowired
    private TrimestreService trimestreService;

    @Autowired
    private PeriodoAcademicoService periodoAcademicoService;

    @Autowired
    private NivelEducativoService nivelEducativoService;


    //1. Sistema de notas

    @GetMapping("/pages/Admin/vistaAdmin")
    public String vistaAdmin(
            @RequestParam(required = false) String nombrePeriodo,
            @RequestParam(required = false) String nombreCurso,
            @RequestParam(required = false) String nombreMateria,
            @RequestParam(required = false) String cedula,
            @RequestParam(required = false) String nombreTrimestre,
            Model model) {

        List<Notas> notas = notasService.buscarNotasPorFiltros(
                nombrePeriodo, nombreCurso, nombreMateria, cedula, nombreTrimestre
        );

        model.addAttribute("notas", notas);
        model.addAttribute("cursos", cursoService.mostrarCursos());
        model.addAttribute("materias", materiaService.obtenerTodasLasMaterias());
        model.addAttribute("trimestres", trimestreService.obtenerTodos());
        model.addAttribute("aniosLectivos", periodoAcademicoService.listarTodos());
        model.addAttribute("nivelesEducativos", nivelEducativoService.listarTodos());

        return "pages/Admin/vistaAdmin";
    }




}