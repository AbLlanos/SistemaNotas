package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.entity.NivelEducativo;
import com.itsqmet.proyecto_vinculacion.repository.NivelEducativoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NivelEducativoService {

    //Aprobado

    @Autowired
    private NivelEducativoRepository nivelEducativoRepository;

    // 1. Mostrar todos
    public List<NivelEducativo> listarTodos() {
        return nivelEducativoRepository.findAll();
    }

}
