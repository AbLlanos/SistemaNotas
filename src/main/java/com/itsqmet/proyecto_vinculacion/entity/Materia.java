package com.itsqmet.proyecto_vinculacion.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "materia")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(name = "tipo_materia")
    private String tipoMateria;

    @Column(name = "visible")
    private Boolean visible = true;

    /* Docente responsable (opcional). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id")
    private Docente docente;

    /* Nivel educativo al que pertenece esta materia (para filtrar en formularios). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nivel_educativo_id")
    private NivelEducativo nivelEducativo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_academico_id")
    private PeriodoAcademico periodoAcademico;

    /* Relación inversa: cursos que incluyen esta materia. */
    @ManyToMany(mappedBy = "materias", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Curso> cursos;

    /* Otras relaciones */
    @OneToMany(mappedBy = "materia", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Asistencia> asistencias;
}
