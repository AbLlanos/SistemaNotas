package com.itsqmet.proyecto_vinculacion.dto;

public class NotaCompletaDTO {

    private Long idNota;
    private String nombreEstudiante;
    private String cedulaEstudiante;
    private String nombreCompletoEstudiante;
    private String areaMateria;
    private String nombreCurso;
    private String cedula;

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    private String nombrePeriodo;

    public String getNombreEstudiante() {
        return nombreEstudiante;
    }

    public void setNombreEstudiante(String nombreEstudiante) {
        this.nombreEstudiante = nombreEstudiante;
    }

    public String getNombrePeriodo() {
        return nombrePeriodo;
    }

    public void setNombrePeriodo(String nombrePeriodo) {
        this.nombrePeriodo = nombrePeriodo;
    }


    public String getNombreCurso() {
        return nombreCurso;
    }

    public void setNombreCurso(String nombreCurso) {
        this.nombreCurso = nombreCurso;
    }

    // Primer trimestre

    private Double notaNumericaPrimerTrim;
    private String notaCualitativaPrimerTrim;

    private Integer asistenciaPrimerTrim;
    private Integer faltasJustificadasPrimerTrim;
    private Integer faltasInjustificadasPrimerTrim;
    private Integer atrasosPrimerTrim;
    private String comportamientoPrimerTrim;
    private Integer totalAsistenciaPrimerTrim;




    // Segundo trimestre

    private Double notaNumericaSegundoTrim;
    private String notaCualitativaSegundoTrim;

    private Integer asistenciaSegundoTrim;
    private Integer faltasJustificadasSegundoTrim;
    private Integer faltasInjustificadasSegundoTrim;
    private Integer atrasosSegundoTrim;
    private String comportamientoSegundoTrim;

    private Integer TotalAsistenciaSegundoTrim;

    // Tercer trimestre

    private Double notaNumericaTercerTrim;
    private String notaCualitativaTercerTrim;

    private Integer asistenciaTercerTrim;
    private Integer faltasJustificadasTercerTrim;
    private Integer faltasInjustificadasTercerTrim;
    private Integer atrasosTercerTrim;
    private String comportamientoTercerTrim;
    private Integer TotalAsistenciaTercerTrim;

    public Integer getTotalAsistenciaSegundoTrim() {
        return TotalAsistenciaSegundoTrim;
    }

    public void setTotalAsistenciaSegundoTrim(Integer totalAsistenciaSegundoTrim) {
        TotalAsistenciaSegundoTrim = totalAsistenciaSegundoTrim;
    }

    public Integer getTotalAsistenciaTercerTrim() {
        return TotalAsistenciaTercerTrim;
    }

    public void setTotalAsistenciaTercerTrim(Integer totalAsistenciaTercerTrim) {
        TotalAsistenciaTercerTrim = totalAsistenciaTercerTrim;
    }

    public Long getIdNota() {
        return idNota;
    }

    public void setIdNota(Long idNota) {
        this.idNota = idNota;
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


}
