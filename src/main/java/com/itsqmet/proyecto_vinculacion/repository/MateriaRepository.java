package com.itsqmet.proyecto_vinculacion.repository;

import com.itsqmet.proyecto_vinculacion.entity.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MateriaRepository extends JpaRepository<Materia, Long> {

    Optional<Materia> findByNombre(String nombre);

    List<Materia> findByCursosNivelEducativoNombre(String nombreNivel);

    Optional<Materia> findById(Long id);

    List<Materia> findByCursosNombre(String nombreCurso);

    // Todas las materias de un nivel educativo por ID de nivel
    List<Materia> findByNivelEducativo_Id(Long id);

    List<Materia> findByVisibleTrue();

    List<Materia> findByVisibleTrueAndPeriodoAcademico_VisibleTrue();
    List<Materia> findByPeriodoAcademicoId(Long periodoId);

}
