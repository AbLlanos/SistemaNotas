package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.dto.NotaCompletaDTO;
import com.itsqmet.proyecto_vinculacion.entity.*;
import com.itsqmet.proyecto_vinculacion.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotasService {

    //Aprobado
    //Mas importarte

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private ComportamientoRepository comportamientoRepository;

    @Autowired

    private TrimestreService trimestreService;

    @Autowired
    private NotasRepository notasRepository;

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private  MateriaService materiaService;

    @Autowired
    private PeriodoAcademicoService periodoAcademicoService;

    // 1. Mostrar todos
    public List<Notas> listarTodasLasNotas() {
        return notasRepository.findAll();
    }

    // 2. Guardar
    public Notas guardarNota(Notas nota) {
        return notasRepository.save(nota);
    }

    // 3. Buscar por ID
    public Notas buscarNotaPorId(Long id) {
        return notasRepository.findById(id).orElse(null);
    }

    // 4. Eliminar por ID
    public void eliminarNota(Long id) {
        notasRepository.deleteById(id);
    }

    // 5. Consultas adicionales





    /*Filtro universal conectado a especification para facilitar el filtrado dinamico de los todos los campos */

    public List<Notas> buscarNotasPorFiltros(String nombrePeriodo,
                                             String nombreCurso,
                                             String nombreMateria,
                                             String cedula,
                                             String nombreTrimestre) {

        Specification<Notas> spec = NotasSpecification.filtrarPorCampos(
                nombrePeriodo, nombreCurso, nombreMateria, cedula, nombreTrimestre
        );

        return notasRepository.findAll(spec);
    }








    public List<NotaCompletaDTO> obtenerNotasCompletas(String nombrePeriodo,
                                                       String nombreCurso,
                                                       String nombreMateria,
                                                       String cedula,
                                                       String nombreTrimestre) {

        Specification<Notas> spec = NotasSpecification.filtrarPorCampos(
                nombrePeriodo, nombreCurso, nombreMateria, cedula, nombreTrimestre);

        List<Notas> notas = notasRepository.findAll(spec);

        // Mapa clave = "cedulaEstudiante|materia|periodo" para agrupar por estudiante y materia
        Map<String, NotaCompletaDTO> mapaNotas = new LinkedHashMap<>();

        for (Notas n : notas) {
            String key = n.getEstudiante().getCedula() + "|" +
                    n.getMateria().getNombre() + "|" +
                    n.getPeriodoAcademico().getNombre();

            NotaCompletaDTO dto = mapaNotas.computeIfAbsent(key, k -> {
                NotaCompletaDTO nuevoDto = new NotaCompletaDTO();
                nuevoDto.setIdNota(n.getId());

                // Datos del estudiante
                if (n.getEstudiante() != null) {
                    nuevoDto.setCedulaEstudiante(n.getEstudiante().getCedula());
                    nuevoDto.setNombreCompletoEstudiante(
                            n.getEstudiante().getNombre() + " " + n.getEstudiante().getApellido());
                } else {
                    nuevoDto.setCedulaEstudiante("---");
                    nuevoDto.setNombreCompletoEstudiante("---");
                }

                // Materia y curso
                nuevoDto.setAreaMateria(n.getMateria() != null ? n.getMateria().getNombre() : "---");
                String cursosTexto = "---";
                if (n.getMateria() != null && n.getMateria().getCursos() != null && !n.getMateria().getCursos().isEmpty()) {
                    cursosTexto = n.getMateria().getCursos()
                            .stream()
                            .map(Curso::getNombre)
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("---");
                }
                nuevoDto.setNombreCurso(cursosTexto);

                return nuevoDto;
            });

            // --- Asignar notas, asistencia y comportamiento por trimestre ---
            if (n.getTrimestre() != null) {
                String trimestre = n.getTrimestre().getNombre().toLowerCase();

                if (trimestre.contains("primer")) {
                    dto.setNotaNumericaPrimerTrim(n.getNotaNumerica());
                    dto.setNotaCualitativaPrimerTrim(n.getNotaCualitativa());
                    asignarAsistenciaYComportamiento(dto, n, "primer");
                } else if (trimestre.contains("segundo")) {
                    dto.setNotaNumericaSegundoTrim(n.getNotaNumerica());
                    dto.setNotaCualitativaSegundoTrim(n.getNotaCualitativa());
                    asignarAsistenciaYComportamiento(dto, n, "segundo");
                } else if (trimestre.contains("tercer") || trimestre.contains("tercero")) {
                    dto.setNotaNumericaTercerTrim(n.getNotaNumerica());
                    dto.setNotaCualitativaTercerTrim(n.getNotaCualitativa());
                    asignarAsistenciaYComportamiento(dto, n, "tercero");
                }

            }
        }

        return new ArrayList<>(mapaNotas.values());
    }

    /**
     * Método auxiliar para setear asistencia y comportamiento según trimestre
     */
    private void asignarAsistenciaYComportamiento(NotaCompletaDTO dto, Notas n, String trimestreClave) {
        asistenciaRepository.findByEstudianteAndMateriaAndTrimestreAndPeriodo(
                n.getEstudiante(), n.getMateria(), n.getTrimestre(), n.getPeriodoAcademico()
        ).ifPresent(a -> {
            switch (trimestreClave) {
                case "primer" -> {
                    dto.setAsistenciaPrimerTrim(a.getAsistencias());
                    dto.setFaltasJustificadasPrimerTrim(a.getFaltasJustificadas());
                    dto.setFaltasInjustificadasPrimerTrim(a.getFaltasInjustificadas());
                    dto.setAtrasosPrimerTrim(a.getAtrasos());
                    dto.setTotalAsistenciaPrimerTrim(
                            (a.getAsistencias() != null ? a.getAsistencias() : 0) +
                                    (a.getFaltasJustificadas() != null ? a.getFaltasJustificadas() : 0) +
                                    (a.getFaltasInjustificadas() != null ? a.getFaltasInjustificadas() : 0)
                    );
                }
                case "segundo" -> {
                    dto.setAsistenciaSegundoTrim(a.getAsistencias());
                    dto.setFaltasJustificadasSegundoTrim(a.getFaltasJustificadas());
                    dto.setFaltasInjustificadasSegundoTrim(a.getFaltasInjustificadas());
                    dto.setAtrasosSegundoTrim(a.getAtrasos());
                    dto.setTotalAsistenciaSegundoTrim(
                            (a.getAsistencias() != null ? a.getAsistencias() : 0) +
                                    (a.getFaltasJustificadas() != null ? a.getFaltasJustificadas() : 0) +
                                    (a.getFaltasInjustificadas() != null ? a.getFaltasInjustificadas() : 0)
                    );
                }
                case "tercero" -> {
                    dto.setAsistenciaTercerTrim(a.getAsistencias());
                    dto.setFaltasJustificadasTercerTrim(a.getFaltasJustificadas());
                    dto.setFaltasInjustificadasTercerTrim(a.getFaltasInjustificadas());
                    dto.setAtrasosTercerTrim(a.getAtrasos());
                    dto.setTotalAsistenciaTercerTrim(
                            (a.getAsistencias() != null ? a.getAsistencias() : 0) +
                                    (a.getFaltasJustificadas() != null ? a.getFaltasJustificadas() : 0) +
                                    (a.getFaltasInjustificadas() != null ? a.getFaltasInjustificadas() : 0)
                    );
                }
            }
        });

        comportamientoRepository.findByEstudianteAndTrimestreAndPeriodo(
                n.getEstudiante(), n.getTrimestre(), n.getPeriodoAcademico()
        ).ifPresent(c -> {
            switch (trimestreClave) {
                case "primer" -> dto.setComportamientoPrimerTrim(c.getComportamiento());
                case "segundo" -> dto.setComportamientoSegundoTrim(c.getComportamiento());
                case "tercero" -> dto.setComportamientoTercerTrim(c.getComportamiento());
            }
        });
    }






    @Transactional
    public void guardarNotasDesdeFormulario(NotaCompletaDTO form) {
        Estudiante estudiante = estudianteService.buscarPorCedula(form.getCedulaEstudiante());
        Materia materia = materiaService.buscarPorNombre(form.getAreaMateria());
        PeriodoAcademico periodo = periodoAcademicoService.buscarPorNombre(form.getNombrePeriodo());

        Trimestre t1 = trimestreService.buscarPorNombre("Primer Trimestre");
        Trimestre t2 = trimestreService.buscarPorNombre("Segundo Trimestre");
        Trimestre t3 = trimestreService.buscarPorNombre("Tercer Trimestre");

        if (estudiante == null || materia == null || periodo == null)
            throw new IllegalArgumentException("Faltan datos necesarios.");

        guardarNotaYRelacionados(estudiante, materia, periodo, t1,
                form.getNotaNumericaPrimerTrim(), form.getNotaCualitativaPrimerTrim(),
                form.getAsistenciaPrimerTrim(), form.getFaltasJustificadasPrimerTrim(),
                form.getFaltasInjustificadasPrimerTrim(), form.getAtrasosPrimerTrim(),
                form.getComportamientoPrimerTrim());

        guardarNotaYRelacionados(estudiante, materia, periodo, t2,
                form.getNotaNumericaSegundoTrim(), form.getNotaCualitativaSegundoTrim(),
                form.getAsistenciaSegundoTrim(), form.getFaltasJustificadasSegundoTrim(),
                form.getFaltasInjustificadasSegundoTrim(), form.getAtrasosSegundoTrim(),
                form.getComportamientoSegundoTrim());

        guardarNotaYRelacionados(estudiante, materia, periodo, t3,
                form.getNotaNumericaTercerTrim(), form.getNotaCualitativaTercerTrim(),
                form.getAsistenciaTercerTrim(), form.getFaltasJustificadasTercerTrim(),
                form.getFaltasInjustificadasTercerTrim(), form.getAtrasosTercerTrim(),
                form.getComportamientoTercerTrim());
    }

    private void guardarNotaYRelacionados(Estudiante estudiante, Materia materia, PeriodoAcademico periodo,
                                          Trimestre trimestre, Double notaNum, String notaCual, Integer asistencias,
                                          Integer faltasJustificadas, Integer faltasInjustificadas, Integer atrasos,
                                          String comportamientoTexto) {

        // --- Guardar o actualizar Nota ---
        Notas nota = notasRepository
                .findByEstudianteAndMateriaAndPeriodoAcademicoAndTrimestre(estudiante, materia, periodo, trimestre)
                .orElse(new Notas());

        // Actualizar campos por trimestre
        if ("Primer Trimestre".equalsIgnoreCase(trimestre.getNombre())) {
            nota.setNotaNumerica(notaNum);
            nota.setNotaCualitativa(notaCual);
        } else if ("Segundo Trimestre".equalsIgnoreCase(trimestre.getNombre())) {
            nota.setNotaNumerica(notaNum);
            nota.setNotaCualitativa(notaCual);
        } else if ("Tercer Trimestre".equalsIgnoreCase(trimestre.getNombre())) {
            nota.setNotaNumerica(notaNum);
            nota.setNotaCualitativa(notaCual);
        }

        nota.setEstudiante(estudiante);
        nota.setMateria(materia);
        nota.setPeriodoAcademico(periodo);
        nota.setTrimestre(trimestre);
        nota.setNotaNumerica(notaNum);
        nota.setNotaCualitativa(notaCual);
        notasRepository.save(nota);

        // --- Guardar o actualizar Asistencia ---
        Asistencia asistencia = asistenciaRepository
                .findByEstudianteAndMateriaAndTrimestreAndPeriodo(estudiante, materia, trimestre, periodo)
                .orElse(new Asistencia());

        asistencia.setEstudiante(estudiante);
        asistencia.setMateria(materia);
        asistencia.setTrimestre(trimestre);
        asistencia.setPeriodo(periodo);
        asistencia.setAsistencias(asistencias);
        asistencia.setFaltasJustificadas(faltasJustificadas);
        asistencia.setFaltasInjustificadas(faltasInjustificadas);
        asistencia.setAtrasos(atrasos);
        asistenciaRepository.save(asistencia);

        // --- Guardar o actualizar Comportamiento ---
        Comportamiento comportamiento = comportamientoRepository
                .findByEstudianteAndTrimestreAndPeriodo(estudiante, trimestre, periodo)
                .orElse(new Comportamiento());

        comportamiento.setEstudiante(estudiante);
        comportamiento.setTrimestre(trimestre);
        comportamiento.setPeriodo(periodo);
        comportamiento.setComportamiento(comportamientoTexto);
        comportamientoRepository.save(comportamiento);
    }










    public NotaCompletaDTO obtenerNotaCompletaPorId(Long idNota) {
        Notas nota = buscarNotaPorId(idNota);
        if (nota == null) {
            throw new RuntimeException("Nota no encontrada con id: " + idNota);
        }

        String nombreCurso = "---";
        if (nota.getMateria() != null && nota.getMateria().getCursos() != null && !nota.getMateria().getCursos().isEmpty()) {
            nombreCurso = nota.getMateria().getCursos().get(0).getNombre();
        }

        List<NotaCompletaDTO> dtos = obtenerNotasCompletas(
                nota.getPeriodoAcademico().getNombre(),
                nombreCurso,
                nota.getMateria().getNombre(),
                nota.getEstudiante().getCedula(),
                null
        );

        return dtos.isEmpty() ? new NotaCompletaDTO() : dtos.get(0);
    }


// --- PDF y Reportes ---

    /**
     * Retorna lista de NotaCompletaDTO para PDF.
     * Si materia = null/"" => todas las materias del curso.
     * Si trimestre = "todos"/null => trae los 3 trimestres.
     */
    public List<NotaCompletaDTO> obtenerNotasParaPDF(String periodo,
                                                     String curso,
                                                     String materia,
                                                     String cedula,
                                                     String trimestre) {
        // Usamos el método existente que agrupa y llena los 3 trimestres
        return obtenerNotasCompletas(periodo, curso, materia, cedula, trimestre);
    }

    /**
     * Retorna un solo NotaCompletaDTO por idNota.
     */
    public NotaCompletaDTO obtenerNotaCompletaPorIdParaPDF(Long idNota) {
        return obtenerNotaCompletaPorId(idNota);
    }

    /**
     * Obtiene el reporte final de todas las materias de un estudiante en un periodo.
     */
    public List<NotaCompletaDTO> obtenerReporteFinal(String periodo, String cedula) {
        Estudiante estudiante = estudianteService.buscarPorCedula(cedula);
        if (estudiante == null) {
            return Collections.emptyList();
        }

        // Aquí NO necesitamos hacer un query manual. Directamente usamos el método agrupador
        return obtenerNotasCompletas(periodo, null, null, cedula, null);
    }

    /**
     * Filtro básico de entidades Notas (solo si necesitas trabajar con Notas directamente).
     */
    public List<Notas> filtrarNotas(String nombrePeriodo, String nombreCurso, String nombreMateria, String cedula) {
        return notasRepository.findAll().stream()
                .filter(n -> (nombrePeriodo == null || nombrePeriodo.isEmpty() ||
                        (n.getPeriodoAcademico() != null &&
                                nombrePeriodo.equals(n.getPeriodoAcademico().getNombre()))))
                .filter(n -> (nombreCurso == null || nombreCurso.isEmpty() ||
                        (n.getMateria() != null && n.getMateria().getCursos() != null &&
                                n.getMateria().getCursos().stream().anyMatch(c -> nombreCurso.equals(c.getNombre())))))
                .filter(n -> (nombreMateria == null || nombreMateria.isEmpty() ||
                        (n.getMateria() != null &&
                                nombreMateria.equals(n.getMateria().getNombre()))))
                .filter(n -> (cedula == null || cedula.isEmpty() ||
                        (n.getEstudiante() != null &&
                                cedula.equals(n.getEstudiante().getCedula()))))
                .toList();
    }


}










