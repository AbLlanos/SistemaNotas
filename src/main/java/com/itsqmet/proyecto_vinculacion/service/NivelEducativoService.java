package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.entity.NivelEducativo;
import com.itsqmet.proyecto_vinculacion.repository.NivelEducativoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para Niveles Educativos.
 */
@Service
public class NivelEducativoService {

    @Autowired
    private NivelEducativoRepository nivelEducativoRepository;

    /** Listar todos. */
    public List<NivelEducativo> listarTodos() {
        return nivelEducativoRepository.findAll();
    }

    /** Buscar por ID (nombre corto usado por controladores nuevos). */
    public Optional<NivelEducativo> buscarPorId(Long id) {
        return nivelEducativoRepository.findById(id);
    }

    /** Alias conservando tu método previo (por compatibilidad). */
    public Optional<NivelEducativo> buscarNivelPorId(Long id) {
        return buscarPorId(id);
    }

    public NivelEducativo buscarOptionalPorId(Long id) {
        return nivelEducativoRepository.findById(id).orElse(null);
    }
}
