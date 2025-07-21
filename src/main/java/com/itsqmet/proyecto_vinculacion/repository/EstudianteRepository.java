package com.itsqmet.proyecto_vinculacion.repository;

import com.itsqmet.proyecto_vinculacion.entity.Docente;
import com.itsqmet.proyecto_vinculacion.entity.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    Optional<Estudiante> findByCedula(String cedula);

    List <Estudiante> findByNombreContainingIgnoreCase(String nombre);
    List<Estudiante> findByCedulaContainingIgnoreCase(String cedula);

    List<Estudiante> findByNombreContainingIgnoreCaseAndCedulaContainingIgnoreCase(String nombre, String cedula);

    // NUEVO: por nivel educativo
    List<Estudiante> findByNivelEducativo_Id(Long nivelId);



    // nuevos (visibles)
    List<Estudiante> findByVisibleTrueAndNivelEducativo_Id(Long nivelId);
    List<Estudiante> findByVisibleTrue();
    List<Estudiante> findByVisibleTrueAndNombreContainingIgnoreCase(String nombre);
    List<Estudiante> findByVisibleTrueAndCedulaContainingIgnoreCase(String cedula);
    List<Estudiante> findByVisibleTrueAndNombreContainingIgnoreCaseAndCedulaContainingIgnoreCase(String nombre, String cedula);


    List<Estudiante> findByVisibleTrueAndNivelEducativoNombre(String nombreNivel);


}
