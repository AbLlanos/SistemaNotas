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

    private String cantidadTotalDias;
    private String cantidadTotalAsistenciasEstudiante;
    private String cantidadFaltas;
    private String cantidadAtrasos;
    private String cantidadFaltasInjustificadas;


    //Conexion con estudiante
    @ManyToOne
    @JoinColumn(name="id_estudiante")
    private Estudiante estudiante;

    //Conexion con materia
    @ManyToOne
    @JoinColumn(name = "id_materia")
    private Materia materia;


}
