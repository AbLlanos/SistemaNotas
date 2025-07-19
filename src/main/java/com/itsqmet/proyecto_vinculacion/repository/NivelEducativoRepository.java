package com.itsqmet.proyecto_vinculacion.repository;

import com.itsqmet.proyecto_vinculacion.entity.NivelEducativo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NivelEducativoRepository extends JpaRepository<NivelEducativo, Long> {

    List<NivelEducativo> findByNombreContainingIgnoreCase(Long id);

}