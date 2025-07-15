package com.itsqmet.proyecto_vinculacion.repository;

import com.itsqmet.proyecto_vinculacion.entity.Notas;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class NotasSpecification {

    public static Specification<Notas> filtrarPorCampos(
            String nombrePeriodo,
            String nombreCurso,
            String nombreMateria,
            String cedula,
            String nombreTrimestre) {

        return (Root<Notas> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            Predicate predicate = cb.conjunction();

            if (nombrePeriodo != null && !nombrePeriodo.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("periodoAcademico").get("nombre"), nombrePeriodo.trim()));
            }

            if (nombreCurso != null && !nombreCurso.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("materia").get("curso").get("nombre"), nombreCurso.trim()));
            }

            if (nombreMateria != null && !nombreMateria.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("materia").get("nombre"), nombreMateria.trim()));
            }

            if (cedula != null && !cedula.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("estudiante").get("cedula"), cedula.trim()));
            }

            if (nombreTrimestre != null && !nombreTrimestre.isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("trimestre").get("nombre"), nombreTrimestre.trim()));
            }

            return predicate;
        };
    }
}

