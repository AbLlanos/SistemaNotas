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

}
