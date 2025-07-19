package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.entity.Materia;
import com.itsqmet.proyecto_vinculacion.repository.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar las operaciones con la entidad Materia.
 * Contiene métodos de negocio que delegan en el repositorio.
 */
@Service
public class MateriaService {

    // Repositorio de Materia para acceso a la base de datos.
    @Autowired
    private MateriaRepository materiaRepository;

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
    public Optional<Materia> buscarPeriodoPorId(Long id) {
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
}
