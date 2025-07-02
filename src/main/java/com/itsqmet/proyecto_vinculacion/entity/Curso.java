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
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    //Conexion con curso
    @OneToMany(mappedBy = "curso", fetch = FetchType.LAZY)
    private List<Materia> materias;

    //Conexion con nivel Educativo
    @ManyToOne
    @JoinColumn(name = "nivel_educativo_id")
    private NivelEducativo nivelEducativo;

}
