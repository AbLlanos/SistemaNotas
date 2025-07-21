package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.entity.Curso;
import com.itsqmet.proyecto_vinculacion.entity.Estudiante;
import com.itsqmet.proyecto_vinculacion.entity.NivelEducativo;
import com.itsqmet.proyecto_vinculacion.repository.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio para Estudiantes.
 * Soporta búsqueda, guardado, filtros y asignación de relaciones.
 */
@Service
public class EstudianteService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private NivelEducativoService nivelEducativoService;

    @Autowired
    private CursoService cursoService;

    /* ===================== CRUD ===================== */

    /** Listar todos. */
    public List<Estudiante> listarTodosEstudiantes() {
        return estudianteRepository.findAll();
    }

    /** Guardar/actualizar entidad completa ya armada (con cursos y nivel seteados). */
    public Estudiante guardarEstudiante(Estudiante estudiante) {
        return estudianteRepository.save(estudiante);
    }

    /** Eliminar por ID. */
    public void eliminarEstudiante(Long id) {
        estudianteRepository.deleteById(id);
    }

    /** Buscar por ID. */
    public Optional<Estudiante> buscarEstudiantePorId(Long id) {
        return estudianteRepository.findById(id);
    }

    /* ===================== BÚSQUEDAS ===================== */

    /** Buscar (exacto) por cédula. */
    public Estudiante buscarPorCedula(String cedula) {
        return estudianteRepository.findByCedula(cedula).orElse(null);
    }

    /** Filtro "contiene" por cédula. */
    public List<Estudiante> buscarPorCedulaFiltro(String cedula) {
        return estudianteRepository.findByCedulaContainingIgnoreCase(cedula);
    }

    /** Filtro "contiene" por nombre. */
    public List<Estudiante> buscarPorNombre(String nombre) {
        return estudianteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    /** Filtros combinados nombre + cédula. */
    public List<Estudiante> buscarPorNombreYCedula(String nombre, String cedula) {
        return estudianteRepository.findByNombreContainingIgnoreCaseAndCedulaContainingIgnoreCase(nombre, cedula);
    }

    /** Obtener lista por IDs. */
    public List<Estudiante> obtenerPorIds(List<Long> ids) {
        return estudianteRepository.findAllById(ids);
    }

    /** Guardar lote. */
    public void guardarTodos(List<Estudiante> estudiantes) {
        estudianteRepository.saveAll(estudiantes);
    }

    /* ===================== UTIL RELACIONES ===================== */

    /**
     * Auxiliar: asigna nivel y cursos a un estudiante (carga entidades por ID) y guarda.
     * Útil si en algún punto quieres manejarlo todo desde servicio sin armar entidad en el controller.
     */
    public Estudiante guardarConNivelYCursos(Estudiante estudiante, Long nivelId, List<Long> cursosIds) {

        // Nivel
        NivelEducativo nivel = null;
        if (nivelId != null) {
            nivel = nivelEducativoService.buscarPorId(nivelId).orElse(null);
        }
        estudiante.setNivelEducativo(nivel);

        // Cursos
        List<Curso> cursos = (cursosIds == null || cursosIds.isEmpty())
                ? Collections.emptyList()
                : cursoService.obtenerCursosPorIds(cursosIds);
        estudiante.setCursos(cursos);

        return guardarEstudiante(estudiante);
    }


    // ==================== NUEVO ====================
    /** Estudiantes por nivel educativo (para filtrar en formulario de Curso). */
    public List<Estudiante> listarPorNivelId(Long nivelId) {
        if (nivelId == null) {
            return listarTodosEstudiantes();
        }
        return estudianteRepository.findByNivelEducativo_Id(nivelId);
    }


    public List<Estudiante> obtenerEstudiantesPorIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return estudianteRepository.findAllById(ids);
    }


    //OCULTAR ESTUDAINTES EN CASO DE SER NECESESARIO

        /* =========================
       VISIBLES (para listados)
       ========================= */

    public List<Estudiante> listarVisibles() {
        return estudianteRepository.findAll()
                .stream()
                .filter(e -> Boolean.TRUE.equals(e.getVisible()))
                .toList();
    }

    public List<Estudiante> buscarVisiblesPorNombre(String nombre) {
        return estudianteRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .filter(e -> Boolean.TRUE.equals(e.getVisible()))
                .toList();
    }

    public List<Estudiante> buscarVisiblesPorCedulaFiltro(String cedula) {
        return estudianteRepository.findByCedulaContainingIgnoreCase(cedula).stream()
                .filter(e -> Boolean.TRUE.equals(e.getVisible()))
                .toList();
    }

    public List<Estudiante> buscarVisiblesPorNombreYCedula(String nombre, String cedula) {
        return estudianteRepository
                .findByNombreContainingIgnoreCaseAndCedulaContainingIgnoreCase(nombre, cedula).stream()
                .filter(e -> Boolean.TRUE.equals(e.getVisible()))
                .toList();
    }


    public List<Estudiante> listarVisiblesPorNivelId(Long nivelId) {
        return estudianteRepository.findByVisibleTrueAndNivelEducativo_Id(nivelId);
    }

    public List<Estudiante> listarVisiblesPorNivelEducativoNombre(String nombreNivel) {
        return estudianteRepository.findByVisibleTrueAndNivelEducativoNombre(nombreNivel);
    }



}
