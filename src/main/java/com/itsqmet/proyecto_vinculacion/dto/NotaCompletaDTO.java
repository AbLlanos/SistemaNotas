package com.itsqmet.proyecto_vinculacion.dto;

import com.itsqmet.proyecto_vinculacion.entity.PeriodoAcademico;
import lombok.Data;

@Data
public class NotaCompletaDTO {

    private Long idNota;
    private Long cursoId;
    private Long materiaId;
    private String tipoMateria;

    private String nombreEstudiante;
    private String cedulaEstudiante;
    private String nombreCompletoEstudiante;
    private String areaMateria;
    private String nombreCurso;
    private String cedula;

    private PeriodoAcademico periodoAcademico;
    private String nombrePeriodo;
    private Long periodoAcademicoId;

    // ---------- Primer Trimestre ----------
    private Double notaNumericaPrimerTrim;
    private String notaNumericaPrimerTrimFormateada;  // <== nuevo campo
    private String notaCualitativaPrimerTrim;
    private Integer asistenciaPrimerTrim;
    private String asistenciaPrimerTrimFormateada;   // <== nuevo campo
    private Integer faltasJustificadasPrimerTrim;
    private Integer faltasInjustificadasPrimerTrim;
    private Integer atrasosPrimerTrim;
    private String comportamientoPrimerTrim;
    private Integer totalAsistenciaPrimerTrim;

    // ---------- Segundo Trimestre ----------
    private Double notaNumericaSegundoTrim;
    private String notaNumericaSegundoTrimFormateada;  // <== nuevo campo
    private String notaCualitativaSegundoTrim;
    private Integer asistenciaSegundoTrim;
    private String asistenciaSegundoTrimFormateada;   // <== nuevo campo
    private Integer faltasJustificadasSegundoTrim;
    private Integer faltasInjustificadasSegundoTrim;
    private Integer atrasosSegundoTrim;
    private String comportamientoSegundoTrim;
    private Integer totalAsistenciaSegundoTrim;

    // ---------- Tercer Trimestre ----------
    private Double notaNumericaTercerTrim;
    private String notaNumericaTercerTrimFormateada;   // <== nuevo campo
    private String notaCualitativaTercerTrim;
    private Integer asistenciaTercerTrim;
    private String asistenciaTercerTrimFormateada;     // <== nuevo campo
    private Integer faltasJustificadasTercerTrim;
    private Integer faltasInjustificadasTercerTrim;
    private Integer atrasosTercerTrim;
    private String comportamientoTercerTrim;
    private Integer totalAsistenciaTercerTrim;

    private Long nivelEducativoId;
    private String estado;
    private Double promedioFinal;
    private String promedioFinalFormateado;            // <== nuevo campo

    private String comportamientoFinalVariable1;
    private String comportamientoFinalVariable2;
    private String comportamientoFinalVariable3;

    private String notaCualitativaFinalPrimerTrim;
    private String notaCualitativaFinalSegundoTrim;
    private String notaCualitativaFinalTercerTrim;

    private String asistenciaFinalVariable1;
    private String asistenciaFinalVariable2;
    private String asistenciaFinalVariable3;



}
