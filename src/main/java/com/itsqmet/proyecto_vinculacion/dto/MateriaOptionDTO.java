package com.itsqmet.proyecto_vinculacion.dto;

public class MateriaOptionDTO {
    private Long id;
    private String nombre;

    public MateriaOptionDTO(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
}
