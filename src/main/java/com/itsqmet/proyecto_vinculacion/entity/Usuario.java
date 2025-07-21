package com.itsqmet.proyecto_vinculacion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String rol;
    private String cedula;
    private String direccion;

    @Column(nullable = false)
    private Boolean visible = true;

    @PrePersist
    public void prePersist() {
        if (visible == null) visible = true;
    }
}
