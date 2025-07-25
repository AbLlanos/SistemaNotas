package com.itsqmet.proyecto_vinculacion.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "El nombre completo no puede estar vacío")
    private String nombre;

    private String apellido;

    @Email(message = "El correo no es válido")
    @NotBlank(message = "El email es obligatorio")
    @Column(unique = true)
    private String email;

    private String password;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    private String rol;

    @NotBlank(message = "La cédula es obligatoria")
    @Size(min = 10, max = 10, message = "La cédula debe tener 10 caracteres")
    @Column(unique = true)
    private String cedula;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @Column(nullable = false)
    private Boolean visible = true;

    @PrePersist
    public void prePersist() {
        if (visible == null) visible = true;
    }
}
