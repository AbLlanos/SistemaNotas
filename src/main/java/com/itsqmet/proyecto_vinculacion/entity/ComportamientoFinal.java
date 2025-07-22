package com.itsqmet.proyecto_vinculacion.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        name = "comportamiento_curso_final",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"estudiante_id","curso_id","periodo_id"}
        )
)
@Data
public class ComportamientoFinal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Relaciones clave ---
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudiante;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_id")
    private PeriodoAcademico periodo;

    // --- Valores finales (uno por trimestre) ---
    @Column(name = "comp_primer_trim", length = 255)
    private String comportamientoPrimerTrim;

    @Column(name = "comp_segundo_trim", length = 255)
    private String comportamientoSegundoTrim;

    @Column(name = "comp_tercer_trim", length = 255)
    private String comportamientoTercerTrim;

}
