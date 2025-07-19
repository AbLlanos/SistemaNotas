package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.entity.Trimestre;
import com.itsqmet.proyecto_vinculacion.repository.TrimestreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrimestreService {

    //Aprobado

    @Autowired
    private TrimestreRepository trimestreRepository;


    // 1. Mostrar todos
    public List<Trimestre> listarTodosPeriodos() {
        return trimestreRepository.findAll();
    }

    // 2. Guardar
    public Trimestre guardarTrimestre(Trimestre trimestre) {
        return trimestreRepository.save(trimestre);
    }

    // 3. Eliminar por ID
    public Trimestre buscarTrimestrePorId(Long id) {
        return trimestreRepository.findById(id).orElse(null);
    }

    // 4. Buscar por ID
    public void eliminar(Long id) {
        trimestreRepository.deleteById(id);
    }

    // 5. Consultas adicionales

    public List<Trimestre> buscarPorNombreContiene(String nombre) {
        return trimestreRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public Trimestre buscarPorNombre(String nombre) {
        return trimestreRepository.findByNombre(nombre).orElse(null);
    }
}
