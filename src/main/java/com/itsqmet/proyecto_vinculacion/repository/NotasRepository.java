package com.itsqmet.proyecto_vinculacion.repository;

import com.itsqmet.proyecto_vinculacion.entity.Notas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface NotasRepository extends JpaRepository<Notas, Long>, JpaSpecificationExecutor<Notas>  {

    List<Notas> findByMateriaCursoNivelEducativoNombre(String nombreNivel);
}

