package com.itsqmet.proyecto_vinculacion.repository;

import com.itsqmet.proyecto_vinculacion.entity.Comportamiento;
import com.itsqmet.proyecto_vinculacion.entity.Estudiante;
import com.itsqmet.proyecto_vinculacion.entity.Trimestre;
import com.itsqmet.proyecto_vinculacion.entity.PeriodoAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComportamientoRepository extends JpaRepository<Comportamiento, Long> {

    Optional<Comportamiento> findByEstudianteAndTrimestreAndPeriodo(Estudiante estudiante, Trimestre trimestre, PeriodoAcademico periodo);
}
