
package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.entity.Materia;
import com.itsqmet.proyecto_vinculacion.entity.PeriodoAcademico;
import com.itsqmet.proyecto_vinculacion.repository.MateriaRepository;
import com.itsqmet.proyecto_vinculacion.repository.PeriodoAcademicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PeriodoAcademicoService {

    //Aprobado

    @Autowired
    private PeriodoAcademicoRepository periodoAcademicoRepository;

    // 1. Mostrar todos
    public List<PeriodoAcademico> listarTodosPeriodosAcademicos() {
        return periodoAcademicoRepository.findAll();
    }

    // 2. Guardar
    public PeriodoAcademico guardarPeriodo(PeriodoAcademico periodoAcademico) {
        return periodoAcademicoRepository.save(periodoAcademico);
    }

    // 3. Eliminar
    public void eliminarPeriodo(Long id) {
        periodoAcademicoRepository.deleteById(id);
    }

    // 4. Buscar
    public Optional<PeriodoAcademico> buscarPeriodoPorId(Long id) {
        return periodoAcademicoRepository.findById(id);
    }

    // 5. Consultas adicionales


    public PeriodoAcademico buscarPorNombre(String nombre) {
        return periodoAcademicoRepository.findByNombre(nombre).orElse(null);
    }

    public PeriodoAcademico buscarPorId(Long id) {
        return periodoAcademicoRepository.findById(id).orElse(null);
    }

    public void guardar(PeriodoAcademico periodo) {
        periodoAcademicoRepository.save(periodo);
    }

    public void eliminarPorId(Long id) {
        periodoAcademicoRepository.deleteById(id);
    }


    public List<PeriodoAcademico> listarPeriodosVisibles() {
        return periodoAcademicoRepository.findByVisibleTrue();
    }

    @Autowired
    private MateriaRepository materiaRepository;


    public void ocultarPeriodo(Long periodoId) {
        PeriodoAcademico periodo = periodoAcademicoRepository.findById(periodoId)
                .orElseThrow(() -> new IllegalArgumentException("Periodo no encontrado"));

        periodo.setVisible(false);
        periodoAcademicoRepository.save(periodo);

        // Ocultar todas las materias del periodo
        List<Materia> materias = materiaRepository.findByPeriodoAcademicoId(periodoId);
        materias.forEach(m -> m.setVisible(false));
        materiaRepository.saveAll(materias);
    }


}

