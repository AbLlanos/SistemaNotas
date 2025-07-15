package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.entity.Trimestre;
import com.itsqmet.proyecto_vinculacion.repository.TrimestreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrimestreService {

    @Autowired
    private TrimestreRepository trimestreRepository;

    public List<Trimestre> obtenerTodos() {
        return trimestreRepository.findAll();
    }

    public Trimestre guardar(Trimestre trimestre) {
        return trimestreRepository.save(trimestre);
    }

    public Trimestre buscarPorId(Long id) {
        return trimestreRepository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        trimestreRepository.deleteById(id);
    }

    public Trimestre buscarPorNombre(String nombre) {
        return trimestreRepository.findByNombre(nombre).orElse(null);
    }
}
