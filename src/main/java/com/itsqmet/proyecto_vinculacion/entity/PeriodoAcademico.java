package com.itsqmet.proyecto_vinculacion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "periodo_academico")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PeriodoAcademico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    /* Notas registradas en este periodo (por materia, estudiante, trimestre). */
    @OneToMany(mappedBy = "periodoAcademico", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Notas> notas;

    /* Cursos creados en este periodo. */
    @OneToMany(mappedBy = "periodoAcademico", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Curso> cursos;
}
