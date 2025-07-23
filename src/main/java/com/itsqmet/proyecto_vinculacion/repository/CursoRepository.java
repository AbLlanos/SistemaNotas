package com.itsqmet.proyecto_vinculacion.repository;

import com.itsqmet.proyecto_vinculacion.entity.Curso;
import com.itsqmet.proyecto_vinculacion.entity.Docente;
import com.itsqmet.proyecto_vinculacion.entity.NivelEducativo;
import com.itsqmet.proyecto_vinculacion.entity.PeriodoAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    // Obtiene todos los cursos (de donde luego se extraen años únicos en el servicio)
    List<Curso> findAll();

    List<Curso> findByNivelEducativo_Nombre(String nombreNivel);

    List<Curso> findByNombreContainingIgnoreCaseAndNivelEducativo_Id(String nombre, Long nivelId);

    List<Curso> findByNivelEducativo_Id(Long nivelId);

    List<Curso> findByNombreContainingIgnoreCase(String nombre);

    // NUEVO: por periodo
    List<Curso> findByPeriodoAcademico_Id(Long periodoId);

    // NUEVO combinado
    List<Curso> findByPeriodoAcademico_IdAndNivelEducativo_Id(Long periodoId, Long nivelId);

    List<Curso> findByNivelEducativo_NombreIgnoreCase(String nombreNivel);

    Optional<Curso> findByNombreIgnoreCase(String nombre);


    List<Curso> findByPeriodoAcademicoNombre(String nombrePeriodo);

    Optional<Curso> findByNombre(String nombre);

    Optional<Curso> findByPeriodoAcademicoAndNombre(PeriodoAcademico periodo, String nombre);

}