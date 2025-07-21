package com.itsqmet.proyecto_vinculacion.repository;

import com.itsqmet.proyecto_vinculacion.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface NotasRepository extends JpaRepository<Notas, Long>, JpaSpecificationExecutor<Notas>  {

    @Query("SELECT n FROM Notas n JOIN n.materia m JOIN m.cursos c WHERE c.nivelEducativo.nombre = :nombreNivel")
    List<Notas> findByNivelEducativoNombreFromCursos(String nombreNivel);

    Optional<Notas> findByEstudianteAndMateriaAndPeriodoAcademicoAndTrimestre(Estudiante estudiante, Materia materia,
                                                                              PeriodoAcademico periodoAcademico, Trimestre trimestre);

    List<Notas> findByEstudianteAndPeriodoAcademicoNombre(Estudiante estudiante, String nombrePeriodo);
}

