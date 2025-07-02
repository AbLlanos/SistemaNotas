package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.entity.Notas;
import com.itsqmet.proyecto_vinculacion.repository.NotasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class NotasService {

    @Autowired
    private NotasRepository notasRepository;

    public List<Notas> listarTodasLasNotas() {
        return notasRepository.findAll();
    }

    public Notas guardarNota(Notas nota) {
        return notasRepository.save(nota);
    }

    public Notas buscarNotaPorId(Long id) {
        return notasRepository.findById(id).orElse(null);
    }

    public void eliminarNota(Long id) {
        notasRepository.deleteById(id);
    }

    public List<Notas> buscarNotasPorFiltros(String nombrePeriodo,
                                             String nombreCurso,
                                             String nombreMateria,
                                             String cedula,
                                             String nombreTrimestre) {

        // Normalizar los valores para evitar problemas con null y espacios
        nombrePeriodo = (nombrePeriodo != null && !nombrePeriodo.trim().isEmpty()) ? nombrePeriodo.trim() : null;
        nombreCurso = (nombreCurso != null && !nombreCurso.trim().isEmpty()) ? nombreCurso.trim() : null;
        nombreMateria = (nombreMateria != null && !nombreMateria.trim().isEmpty()) ? nombreMateria.trim() : null;
        cedula = (cedula != null && !cedula.trim().isEmpty()) ? cedula.trim() : null;
        nombreTrimestre = (nombreTrimestre != null && !nombreTrimestre.trim().isEmpty()) ? nombreTrimestre.trim() : null;

        // Caso sin filtros → devolver todo
        if (nombrePeriodo == null && nombreCurso == null && nombreMateria == null && cedula == null && nombreTrimestre == null) {
            return notasRepository.findAll();
        }

        // Construcción condicional según filtros aplicados

        if (nombrePeriodo != null && nombreCurso == null && nombreMateria == null && cedula == null && nombreTrimestre == null) {
            return notasRepository.findByPeriodoAcademico_Nombre(nombrePeriodo);
        }

        if (nombreCurso != null && nombrePeriodo == null && nombreMateria == null && cedula == null && nombreTrimestre == null) {
            return notasRepository.findByMateria_Curso_Nombre(nombreCurso);
        }

        if (nombreMateria != null && nombrePeriodo == null && nombreCurso == null && cedula == null && nombreTrimestre == null) {
            return notasRepository.findByMateria_Nombre(nombreMateria);
        }

        if (cedula != null && nombrePeriodo == null && nombreCurso == null && nombreMateria == null && nombreTrimestre == null) {
            return notasRepository.findByEstudiante_Cedula(cedula);
        }

        if (nombreTrimestre != null && nombrePeriodo == null && nombreCurso == null && nombreMateria == null && cedula == null) {
            return notasRepository.findByTrimestre_Nombre(nombreTrimestre);
        }

        // Construcción de consultas para combinaciones de filtros, ordenadas por cantidad de filtros activos

        if (nombrePeriodo != null && nombreCurso != null && nombreMateria != null && nombreTrimestre != null && cedula != null) {
            return notasRepository.findByPeriodoAcademico_NombreAndMateria_Curso_NombreAndMateria_NombreAndTrimestre_NombreAndEstudiante_Cedula(
                    nombrePeriodo, nombreCurso, nombreMateria, nombreTrimestre, cedula);
        }

        if (nombrePeriodo != null && nombreCurso != null && nombreMateria != null && nombreTrimestre != null) {
            return notasRepository.findByPeriodoAcademico_NombreAndMateria_Curso_NombreAndMateria_NombreAndTrimestre_Nombre(
                    nombrePeriodo, nombreCurso, nombreMateria, nombreTrimestre);
        }

        if (nombrePeriodo != null && nombreCurso != null && nombreMateria != null) {
            return notasRepository.findByPeriodoAcademico_NombreAndMateria_Curso_NombreAndMateria_Nombre(
                    nombrePeriodo, nombreCurso, nombreMateria);
        }

        if (nombrePeriodo != null && nombreCurso != null) {
            return notasRepository.findByPeriodoAcademico_NombreAndMateria_Curso_Nombre(nombrePeriodo, nombreCurso);
        }

        if (nombreCurso != null && nombreMateria != null) {
            return notasRepository.findByMateria_Curso_NombreAndMateria_Nombre(nombreCurso, nombreMateria);
        }

        // Si ninguna combinación coincide,
        return notasRepository.findAll();
    }


}





