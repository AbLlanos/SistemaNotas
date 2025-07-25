package com.itsqmet.proyecto_vinculacion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer asistencias;
    private Integer faltasJustificadas;
    private Integer faltasInjustificadas;
    private Integer atrasos;
    private Integer totalAsistencias;

    @ManyToOne
    private Estudiante estudiante;

    @ManyToOne
    private Trimestre trimestre;

    @ManyToOne
    private PeriodoAcademico periodo;

    @ManyToOne
    private Materia materia;


}
