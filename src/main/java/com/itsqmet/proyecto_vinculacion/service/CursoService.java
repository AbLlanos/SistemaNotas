package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.entity.Curso;
import com.itsqmet.proyecto_vinculacion.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CursoService {

    //APROBADO

    @Autowired
    private CursoRepository cursoRepository;

    /* 1. Mostrar todos */
    public List<Curso> listarTodosCursos() {
        return cursoRepository.findAll();
    }

    // 2. Guardar
    public Curso guardarCurso(Curso curso) {
        return cursoRepository.save(curso);
    }

    // 3. Eliminar por ID
    public void eliminarCurso(Long id) {
        cursoRepository.deleteById(id);
    }

    // 4. Buscar ID
    public Optional<Curso> buscarCursoPorId(Long id) {
        return cursoRepository.findById(id);
    }

    // 5. Consultas adicionales

    public List<Curso> findByNivelEducativoNombre(String nombreNivel) {
        return cursoRepository.findByNivelEducativo_Nombre(nombreNivel);
    }

    public List<Curso> filtrarCursos(String nombre, Long nivelId) {
        if (nombre != null && !nombre.isEmpty() && nivelId != null) {
            return cursoRepository.findByNombreContainingIgnoreCaseAndNivelEducativo_Id(nombre, nivelId);
        } else if (nombre != null && !nombre.isEmpty()) {
            return cursoRepository.findByNombreContainingIgnoreCase(nombre);
        } else if (nivelId != null) {
            return cursoRepository.findByNivelEducativo_Id(nivelId);
        } else {
            return cursoRepository.findAll();
        }
    }



}
