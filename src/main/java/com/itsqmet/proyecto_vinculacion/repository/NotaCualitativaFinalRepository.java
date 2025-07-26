package com.itsqmet.proyecto_vinculacion.repository;

import com.itsqmet.proyecto_vinculacion.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotaCualitativaFinalRepository extends JpaRepository<NotaCualitativaFinal, Long> {
    Optional<NotaCualitativaFinal> findByEstudianteAndCursoAndPeriodo(Estudiante estudiante, Curso curso, PeriodoAcademico periodo);

}