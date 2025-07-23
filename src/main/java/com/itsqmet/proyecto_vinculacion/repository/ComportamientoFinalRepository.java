package com.itsqmet.proyecto_vinculacion.repository;

import com.itsqmet.proyecto_vinculacion.entity.ComportamientoFinal;
import com.itsqmet.proyecto_vinculacion.entity.Curso;
import com.itsqmet.proyecto_vinculacion.entity.Estudiante;
import com.itsqmet.proyecto_vinculacion.entity.PeriodoAcademico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComportamientoFinalRepository extends JpaRepository<ComportamientoFinal, Long> {
    Optional<ComportamientoFinal> findByEstudianteAndCursoAndPeriodo(Estudiante estudiante, Curso curso, PeriodoAcademico periodo);
    Optional<ComportamientoFinal> findByEstudianteIdAndCursoIdAndPeriodoId(Long estudianteId, Long cursoId, Long periodoId);

}



