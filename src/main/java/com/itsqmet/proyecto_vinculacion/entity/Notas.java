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

    private Double deber;
    private Double taller;
    private Double pruebas;
    private Double participacion;
    private Double examen;
    private Double notaTotal;
    private Double recuperacion;

    //Conexion con materia
    @ManyToOne
    @JoinColumn(name = "materia_id")
    private Materia materia;

    //Conexion con estudiante
    @ManyToOne
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudiante;

    //Conexion con trimestre
    @ManyToOne
    @JoinColumn(name = "trimestre_id")
    private Trimestre trimestre;

    //Conexion con periodo Academico
    @ManyToOne
    @JoinColumn(name = "periodo_academico_id")
    private PeriodoAcademico periodoAcademico;

}
