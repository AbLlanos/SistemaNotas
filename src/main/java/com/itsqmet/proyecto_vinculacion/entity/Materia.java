package com.itsqmet.proyecto_vinculacion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    //Conexion con docente
    @ManyToOne
    @JoinColumn(name = "docente_id")
    private Docente docente;

    //Conexion con curso
    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;

    //Conexion con asistencias
    @OneToMany(mappedBy = "materia", fetch = FetchType.LAZY)
    private List<Asistencia> asistencias;

}
