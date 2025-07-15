package com.itsqmet.proyecto_vinculacion.repository;

import com.itsqmet.proyecto_vinculacion.entity.Materia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MateriaRepository extends JpaRepository<Materia, Long> {
    Optional<Materia> findByNombre(String nombre);
    List<Materia> findByCurso_NivelEducativo_Nombre(String nombreNivel);

}