package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.entity.Curso;
import com.itsqmet.proyecto_vinculacion.entity.Materia;
import com.itsqmet.proyecto_vinculacion.entity.Notas;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class NotasSpecification {

    public static Specification<Notas> filtrarPorCampos(
            String nombrePeriodo,
            String nombreCurso,
            String nombreMateria,
            String cedula,
            String nombreTrimestre) {

        return (Root<Notas> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            query.distinct(true);

            Predicate predicate = cb.conjunction();

            Join<Notas, Materia> joinMateria = root.join("materia");
            Join<Materia, Curso> joinCurso = joinMateria.join("cursos");

            if (nombreCurso != null && !nombreCurso.isBlank()) {
                predicate = cb.and(predicate,
                        cb.equal(cb.lower(joinCurso.get("nombre")), nombreCurso.trim().toLowerCase()));
            }

            if (nombreMateria != null && !nombreMateria.isBlank()) {
                predicate = cb.and(predicate,
                        cb.equal(cb.lower(joinMateria.get("nombre")), nombreMateria.trim().toLowerCase()));
            }

            if (cedula != null && !cedula.isBlank()) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("estudiante").get("cedula"), cedula.trim()));
            }

            if (nombreTrimestre != null && !nombreTrimestre.isBlank()) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("trimestre").get("nombre"), nombreTrimestre.trim()));
            }

            if (nombrePeriodo != null && !nombrePeriodo.isBlank()) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("periodoAcademico").get("nombre"), nombrePeriodo.trim()));
            }

            return predicate;
        };
    }
}
