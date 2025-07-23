package com.itsqmet.proyecto_vinculacion.repository;

import com.itsqmet.proyecto_vinculacion.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComportamientoRepository extends JpaRepository<Comportamiento, Long> {

    List<Comportamiento> findByEstudianteAndTrimestreAndPeriodo(Estudiante estudiante, Trimestre trimestre, PeriodoAcademico periodo);

    Optional<Comportamiento> findByEstudianteAndMateriaAndTrimestreAndPeriodo(
            Estudiante estudiante,
            Materia materia,
            Trimestre trimestre,
            PeriodoAcademico periodo);

    @Query("SELECT c FROM Comportamiento c WHERE c.estudiante = :estudiante AND c.trimestre = :trimestre AND c.periodo = :periodo")
    Optional<Comportamiento> findUniqueByEstudianteAndTrimestreAndPeriodo(
            @Param("estudiante") Estudiante estudiante,
            @Param("trimestre") Trimestre trimestre,
            @Param("periodo") PeriodoAcademico periodo);

    Optional<Comportamiento> findByEstudianteAndMateriaAndPeriodoAndTrimestre(
            Estudiante estudiante,
            Materia materia,
            PeriodoAcademico periodo,
            Trimestre trimestre
    );

}
