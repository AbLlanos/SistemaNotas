package com.itsqmet.proyecto_vinculacion.dto;

import com.itsqmet.proyecto_vinculacion.entity.PeriodoAcademico;
import lombok.Data;

@Data
public class NotaCompletaDTO {

    private Long idNota;

    // Identificadores de selección desde el formulario
    private Long cursoId;
    private Long materiaId;

    private String nombreEstudiante;
    private String cedulaEstudiante;
    private String nombreCompletoEstudiante;

    // nombre de la materia (texto); se llena desde hidden
    private String areaMateria;

    private String nombreCurso;
    private String cedula;

    private PeriodoAcademico periodoAcademico; // no se bindea directamente, sólo informativo
    private String nombrePeriodo;              // se llena desde Curso (data-periodo)
    private Long periodoAcademicoId;

    // ---------- Primer Trimestre ----------
    private Double  notaNumericaPrimerTrim;
    private String  notaCualitativaPrimerTrim;
    private Integer asistenciaPrimerTrim;
    private Integer faltasJustificadasPrimerTrim;
    private Integer faltasInjustificadasPrimerTrim;
    private Integer atrasosPrimerTrim;
    private String  comportamientoPrimerTrim;
    private Integer totalAsistenciaPrimerTrim;

    // ---------- Segundo Trimestre ----------
    private Double  notaNumericaSegundoTrim;
    private String  notaCualitativaSegundoTrim;
    private Integer asistenciaSegundoTrim;
    private Integer faltasJustificadasSegundoTrim;
    private Integer faltasInjustificadasSegundoTrim;
    private Integer atrasosSegundoTrim;
    private String  comportamientoSegundoTrim;
    private Integer totalAsistenciaSegundoTrim;

    // ---------- Tercer Trimestre ----------
    private Double  notaNumericaTercerTrim;
    private String  notaCualitativaTercerTrim;
    private Integer asistenciaTercerTrim;
    private Integer faltasJustificadasTercerTrim;
    private Integer faltasInjustificadasTercerTrim;
    private Integer atrasosTercerTrim;
    private String  comportamientoTercerTrim;
    private Integer totalAsistenciaTercerTrim;


    private String estado;
    private Double promedioFinal;

    private String comportamientoFinalVariable1;
    private String comportamientoFinalVariable2;
    private String comportamientoFinalVariable3;


}