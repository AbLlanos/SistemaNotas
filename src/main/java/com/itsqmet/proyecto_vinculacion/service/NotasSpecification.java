package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.entity.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class NotasSpecification {

    public static Specification<Notas> filtrarPorCampos(String nombrePeriodo,
                                                        String nombreCurso,
                                                        String nombreMateria,
                                                        String cedula,
                                                        String nombreTrimestre) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nombrePeriodo != null && !nombrePeriodo.isEmpty()) {
                Join<Notas, PeriodoAcademico> periodoJoin = root.join("periodoAcademico", JoinType.LEFT);
                predicates.add(cb.equal(cb.lower(periodoJoin.get("nombre")), nombrePeriodo.toLowerCase()));
            }

            if (nombreCurso != null && !nombreCurso.isEmpty()) {
                Join<Notas, Materia> materiaJoin = root.join("materia", JoinType.LEFT);
                Join<Materia, Curso> cursoJoin = materiaJoin.join("cursos", JoinType.LEFT);
                predicates.add(cb.equal(cb.lower(cursoJoin.get("nombre")), nombreCurso.toLowerCase()));
            }

            if (nombreMateria != null && !nombreMateria.isEmpty()) {
                Join<Notas, Materia> materiaJoin = root.join("materia", JoinType.LEFT);
                predicates.add(cb.equal(cb.lower(materiaJoin.get("nombre")), nombreMateria.toLowerCase()));
            }

            if (cedula != null && !cedula.isEmpty()) {
                Join<Notas, Estudiante> estudianteJoin = root.join("estudiante", JoinType.LEFT);
                predicates.add(cb.equal(cb.lower(estudianteJoin.get("cedula")), cedula.toLowerCase()));
            }

            if (nombreTrimestre != null && !nombreTrimestre.isEmpty()) {
                Join<Notas, Trimestre> trimestreJoin = root.join("trimestre", JoinType.LEFT);
                predicates.add(cb.equal(cb.lower(trimestreJoin.get("nombre")), nombreTrimestre.toLowerCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
