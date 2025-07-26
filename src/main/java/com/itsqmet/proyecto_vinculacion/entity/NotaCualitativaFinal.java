package com.itsqmet.proyecto_vinculacion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        name = "nota_cualitativa_final",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"estudiante_id", "curso_id", "periodo_id"}
        )
)
@Data
public class NotaCualitativaFinal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nota_final_primer_trim", length = 255)
    private String notaFinalPrimerTrim;

    @Column(name = "nota_final_segundo_trim", length = 255)
    private String notaFinalSegundoTrim;

    @Column(name = "nota_final_tercer_trim", length = 255)
    private String notaFinalTercerTrim;


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudiante;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "periodo_id")
    private PeriodoAcademico periodo;

}

