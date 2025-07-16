package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.entity.PeriodoAcademico;
import com.itsqmet.proyecto_vinculacion.repository.PeriodoAcademicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PeriodoAcademicoService {
    @Autowired
    private PeriodoAcademicoRepository periodoAcademicoRepository;

    public List<PeriodoAcademico> listarTodos() {
        return periodoAcademicoRepository.findAll();
    }

    public PeriodoAcademico buscarPorNombre(String nombre) {
        return periodoAcademicoRepository.findByNombre(nombre).orElse(null);
    }
}
