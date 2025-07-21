package com.itsqmet.proyecto_vinculacion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "estudiante")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Estudiante extends Usuario {

    @ManyToMany
    @JoinTable(
            name = "estudiante_curso",
            joinColumns = @JoinColumn(name = "estudiante_id"),
            inverseJoinColumns = @JoinColumn(name = "curso_id")
    )
    private List<Curso> cursos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nivel_educativo_id")
    private NivelEducativo nivelEducativo;

    @OneToMany(mappedBy = "estudiante", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Asistencia> asistencias;
}