package com.itsqmet.proyecto_vinculacion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double notaNumerica;

    private String notaCualitativa;

    @ManyToOne
    private Estudiante estudiante;

    @ManyToOne
    private Materia materia;

    @ManyToOne
    private Trimestre trimestre;

    @ManyToOne
    private PeriodoAcademico periodoAcademico;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getNotaNumerica() {
        return notaNumerica;
    }

    public void setNotaNumerica(Double notaNumerica) {
        this.notaNumerica = notaNumerica;
    }

    public String getNotaCualitativa() {
        return notaCualitativa;
    }

    public void setNotaCualitativa(String notaCualitativa) {
        this.notaCualitativa = notaCualitativa;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public Trimestre getTrimestre() {
        return trimestre;
    }

    public void setTrimestre(Trimestre trimestre) {
        this.trimestre = trimestre;
    }

    public PeriodoAcademico getPeriodoAcademico() {
        return periodoAcademico;
    }

    public void setPeriodoAcademico(PeriodoAcademico periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }
}

