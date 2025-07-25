package com.itsqmet.proyecto_vinculacion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trimestre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    //Conexion con notas
    @OneToMany(mappedBy = "trimestre", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Notas> notas;
}
