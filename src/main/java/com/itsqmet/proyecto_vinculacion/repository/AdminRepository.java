package com.itsqmet.proyecto_vinculacion.repository;

import com.itsqmet.proyecto_vinculacion.entity.Admin;
import com.itsqmet.proyecto_vinculacion.entity.Curso;
import com.itsqmet.proyecto_vinculacion.entity.Estudiante;
import com.itsqmet.proyecto_vinculacion.entity.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    List<Admin> findByNombreContainingIgnoreCaseAndCedulaContainingIgnoreCase(String nombre, String cedula);


}
