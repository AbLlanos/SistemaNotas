package com.itsqmet.proyecto_vinculacion.repository;

import com.itsqmet.proyecto_vinculacion.entity.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocenteRepository extends JpaRepository <Docente, Long> {

    Optional <Docente> findById(Long id);
    List <Docente> findByNombreContainingIgnoreCase(String nombre);
    List<Docente> findByCedulaContainingIgnoreCase(String cedula);

    List<Docente> findByNombreContainingIgnoreCaseAndCedulaContainingIgnoreCase(String nombre, String cedula);

    Optional<Docente> findByEmail(String email);
    Optional<Docente> findByCedula(String cedula);
}
