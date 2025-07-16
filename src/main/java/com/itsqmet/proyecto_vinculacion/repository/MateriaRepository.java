package com.itsqmet.proyecto_vinculacion.repository;

import com.itsqmet.proyecto_vinculacion.entity.Docente;
import com.itsqmet.proyecto_vinculacion.entity.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MateriaRepository extends JpaRepository<Materia, Long> {
    Optional<Materia> findByNombre(String nombre);

    @Query("SELECT m FROM Materia m JOIN m.cursos c WHERE c.nivelEducativo.nombre = :nombreNivel")
    List<Materia> findByNombreNivelEducativo(@Param("nombreNivel") String nombreNivel);

    @Query("SELECT m FROM Materia m JOIN m.cursos c WHERE c.nivelEducativo.nombre = :nombreNivel")
    List<Materia> buscarPorNombreNivel(@Param("nombreNivel") String nombreNivel);

    Optional <Materia> findById(Long id);

    List<Materia> findByCursosNombre(String nombreCurso);
}