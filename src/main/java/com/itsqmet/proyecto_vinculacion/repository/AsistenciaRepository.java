package com.itsqmet.proyecto_vinculacion.repository;

import com.itsqmet.proyecto_vinculacion.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    Optional<Asistencia> findByEstudianteAndMateriaAndTrimestreAndPeriodo(
            Estudiante estudiante,
            Materia materia,
            Trimestre trimestre,
            PeriodoAcademico periodo);

    Optional<Asistencia> findByEstudianteAndMateriaAndPeriodoAndTrimestre(
            Estudiante estudiante,
            Materia materia,
            PeriodoAcademico periodo,
            Trimestre trimestre
    );


    Optional<Asistencia> findByEstudianteIdAndMateriaIdAndTrimestreIdAndPeriodoId(
            Long estudianteId,
            Long materiaId,
            Long trimestreId,
            Long periodoId
    );

}
