package com.itsqmet.proyecto_vinculacion.dto;

public class EstudianteOptionDTO {
    private String cedula;
    private String nombreCompleto;

    public EstudianteOptionDTO(String cedula, String nombreCompleto) {
        this.cedula = cedula;
        this.nombreCompleto = nombreCompleto;
    }

    public String getCedula() {
        return cedula;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }
}