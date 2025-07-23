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



    List<Materia> findByNombreContainingIgnoreCase(String nombre);

    List<Materia> findDistinctByCursosNombre(String nombreCurso);

    List<Materia> findByCursosNombre(String nombreCurso);

    // Todas las materias de un nivel educativo por nombre de nivel
    List<Materia> findByNivelEducativo_Nombre(String nombre);

    // Todas las materias de un nivel educativo por ID de nivel
    List<Materia> findByNivelEducativo_Id(Long id);

    @Query("SELECT m FROM Materia m JOIN m.cursos c WHERE c.id = :cursoId")
    List<Materia> findMateriasByCursoId(Long cursoId);


    List<Materia> findDistinctByCursos_NombreIgnoreCase(String nombreCurso);
}
