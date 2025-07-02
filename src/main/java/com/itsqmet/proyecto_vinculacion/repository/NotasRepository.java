package com.itsqmet.proyecto_vinculacion.repository;

import com.itsqmet.proyecto_vinculacion.entity.Notas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotasRepository extends JpaRepository<Notas, Long> {

    /*
        Orden de filtros:
        1. PeriodoAcademico.nombre (en lugar de AnioLectivo)
        2. Curso.nombre
        3. Materia.nombre
        4. Trimestre.nombre
        5. Estudiante.cedula
    */

        List<Notas> findByPeriodoAcademico_Nombre(String nombrePeriodo);

        List<Notas> findByMateria_Curso_Nombre(String nombreCurso);

        List<Notas> findByMateria_Nombre(String nombreMateria);

        List<Notas> findByTrimestre_Nombre(String nombreTrimestre);

        List<Notas> findByEstudiante_Cedula(String cedula);

        List<Notas> findByPeriodoAcademico_NombreAndMateria_Curso_Nombre(String nombrePeriodo, String nombreCurso);

        List<Notas> findByPeriodoAcademico_NombreAndMateria_Curso_NombreAndMateria_Nombre(String nombrePeriodo, String nombreCurso, String nombreMateria);

        List<Notas> findByPeriodoAcademico_NombreAndMateria_Curso_NombreAndMateria_NombreAndTrimestre_Nombre(String nombrePeriodo, String nombreCurso, String nombreMateria, String nombreTrimestre);

        List<Notas> findByPeriodoAcademico_NombreAndMateria_Curso_NombreAndMateria_NombreAndTrimestre_NombreAndEstudiante_Cedula(String nombrePeriodo, String nombreCurso, String nombreMateria, String nombreTrimestre, String cedula);

        List<Notas> findByMateria_Curso_NombreAndMateria_Nombre(String nombreCurso, String nombreMateria);
}
