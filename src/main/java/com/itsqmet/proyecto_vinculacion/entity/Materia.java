package com.itsqmet.proyecto_vinculacion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;  // Importa esto
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

    // Conexión con docente
    @ManyToOne
    @JoinColumn(name = "docente_id")
    private Docente docente;

    // Esta relación puede generar ciclo al serializar, ponle @JsonIgnore
    @ManyToMany(mappedBy = "materias")
    @JsonIgnore
    private List<Curso> cursos;

    // Otras relaciones
    @OneToMany(mappedBy = "materia", fetch = FetchType.LAZY)
    private List<Asistencia> asistencias;

}
