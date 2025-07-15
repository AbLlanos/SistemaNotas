package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.entity.Curso;
import com.itsqmet.proyecto_vinculacion.repository.CursoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CursoService {
    @Autowired
    private CursoRepository cursoRepository;

    // Mostrar todos los cursos
    public List<Curso> mostrarCursos() {
        return cursoRepository.findAll();
    }

    // Buscar por ID
    public Optional<Curso> buscarCursoPorId(Long id) {
        return cursoRepository.findById(id);
    }

    // Guardar curso
    public Curso guardarCurso(Curso curso) {
        return cursoRepository.save(curso);
    }

    // Eliminar curso por ID
    public void eliminarCurso(Long id) {
        cursoRepository.deleteById(id);
    }

    // Obtener número total de cursos
    public int obtenerNumeroCursos() {
        return cursoRepository.findAll().size();
    }

    // Obtener curso con estudiantes (transaccional)
    @Transactional
    public Curso obtenerCursoConEstudiantes(Long id) {
        return cursoRepository.findById(id).orElseThrow();
    }

    // Obtener curso con materias (transaccional)
    @Transactional
    public Curso obtenerCursoConMaterias(Long id) {
        return cursoRepository.findById(id).orElseThrow();
    }

    public List<Curso> findByNivelEducativoNombre(String nombreNivel) {
        return cursoRepository.findByNivelEducativo_Nombre(nombreNivel);
    }
}
