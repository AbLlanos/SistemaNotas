package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.entity.Curso;
import com.itsqmet.proyecto_vinculacion.entity.Estudiante;
import com.itsqmet.proyecto_vinculacion.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstudianteService {

    //APROBADO

    @Autowired
    private EstudianteRepository estudianteRepository;

    // 1. Mostrar
    public List<Estudiante> listarTodosEstudiantes() {
        return estudianteRepository.findAll();
    }

    // 2. Guardar
    public Estudiante guardarEstudiante(Estudiante estudiante) {
        return estudianteRepository.save(estudiante);
    }

    // 3. Eliminar por ID
    public void eliminarEstudiante(Long id) {
        estudianteRepository.deleteById(id);
    }

    // 4. Buscar por ID
    public Optional<Estudiante> buscarEstudiantePorId(Long id) {
        return estudianteRepository.findById(id);
    }

    // 5. Consultas adicionales

    public Estudiante buscarPorCedula(String cedula) {
        return estudianteRepository.findByCedula(cedula).orElse(null);
    }



}
