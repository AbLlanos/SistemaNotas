package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.entity.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class NotasSpecification {

    public static Specification<Notas> filtrarPorCampos(
            String nombrePeriodo,
            String nombreCurso,
            String nombreMateria,
            String cedula,
            String nombreTrimestre,
            String nivelFiltro) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nombrePeriodo != null && !nombrePeriodo.isBlank()) {
                predicates.add(cb.equal(root.get("periodoAcademico").get("nombre"), nombrePeriodo));
            }
            if (nombreCurso != null && !nombreCurso.isBlank()) {
                predicates.add(cb.equal(root.get("curso").get("nombre"), nombreCurso));
            }
            if (nombreMateria != null && !nombreMateria.isBlank()) {
                predicates.add(cb.equal(root.get("materia").get("nombre"), nombreMateria));
            }
            if (cedula != null && !cedula.isBlank()) {
                predicates.add(cb.equal(root.get("estudiante").get("cedula"), cedula));
            }
            if (nombreTrimestre != null && !nombreTrimestre.isBlank()) {
                predicates.add(cb.equal(root.get("trimestre").get("nombre"), nombreTrimestre));
            }
            if (nivelFiltro != null && !nivelFiltro.isBlank()) {
                predicates.add(cb.equal(
                        cb.lower(cb.function("REPLACE", String.class,
                                root.get("estudiante").get("nivelEducativo").get("nombre"),
                                cb.literal(" "), cb.literal(""))),
                        nivelFiltro.toLowerCase()
                ));
            }

            // ** Filtro para que solo considere periodos activos **
            predicates.add(cb.isTrue(root.get("periodoAcademico").get("visible")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
