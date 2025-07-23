package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.entity.Curso;
import com.itsqmet.proyecto_vinculacion.entity.PeriodoAcademico;
import com.itsqmet.proyecto_vinculacion.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de Cursos.
 */
@Service
public class CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    /* ===================== CRUD ===================== */

    /** Listar todos. */
    public List<Curso> listarTodosCursos() {
        return cursoRepository.findAll();
    }

    /** Guardar/actualizar. */
    public Curso guardarCurso(Curso curso) {
        return cursoRepository.save(curso);
    }

    /** Guardar lote. */
    public void guardarCursos(List<Curso> cursos) {
        cursoRepository.saveAll(cursos);
    }

    /** Eliminar por ID. */
    public void eliminarCurso(Long id) {
        cursoRepository.deleteById(id);
    }

    /** Buscar por ID. */
    public Optional<Curso> buscarCursoPorId(Long id) {
        return cursoRepository.findById(id);
    }

    /** Obtener cursos por lista de IDs. */
    public List<Curso> obtenerCursosPorIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return cursoRepository.findAllById(ids);
    }

    /* ===================== FILTROS ===================== */

    /** Por nombre de nivel educativo. */
    public List<Curso> findByNivelEducativoNombre(String nombreNivel) {
        // Si deseas mantener compatibilidad y no tienes método repo directo:
        // return cursoRepository.findByNivelEducativo_Nombre(nombreNivel);
        return cursoRepository.findByNivelEducativo_Nombre(nombreNivel);
    }

    /** Por ID de nivel educativo (Útil para filtrar cursos en formulario de Estudiante). */
    public List<Curso> listarPorNivelId(Long nivelId) {
        if (nivelId == null) return listarTodosCursos();
        return cursoRepository.findByNivelEducativo_Id(nivelId);
    }

    /** NUEVO: por periodo. */
    public List<Curso> listarPorPeriodoId(Long periodoId) {
        if (periodoId == null) return listarTodosCursos();
        return cursoRepository.findByPeriodoAcademico_Id(periodoId);
    }

    /** NUEVO: filtro compuesto periodo + nivel + nombre opcional. */
    public List<Curso> filtrarCursosAvanzado(String nombre, Long periodoId, Long nivelId) {
        if (periodoId != null && nivelId != null) {
            // no hay método con nombre+periodo+nivel; combina manual
            return cursoRepository
                    .findByPeriodoAcademico_IdAndNivelEducativo_Id(periodoId, nivelId)
                    .stream()
                    .filter(c -> nombre == null || nombre.isBlank() ||
                            c.getNombre() != null && c.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                    .toList();
        }
        // fallback al filtro existente
        return filtrarCursos(nombre, nivelId);
    }

    /**
     * Filtro compuesto opcional: nombre y nivelId.
     * Si ambos null, regresa todo.
     */
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

    public List<Curso> listarCursosBachillerato() {
        return cursoRepository.findByNivelEducativo_NombreIgnoreCase("Bachillerato General");
    }

    public Curso getCursoOrThrow(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado id=" + id));
    }

    public Curso getCursoByNombreOrThrow(String nombreCurso) {
        return cursoRepository.findByNombreIgnoreCase(nombreCurso)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado nombre=" + nombreCurso));
    }

    // CursoService.java
    public List<Curso> obtenerCursosPorPeriodo(String nombrePeriodo) {
        return cursoRepository.findByPeriodoAcademicoNombre(nombrePeriodo);
    }

    public List<Curso> obtenerCursosPorPeriodoID(Long periodoAcademicoId) {
        return cursoRepository.findByPeriodoAcademico_Id(periodoAcademicoId);
    }


    public Curso buscarPorNombre(String nombre) {
        return cursoRepository.findByNombre(nombre)
                .orElse(null);
    }

    public Curso buscarCursoPorPeriodoAndNombre(PeriodoAcademico periodo, String nombre) {
        return cursoRepository.findByPeriodoAcademicoAndNombre(periodo, nombre)
                .orElse(null);
    }

    public List<Curso> obtenerCursosPorPeriodoVisible(String nombrePeriodo) {
        return cursoRepository.findByPeriodoAcademicoNombreAndPeriodoAcademicoVisibleTrue(nombrePeriodo);
    }

}
