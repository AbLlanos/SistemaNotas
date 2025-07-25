package com.itsqmet.proyecto_vinculacion.repository;

import com.itsqmet.proyecto_vinculacion.entity.Estudiante;
import com.itsqmet.proyecto_vinculacion.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Long> {

    //Validar si existe un estudiante por cedula
    Optional<Usuario> findByCedula(String cedula);
    //Validar si existe un estudiante por correo
    Optional<Usuario> findByEmail(String email);

    boolean existsByCedula(String cedula);
    boolean existsByEmail(String email);



}
