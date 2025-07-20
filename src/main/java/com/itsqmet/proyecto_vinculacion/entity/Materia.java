package com.itsqmet.proyecto_vinculacion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "materia")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    /* Docente responsable (opcional). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id")
    private Docente docente;

    /* Nivel educativo al que pertenece esta materia (para filtrar en formularios). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nivel_educativo_id")
    private NivelEducativo nivelEducativo;

    /* Relación inversa: cursos que incluyen esta materia. */
    @ManyToMany(mappedBy = "materias", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Curso> cursos;

    /* Otras relaciones */
    @OneToMany(mappedBy = "materia", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Asistencia> asistencias;
}
