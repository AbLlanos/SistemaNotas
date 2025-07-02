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
public class Estudiante extends Usuario {


    //Conexion con curso
    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;

    //Conexion con estudiante
    @OneToMany(mappedBy = "estudiante", fetch = FetchType.LAZY)
    private List<Asistencia> asistencias;
}
