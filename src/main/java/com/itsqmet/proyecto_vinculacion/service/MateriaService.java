package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.entity.Curso;
import com.itsqmet.proyecto_vinculacion.entity.Docente;
import com.itsqmet.proyecto_vinculacion.entity.Materia;
import com.itsqmet.proyecto_vinculacion.repository.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar las operaciones con la entidad Materia.
 * Contiene métodos de negocio que delegan en el repositorio.
 */
@Service
public class MateriaService {

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private DocenteService docenteService;

    @Autowired
    private CursoService cursoService;


    // 1. Listar todas las materias.
    public List<Materia> listarTodasMaterias() {
        return materiaRepository.findAll();
    }

    // 2. Guardar una materia nueva o actualizar una existente.
    public Materia guardarMateria(Materia materia) {
        return materiaRepository.save(materia);
    }

    // 3. Eliminar una materia por ID.
    public void eliminarMateria(Long id) {
        materiaRepository.deleteById(id);
    }

    // 4. Buscar una materia por su ID.
    public Optional<Materia> buscarMateriaPorId(Long id) {
        return materiaRepository.findById(id);
    }

    // 5. Buscar todas las materias asociadas a un nivel educativo específico.
    public List<Materia> findByCursoNivelEducativoNombre(String nombreNivel) {
        return materiaRepository.findByCursosNivelEducativoNombre(nombreNivel);
    }

    // 6. Buscar una materia por su nombre.
    public Materia buscarPorNombre(String nombre) {
        return materiaRepository.findByNombre(nombre).orElse(null);
    }

    // 7. Obtener una lista de materias por una lista de IDs.
    public List<Materia> obtenerMateriasPorIds(List<Long> ids) {
        return materiaRepository.findAllById(ids);
    }

    // 8. Buscar todas las materias asociadas a un curso.
    public List<Materia> buscarPorNombreCurso(String nombreCurso) {
        return materiaRepository.findByCursosNombre(nombreCurso);
    }

    // 9. Filtrar materias por nombre, docente y curso.
    public List<Materia> filtrarPorNombreYDocente(String nombre, Long docenteId) {
        List<Materia> materias = materiaRepository.findAll();

        return materias.stream()
                .filter(m -> nombre == null || nombre.isBlank() ||
                        m.getNombre() != null && m.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .filter(m -> docenteId == null ||
                        (m.getDocente() != null && m.getDocente().getId().equals(docenteId)))
                .toList();
    }

    // 10. Buscar materias por nivel educativo (nuevo)
    public List<Materia> findByNivelEducativoNombre(String nombreNivel) {
        return materiaRepository.findByNivelEducativo_Nombre(nombreNivel);
    }

    // ==================== NUEVO ====================
    /** Materias por nivel educativo (para filtrar en formulario de Curso). */
    public List<Materia> listarPorNivelId(Long nivelId) {
        if (nivelId == null) {
            return listarTodasMaterias();
        }
        return materiaRepository.findByNivelEducativo_Id(nivelId);
    }

}