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


    public Long getIdNota() {
        return idNota;
    }

    public void setIdNota(Long idNota) {
        this.idNota = idNota;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public Long getMateriaId() {
        return materiaId;
    }

    public void setMateriaId(Long materiaId) {
        this.materiaId = materiaId;
    }

    public String getNombreEstudiante() {
        return nombreEstudiante;
    }

    public void setNombreEstudiante(String nombreEstudiante) {
        this.nombreEstudiante = nombreEstudiante;
    }

    public String getCedulaEstudiante() {
        return cedulaEstudiante;
    }

    public void setCedulaEstudiante(String cedulaEstudiante) {
        this.cedulaEstudiante = cedulaEstudiante;
    }

    public String getNombreCompletoEstudiante() {
        return nombreCompletoEstudiante;
    }

    public void setNombreCompletoEstudiante(String nombreCompletoEstudiante) {
        this.nombreCompletoEstudiante = nombreCompletoEstudiante;
    }

    public String getAreaMateria() {
        return areaMateria;
    }

    public void setAreaMateria(String areaMateria) {
        this.areaMateria = areaMateria;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public void setNombreCurso(String nombreCurso) {
        this.nombreCurso = nombreCurso;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public PeriodoAcademico getPeriodoAcademico() {
        return periodoAcademico;
    }

    public void setPeriodoAcademico(PeriodoAcademico periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }

    public String getNombrePeriodo() {
        return nombrePeriodo;
    }

    public void setNombrePeriodo(String nombrePeriodo) {
        this.nombrePeriodo = nombrePeriodo;
    }

    public Long getPeriodoAcademicoId() {
        return periodoAcademicoId;
    }

    public void setPeriodoAcademicoId(Long periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
    }

    public Double getNotaNumericaPrimerTrim() {
        return notaNumericaPrimerTrim;
    }

    public void setNotaNumericaPrimerTrim(Double notaNumericaPrimerTrim) {
        this.notaNumericaPrimerTrim = notaNumericaPrimerTrim;
    }

    public String getNotaCualitativaPrimerTrim() {
        return notaCualitativaPrimerTrim;
    }

    public void setNotaCualitativaPrimerTrim(String notaCualitativaPrimerTrim) {
        this.notaCualitativaPrimerTrim = notaCualitativaPrimerTrim;
    }

    public Integer getAsistenciaPrimerTrim() {
        return asistenciaPrimerTrim;
    }

    public void setAsistenciaPrimerTrim(Integer asistenciaPrimerTrim) {
        this.asistenciaPrimerTrim = asistenciaPrimerTrim;
    }

    public Integer getFaltasJustificadasPrimerTrim() {
        return faltasJustificadasPrimerTrim;
    }

    public void setFaltasJustificadasPrimerTrim(Integer faltasJustificadasPrimerTrim) {
        this.faltasJustificadasPrimerTrim = faltasJustificadasPrimerTrim;
    }

    public Integer getFaltasInjustificadasPrimerTrim() {
        return faltasInjustificadasPrimerTrim;
    }

    public void setFaltasInjustificadasPrimerTrim(Integer faltasInjustificadasPrimerTrim) {
        this.faltasInjustificadasPrimerTrim = faltasInjustificadasPrimerTrim;
    }

    public Integer getAtrasosPrimerTrim() {
        return atrasosPrimerTrim;
    }

    public void setAtrasosPrimerTrim(Integer atrasosPrimerTrim) {
        this.atrasosPrimerTrim = atrasosPrimerTrim;
    }

    public String getComportamientoPrimerTrim() {
        return comportamientoPrimerTrim;
    }

    public void setComportamientoPrimerTrim(String comportamientoPrimerTrim) {
        this.comportamientoPrimerTrim = comportamientoPrimerTrim;
    }

    public Integer getTotalAsistenciaPrimerTrim() {
        return totalAsistenciaPrimerTrim;
    }

    public void setTotalAsistenciaPrimerTrim(Integer totalAsistenciaPrimerTrim) {
        this.totalAsistenciaPrimerTrim = totalAsistenciaPrimerTrim;
    }

    public Double getNotaNumericaSegundoTrim() {
        return notaNumericaSegundoTrim;
    }

    public void setNotaNumericaSegundoTrim(Double notaNumericaSegundoTrim) {
        this.notaNumericaSegundoTrim = notaNumericaSegundoTrim;
    }

    public String getNotaCualitativaSegundoTrim() {
        return notaCualitativaSegundoTrim;
    }

    public void setNotaCualitativaSegundoTrim(String notaCualitativaSegundoTrim) {
        this.notaCualitativaSegundoTrim = notaCualitativaSegundoTrim;
    }

    public Integer getAsistenciaSegundoTrim() {
        return asistenciaSegundoTrim;
    }

    public void setAsistenciaSegundoTrim(Integer asistenciaSegundoTrim) {
        this.asistenciaSegundoTrim = asistenciaSegundoTrim;
    }

    public Integer getFaltasJustificadasSegundoTrim() {
        return faltasJustificadasSegundoTrim;
    }

    public void setFaltasJustificadasSegundoTrim(Integer faltasJustificadasSegundoTrim) {
        this.faltasJustificadasSegundoTrim = faltasJustificadasSegundoTrim;
    }

    public Integer getFaltasInjustificadasSegundoTrim() {
        return faltasInjustificadasSegundoTrim;
    }

    public void setFaltasInjustificadasSegundoTrim(Integer faltasInjustificadasSegundoTrim) {
        this.faltasInjustificadasSegundoTrim = faltasInjustificadasSegundoTrim;
    }

    public Integer getAtrasosSegundoTrim() {
        return atrasosSegundoTrim;
    }

    public void setAtrasosSegundoTrim(Integer atrasosSegundoTrim) {
        this.atrasosSegundoTrim = atrasosSegundoTrim;
    }

    public String getComportamientoSegundoTrim() {
        return comportamientoSegundoTrim;
    }

    public void setComportamientoSegundoTrim(String comportamientoSegundoTrim) {
        this.comportamientoSegundoTrim = comportamientoSegundoTrim;
    }

    public Integer getTotalAsistenciaSegundoTrim() {
        return totalAsistenciaSegundoTrim;
    }

    public void setTotalAsistenciaSegundoTrim(Integer totalAsistenciaSegundoTrim) {
        this.totalAsistenciaSegundoTrim = totalAsistenciaSegundoTrim;
    }

    public Double getNotaNumericaTercerTrim() {
        return notaNumericaTercerTrim;
    }

    public void setNotaNumericaTercerTrim(Double notaNumericaTercerTrim) {
        this.notaNumericaTercerTrim = notaNumericaTercerTrim;
    }

    public String getNotaCualitativaTercerTrim() {
        return notaCualitativaTercerTrim;
    }

    public void setNotaCualitativaTercerTrim(String notaCualitativaTercerTrim) {
        this.notaCualitativaTercerTrim = notaCualitativaTercerTrim;
    }

    public Integer getAsistenciaTercerTrim() {
        return asistenciaTercerTrim;
    }

    public void setAsistenciaTercerTrim(Integer asistenciaTercerTrim) {
        this.asistenciaTercerTrim = asistenciaTercerTrim;
    }

    public Integer getFaltasJustificadasTercerTrim() {
        return faltasJustificadasTercerTrim;
    }

    public void setFaltasJustificadasTercerTrim(Integer faltasJustificadasTercerTrim) {
        this.faltasJustificadasTercerTrim = faltasJustificadasTercerTrim;
    }

    public Integer getFaltasInjustificadasTercerTrim() {
        return faltasInjustificadasTercerTrim;
    }

    public void setFaltasInjustificadasTercerTrim(Integer faltasInjustificadasTercerTrim) {
        this.faltasInjustificadasTercerTrim = faltasInjustificadasTercerTrim;
    }

    public Integer getAtrasosTercerTrim() {
        return atrasosTercerTrim;
    }

    public void setAtrasosTercerTrim(Integer atrasosTercerTrim) {
        this.atrasosTercerTrim = atrasosTercerTrim;
    }

    public String getComportamientoTercerTrim() {
        return comportamientoTercerTrim;
    }

    public void setComportamientoTercerTrim(String comportamientoTercerTrim) {
        this.comportamientoTercerTrim = comportamientoTercerTrim;
    }

    public Integer getTotalAsistenciaTercerTrim() {
        return totalAsistenciaTercerTrim;
    }

    public void setTotalAsistenciaTercerTrim(Integer totalAsistenciaTercerTrim) {
        this.totalAsistenciaTercerTrim = totalAsistenciaTercerTrim;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Double getPromedioFinal() {
        return promedioFinal;
    }

    public void setPromedioFinal(Double promedioFinal) {
        this.promedioFinal = promedioFinal;
    }

    public String getComportamientoFinalVariable1() {
        return comportamientoFinalVariable1;
    }

    public void setComportamientoFinalVariable1(String comportamientoFinalVariable1) {
        this.comportamientoFinalVariable1 = comportamientoFinalVariable1;
    }

    public String getComportamientoFinalVariable2() {
        return comportamientoFinalVariable2;
    }

    public void setComportamientoFinalVariable2(String comportamientoFinalVariable2) {
        this.comportamientoFinalVariable2 = comportamientoFinalVariable2;
    }

    public String getComportamientoFinalVariable3() {
        return comportamientoFinalVariable3;
    }

    public void setComportamientoFinalVariable3(String comportamientoFinalVariable3) {
        this.comportamientoFinalVariable3 = comportamientoFinalVariable3;
    }
}