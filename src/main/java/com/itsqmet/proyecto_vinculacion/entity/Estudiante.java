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

    /* Cursos en los que está inscrito este estudiante (lado dueño). */
    @ManyToMany
    @JoinTable(
            name = "estudiante_curso",
            joinColumns = @JoinColumn(name = "estudiante_id"),
            inverseJoinColumns = @JoinColumn(name = "curso_id")
    )
    private List<Curso> cursos;

    /* Nivel educativo (sirve para filtrar cursos/materias válidos en UI). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nivel_educativo_id")
    private NivelEducativo nivelEducativo;

    /* Relación con asistencias (y de ahí puedes derivar notas, etc.). */
    @OneToMany(mappedBy = "estudiante", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Asistencia> asistencias;
}
