
package com.itsqmet.proyecto_vinculacion.dto;

public class FiltroDTO {
    private String nombrePeriodo;
    private String nombreCurso;
    private String nombreMateria;
    private String cedula;
    private String nombreTrimestre;

    public FiltroDTO() {}

    public FiltroDTO(String nombrePeriodo, String nombreCurso, String nombreMateria, String cedula, String nombreTrimestre) {
        this.nombrePeriodo = nombrePeriodo;
        this.nombreCurso = nombreCurso;
        this.nombreMateria = nombreMateria;
        this.cedula = cedula;
        this.nombreTrimestre = nombreTrimestre;
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

    public String getNombreMateria() {
        return nombreMateria;
    }

    public void setNombreMateria(String nombreMateria) {
        this.nombreMateria = nombreMateria;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombreTrimestre() {
        return nombreTrimestre;
    }

    public void setNombreTrimestre(String nombreTrimestre) {
        this.nombreTrimestre = nombreTrimestre;
    }
}