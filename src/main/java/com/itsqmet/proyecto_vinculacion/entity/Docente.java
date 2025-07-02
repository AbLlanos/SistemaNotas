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
public class Docente extends Usuario {

    //Conexion con docente
    @OneToMany(mappedBy = "docente", fetch = FetchType.LAZY)
    private List<Materia> materias;
}
