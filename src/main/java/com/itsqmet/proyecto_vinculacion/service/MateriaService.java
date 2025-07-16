package com.itsqmet.proyecto_vinculacion.service;


import com.itsqmet.proyecto_vinculacion.entity.Docente;
import com.itsqmet.proyecto_vinculacion.entity.Materia;
import com.itsqmet.proyecto_vinculacion.repository.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MateriaService {
    @Autowired
    private MateriaRepository materiaRepository;

    public List<Materia> obtenerTodasLasMaterias() {
        return materiaRepository.findAll();
    }

    public List<Materia> findByCursoNivelEducativoNombre(String nombreNivel) {
        return materiaRepository.findByNombreNivelEducativo(nombreNivel);
    }

    public Materia buscarPorNombre(String nombre) {
        return materiaRepository.findByNombre(nombre).orElse(null);
    }

    public List<Materia> obtenerMateriasPorIds(List<Long> ids) {
        return materiaRepository.findAllById(ids);
    }

    public List<Materia> buscarPorNombreCurso(String nombreCurso) {
        return materiaRepository.findByCursosNombre(nombreCurso);
    }


}