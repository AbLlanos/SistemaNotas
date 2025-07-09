package com.itsqmet.proyecto_vinculacion.repository;

import com.itsqmet.proyecto_vinculacion.entity.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocenteRepository extends JpaRepository <Docente, Long> {
   Optional <Docente> findByCedula(String cedula);
    List <Docente> findByNombreContainingIgnoreCase(String nombre);
    Optional <Docente> findById(Long id); //buscar por id

}
