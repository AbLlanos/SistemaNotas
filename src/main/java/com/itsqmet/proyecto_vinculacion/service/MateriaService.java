package com.itsqmet.proyecto_vinculacion.service;


import com.itsqmet.proyecto_vinculacion.entity.Materia;
import com.itsqmet.proyecto_vinculacion.repository.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MateriaService {
    @Autowired
    private MateriaRepository materiaRepository;

    public List<Materia> obtenerTodasLasMaterias() {
        return materiaRepository.findAll();
    }

    public List<Materia> findByCursoNivelEducativoNombre(String nombreNivel) {
        return materiaRepository.findByCurso_NivelEducativo_Nombre(nombreNivel);
    }

    public Materia buscarPorNombre(String nombre) {
        return materiaRepository.findByNombre(nombre).orElse(null);
    }
}