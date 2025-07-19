package com.itsqmet.proyecto_vinculacion.repository;

import com.itsqmet.proyecto_vinculacion.entity.Trimestre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrimestreRepository extends JpaRepository<Trimestre, Long> {
    Optional<Trimestre> findByNombre(String nombre);
    List<Trimestre> findByNombreContainingIgnoreCase(String nombre);
}
