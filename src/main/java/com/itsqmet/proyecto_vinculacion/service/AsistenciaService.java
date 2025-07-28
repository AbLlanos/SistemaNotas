package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.entity.Asistencia;
import com.itsqmet.proyecto_vinculacion.repository.AsistenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AsistenciaService {

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    public Asistencia obtenerAsistencia(Long estudianteId, Long materiaId, Long trimestreId, Long periodoId) {
        return asistenciaRepository.findByEstudianteIdAndMateriaIdAndTrimestreIdAndPeriodoId(
                estudianteId, materiaId, trimestreId, periodoId
        ).orElse(null);
    }

}
