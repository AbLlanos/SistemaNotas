package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.entity.Estudiante;
import com.itsqmet.proyecto_vinculacion.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstudianteService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    public Estudiante buscarPorCedula(String cedula) {
        return estudianteRepository.findByCedula(cedula).orElse(null);
    }

    public List<Estudiante> obtenerTodos() {
        return estudianteRepository.findAll();
    }



}
