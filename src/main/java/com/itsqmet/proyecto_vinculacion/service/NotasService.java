package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.dto.NotaCompletaDTO;
import com.itsqmet.proyecto_vinculacion.entity.*;
import com.itsqmet.proyecto_vinculacion.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotasService {

    //Aprobado
    //Mas importarte

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private ComportamientoFinalRepository comportamientoFinalRepository;

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

    @Autowired
    private CursoService cursoService;

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
                            (n.getEstudiante().getNombre() != null ? n.getEstudiante().getNombre() : "") + " " +
                                    (n.getEstudiante().getApellido() != null ? n.getEstudiante().getApellido() : "")
                                            .trim());
                } else {
                    nuevoDto.setCedulaEstudiante("---");
                    nuevoDto.setNombreCompletoEstudiante("---");
                }

                nuevoDto.setAreaMateria(n.getMateria() != null ? n.getMateria().getNombre() : "---");
                nuevoDto.setNombreCurso(n.getCurso() != null ? n.getCurso().getNombre() : "---");
                nuevoDto.setNombrePeriodo(n.getPeriodoAcademico() != null ? n.getPeriodoAcademico().getNombre() : "---");

                return nuevoDto;
            });

            // Notas por trimestre
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

            // **Obtener comportamiento final**
            ComportamientoFinal comportamiento = comportamientoFinalRepository
                    .findByEstudianteAndCursoAndPeriodo(n.getEstudiante(), n.getCurso(), n.getPeriodoAcademico())
                    .orElse(null);

            if (comportamiento != null) {
                dto.setComportamientoFinalVariable1(comportamiento.getComportamientoPrimerTrim());
                dto.setComportamientoFinalVariable2(comportamiento.getComportamientoSegundoTrim());
                dto.setComportamientoFinalVariable3(comportamiento.getComportamientoTercerTrim());
            } else {
                dto.setComportamientoFinalVariable1("---");
                dto.setComportamientoFinalVariable2("---");
                dto.setComportamientoFinalVariable3("---");
            }
        }


        List<NotaCompletaDTO> lista = new ArrayList<>(mapaNotas.values());

        double NOTA_MINIMA_APROBACION = 7.0;
        for (NotaCompletaDTO dto : lista) {
            calcularEstado(dto, NOTA_MINIMA_APROBACION);
        }

        return new ArrayList<>(mapaNotas.values());
    }



    private void calcularEstado(NotaCompletaDTO dto, double notaMinima) {
        // Recolectar notas no nulas
        List<Double> notas = new ArrayList<>(3);
        if (dto.getNotaNumericaPrimerTrim()  != null) notas.add(dto.getNotaNumericaPrimerTrim());
        if (dto.getNotaNumericaSegundoTrim() != null) notas.add(dto.getNotaNumericaSegundoTrim());
        if (dto.getNotaNumericaTercerTrim()  != null) notas.add(dto.getNotaNumericaTercerTrim());

        if (notas.isEmpty()) {
            dto.setEstado("---");
            dto.setPromedioFinal(null);
            return;
        }

        double prom = notas.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        dto.setPromedioFinal(prom);
        dto.setEstado(prom >= notaMinima ? "APROBADO" : "REPROBADO");
    }











    /**
     * Método auxiliar para setear asistencia y comportamiento según trimestre
     */
    //*************************Aqui se peude asigna calcul automatico de dias totales
    /*
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
                    dto.setTotalAsistenciaPrimerTrim(a.getTotalAsistencias()); // Se guarda el valor exacto
                }
                case "segundo" -> {
                    dto.setAsistenciaSegundoTrim(a.getAsistencias());
                    dto.setFaltasJustificadasSegundoTrim(a.getFaltasJustificadas());
                    dto.setFaltasInjustificadasSegundoTrim(a.getFaltasInjustificadas());
                    dto.setAtrasosSegundoTrim(a.getAtrasos());
                    dto.setTotalAsistenciaSegundoTrim(a.getTotalAsistencias()); // Se guarda el valor exacto
                }
                case "tercero" -> {
                    dto.setAsistenciaTercerTrim(a.getAsistencias());
                    dto.setFaltasJustificadasTercerTrim(a.getFaltasJustificadas());
                    dto.setFaltasInjustificadasTercerTrim(a.getFaltasInjustificadas());
                    dto.setAtrasosTercerTrim(a.getAtrasos());
                    dto.setTotalAsistenciaTercerTrim(a.getTotalAsistencias()); // Se guarda el valor exacto
                }
            }
        });

        // --- Comportamiento ---
        comportamientoRepository.findByEstudianteAndMateriaAndTrimestreAndPeriodo(
                n.getEstudiante(), n.getMateria(), n.getTrimestre(), n.getPeriodoAcademico()
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
        // ----- Resolver estudiante -----
        Estudiante estudiante = null;
        if (form.getCedulaEstudiante() != null && !form.getCedulaEstudiante().isBlank()) {
            estudiante = estudianteService.buscarPorCedula(form.getCedulaEstudiante());
        }

        // ----- Resolver curso -----
        Curso curso = null;
        if (form.getCursoId() != null) {
            curso = cursoService.buscarCursoPorId(form.getCursoId()).orElse(null);
        }
        if (curso == null && form.getNombreCurso() != null && !form.getNombreCurso().isBlank()) {
            curso = cursoService.buscarPorNombre(form.getNombreCurso());
        }

        // ----- Resolver materia -----
        Materia materia = null;
        if (form.getMateriaId() != null) {
            materia = materiaService.buscarPorId(form.getMateriaId());
        }
        if (materia == null && form.getAreaMateria() != null && !form.getAreaMateria().isBlank()) {
            materia = materiaService.buscarPorNombre(form.getAreaMateria());
        }

        // ----- Resolver periodo -----
        PeriodoAcademico periodo = null;
        if (curso != null) {
            periodo = curso.getPeriodoAcademico();
            if (form.getNombreCurso() == null || form.getNombreCurso().isBlank()) {
                form.setNombreCurso(curso.getNombre());
            }
        }
        if (periodo == null && form.getNombrePeriodo() != null && !form.getNombrePeriodo().isBlank()) {
            periodo = periodoAcademicoService.buscarPorNombre(form.getNombrePeriodo());
        }

        // ----- Si estamos editando y algo sigue nulo, usa la nota base -----
        if (form.getIdNota() != null) {
            Notas base = notasRepository.findById(form.getIdNota()).orElse(null);
            if (base == null) {
                throw new IllegalArgumentException("No se encontró la nota base con id=" + form.getIdNota());
            }
            if (estudiante == null) estudiante = base.getEstudiante();
            if (materia == null) materia = base.getMateria();
            if (periodo == null) periodo = base.getPeriodoAcademico();
            if (curso == null) curso = base.getCurso();
        }

        // ----- Validación final mínima para notas -----
        if (estudiante == null || materia == null || periodo == null) {
            throw new IllegalArgumentException("Faltan datos necesarios (estudiante/materia/periodo).");
        }

        // **VALIDAR SI YA EXISTE REGISTRO**
        if (form.getIdNota() == null) {
            boolean existe = notasRepository.existsByEstudianteAndMateriaAndPeriodoAcademico(estudiante, materia, periodo);
            if (existe) {
                throw new IllegalStateException("Ya existe un registro de notas para este estudiante, materia y periodo.");
            }
        }

        // ----- Trimestres -----
        Trimestre t1 = trimestreService.buscarPorNombre("Primer Trimestre");
        Trimestre t2 = trimestreService.buscarPorNombre("Segundo Trimestre");
        Trimestre t3 = trimestreService.buscarPorNombre("Tercer Trimestre");

        // Guardar para cada trimestre nota, comportamiento y asistencia

        // Primer trimestre
        upsertNotaYRelacionados(
                estudiante, materia, periodo, t1,
                form.getNotaNumericaPrimerTrim(), form.getNotaCualitativaPrimerTrim(),
                form.getAsistenciaPrimerTrim(), form.getFaltasJustificadasPrimerTrim(),
                form.getFaltasInjustificadasPrimerTrim(), form.getAtrasosPrimerTrim(),
                form.getComportamientoPrimerTrim(),
                form.getTotalAsistenciaPrimerTrim(),
                curso
        );
        upsertComportamiento(estudiante, materia, periodo, t1, form.getComportamientoPrimerTrim());
        upsertAsistencia(estudiante, materia, periodo, t1,
                form.getAsistenciaPrimerTrim(),
                form.getFaltasJustificadasPrimerTrim(),
                form.getFaltasInjustificadasPrimerTrim(),
                form.getAtrasosPrimerTrim(),
                form.getTotalAsistenciaPrimerTrim()
        );

        // Segundo trimestre
        upsertNotaYRelacionados(
                estudiante, materia, periodo, t2,
                form.getNotaNumericaSegundoTrim(), form.getNotaCualitativaSegundoTrim(),
                form.getAsistenciaSegundoTrim(), form.getFaltasJustificadasSegundoTrim(),
                form.getFaltasInjustificadasSegundoTrim(), form.getAtrasosSegundoTrim(),
                form.getComportamientoSegundoTrim(),
                form.getTotalAsistenciaSegundoTrim(),
                curso
        );
        upsertComportamiento(estudiante, materia, periodo, t2, form.getComportamientoSegundoTrim());
        upsertAsistencia(estudiante, materia, periodo, t2,
                form.getAsistenciaSegundoTrim(),
                form.getFaltasJustificadasSegundoTrim(),
                form.getFaltasInjustificadasSegundoTrim(),
                form.getAtrasosSegundoTrim(),
                form.getTotalAsistenciaSegundoTrim()
        );

        // Tercer trimestre
        upsertNotaYRelacionados(
                estudiante, materia, periodo, t3,
                form.getNotaNumericaTercerTrim(), form.getNotaCualitativaTercerTrim(),
                form.getAsistenciaTercerTrim(), form.getFaltasJustificadasTercerTrim(),
                form.getFaltasInjustificadasTercerTrim(), form.getAtrasosTercerTrim(),
                form.getComportamientoTercerTrim(),
                form.getTotalAsistenciaTercerTrim(),
                curso
        );
        upsertComportamiento(estudiante, materia, periodo, t3, form.getComportamientoTercerTrim());
        upsertAsistencia(estudiante, materia, periodo, t3,
                form.getAsistenciaTercerTrim(),
                form.getFaltasJustificadasTercerTrim(),
                form.getFaltasInjustificadasTercerTrim(),
                form.getAtrasosTercerTrim(),
                form.getTotalAsistenciaTercerTrim()
        );

        // Guardar Comportamiento Final por Curso si hay algún dato
        if (curso != null &&
                (notBlank(form.getComportamientoFinalVariable1()) ||
                        notBlank(form.getComportamientoFinalVariable2()) ||
                        notBlank(form.getComportamientoFinalVariable3()))) {

            upsertComportamientoCursoFinal(
                    estudiante,
                    curso,
                    periodo,
                    form.getComportamientoFinalVariable1(),
                    form.getComportamientoFinalVariable2(),
                    form.getComportamientoFinalVariable3()
            );
        }
    }

// Métodos auxiliares (ya tienes estos, pero para referencia):

    private void upsertComportamiento(Estudiante estudiante, Materia materia, PeriodoAcademico periodo,
                                      Trimestre trimestre, String comportamiento) {
        if (comportamiento == null) return;

        Optional<Comportamiento> optComp = comportamientoRepository
                .findByEstudianteAndMateriaAndPeriodoAndTrimestre(estudiante, materia, periodo, trimestre);

        Comportamiento comp = optComp.orElse(new Comportamiento());

        comp.setEstudiante(estudiante);
        comp.setMateria(materia);
        comp.setPeriodo(periodo);
        comp.setTrimestre(trimestre);
        comp.setComportamiento(comportamiento);

        comportamientoRepository.save(comp);
    }

    private void upsertNotaYRelacionados(Estudiante estudiante, Materia materia, PeriodoAcademico periodo,
                                         Trimestre trimestre, Double notaNumerica, String notaCualitativa,
                                         Integer asistencia, Integer faltasJustificadas, Integer faltasInjustificadas,
                                         Integer atrasos, String comportamiento, Integer totalAsistencia,
                                         Curso curso) {

        Optional<Notas> optionalNota = notasRepository
                .findByEstudianteAndMateriaAndPeriodoAcademicoAndTrimestre(estudiante, materia, periodo, trimestre);

        Notas nota = optionalNota.orElse(new Notas());

        nota.setEstudiante(estudiante);
        nota.setMateria(materia);
        nota.setPeriodoAcademico(periodo);
        nota.setTrimestre(trimestre);
        nota.setNotaNumerica(notaNumerica);
        nota.setNotaCualitativa(notaCualitativa);

        // ** Asignamos el curso **
        nota.setCurso(curso);

        notasRepository.save(nota);
    }

    private void upsertAsistencia(Estudiante estudiante, Materia materia, PeriodoAcademico periodo,
                                  Trimestre trimestre, Integer asistencias,
                                  Integer faltasJustificadas, Integer faltasInjustificadas,
                                  Integer atrasos, Integer totalAsistencias) {

        Optional<Asistencia> optAsistencia = asistenciaRepository.findByEstudianteAndMateriaAndPeriodoAndTrimestre(
                estudiante, materia, periodo, trimestre);

        Asistencia asistencia = optAsistencia.orElse(new Asistencia());
        asistencia.setEstudiante(estudiante);
        asistencia.setMateria(materia);
        asistencia.setPeriodo(periodo);
        asistencia.setTrimestre(trimestre);

        asistencia.setAsistencias(asistencias != null ? asistencias : 0);
        asistencia.setFaltasJustificadas(faltasJustificadas != null ? faltasJustificadas : 0);
        asistencia.setFaltasInjustificadas(faltasInjustificadas != null ? faltasInjustificadas : 0);
        asistencia.setAtrasos(atrasos != null ? atrasos : 0);
        asistencia.setTotalAsistencias(totalAsistencias != null ? totalAsistencias : 0);

        asistenciaRepository.save(asistencia);
    }


    @Autowired
    private ComportamientoFinalRepository comportamientoCursoFinalRepository;

    private void upsertComportamientoCursoFinal(
            Estudiante estudiante,
            Curso curso,
            PeriodoAcademico periodo,
            String comp1,
            String comp2,
            String comp3
    ) {
        ComportamientoFinal ent = comportamientoCursoFinalRepository
                .findByEstudianteAndCursoAndPeriodo(estudiante, curso, periodo)
                .orElseGet(ComportamientoFinal::new);

        ent.setEstudiante(estudiante);
        ent.setCurso(curso);
        ent.setPeriodo(periodo);
        ent.setComportamientoPrimerTrim(comp1);
        ent.setComportamientoSegundoTrim(comp2);
        ent.setComportamientoTercerTrim(comp3);

        comportamientoCursoFinalRepository.save(ent);
    }

    private boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }














    public NotaCompletaDTO obtenerNotaCompletaPorId(Long idNota) {
        Notas nota = buscarNotaPorId(idNota);
        if (nota == null) {
            throw new RuntimeException("Nota no encontrada con id: " + idNota);
        }

        String nombreCurso = "---";
        Long cursoId = null;
        Long periodoId = null;
        Curso cursoRelacionado = null; // <--- NUEVO para comportamiento final

        if (nota.getMateria() != null && nota.getMateria().getCursos() != null && !nota.getMateria().getCursos().isEmpty()) {
            cursoRelacionado = nota.getMateria().getCursos().get(0);
            nombreCurso = cursoRelacionado.getNombre();
            cursoId = cursoRelacionado.getId();
            if (cursoRelacionado.getPeriodoAcademico() != null) {
                periodoId = cursoRelacionado.getPeriodoAcademico().getId();
            }
        }

        List<NotaCompletaDTO> dtos = obtenerNotasCompletas(
                nota.getPeriodoAcademico().getNombre(),
                nombreCurso,
                nota.getMateria().getNombre(),
                nota.getEstudiante().getCedula(),
                null
        );

        NotaCompletaDTO dto = dtos.isEmpty() ? new NotaCompletaDTO() : dtos.get(0);

        // asegurar id nota
        dto.setIdNota(nota.getId());
        dto.setCursoId(cursoId);
        dto.setPeriodoAcademicoId(periodoId);
        dto.setNombrePeriodo(nota.getPeriodoAcademico().getNombre());
        dto.setMateriaId(nota.getMateria().getId());
        dto.setAreaMateria(nota.getMateria().getNombre());

        // Estudiante
        if (nota.getEstudiante() != null) {
            Estudiante e = nota.getEstudiante();
            dto.setCedulaEstudiante(e.getCedula());
            dto.setNombreEstudiante(e.getNombre());
            dto.setNombreCompletoEstudiante(
                    ((e.getNombre() != null ? e.getNombre() : "") + " " +
                            (e.getApellido() != null ? e.getApellido() : "")).trim()
            );

            // --- Recuperar Comportamiento Final (si existe) --- // NUEVO
            if (cursoRelacionado != null && periodoId != null) {
                comportamientoCursoFinalRepository.findByEstudianteAndCursoAndPeriodo(e, cursoRelacionado, nota.getPeriodoAcademico())
                        .ifPresent(cf -> {
                            dto.setComportamientoFinalVariable1(cf.getComportamientoPrimerTrim());
                            dto.setComportamientoFinalVariable2(cf.getComportamientoSegundoTrim());
                            dto.setComportamientoFinalVariable3(cf.getComportamientoTercerTrim());
                        });
            }
        }

        return dto;
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
        Notas nota = buscarNotaPorId(idNota);
        if (nota == null) {
            throw new RuntimeException("Nota no encontrada con id: " + idNota);
        }

        String nombreCurso = "---";
        Curso cursoRelacionado = null;
        if (nota.getMateria() != null && nota.getMateria().getCursos() != null && !nota.getMateria().getCursos().isEmpty()) {
            cursoRelacionado = nota.getMateria().getCursos().get(0);
            nombreCurso = cursoRelacionado.getNombre();
        }

        List<NotaCompletaDTO> dtos = obtenerNotasCompletas(
                nota.getPeriodoAcademico().getNombre(),
                nombreCurso,
                nota.getMateria().getNombre(),
                nota.getEstudiante().getCedula(),
                null
        );

        NotaCompletaDTO dto = dtos.isEmpty() ? new NotaCompletaDTO() : dtos.get(0);

        dto.setIdNota(nota.getId());
        dto.setCursoId(cursoRelacionado != null ? cursoRelacionado.getId() : null);
        dto.setPeriodoAcademicoId(nota.getPeriodoAcademico() != null ? nota.getPeriodoAcademico().getId() : null);
        dto.setNombrePeriodo(nota.getPeriodoAcademico() != null ? nota.getPeriodoAcademico().getNombre() : null);
        dto.setMateriaId(nota.getMateria() != null ? nota.getMateria().getId() : null);
        dto.setAreaMateria(nota.getMateria() != null ? nota.getMateria().getNombre() : null);

        if (nota.getEstudiante() != null) {
            Estudiante e = nota.getEstudiante();
            dto.setCedulaEstudiante(e.getCedula());
            dto.setNombreEstudiante(e.getNombre());
            dto.setNombreCompletoEstudiante(((e.getNombre() != null ? e.getNombre() : "") + " " +
                    (e.getApellido() != null ? e.getApellido() : "")).trim());

            // Recuperar comportamiento final usando entidades completas:
            if (cursoRelacionado != null && nota.getPeriodoAcademico() != null) {
                comportamientoCursoFinalRepository.findByEstudianteAndCursoAndPeriodo(e, cursoRelacionado, nota.getPeriodoAcademico())
                        .ifPresentOrElse(cf -> {
                            dto.setComportamientoFinalVariable1(cf.getComportamientoPrimerTrim());
                            dto.setComportamientoFinalVariable2(cf.getComportamientoSegundoTrim());
                            dto.setComportamientoFinalVariable3(cf.getComportamientoTercerTrim());
                        }, () -> {
                            // Opcional: valores por defecto si no hay comportamiento
                            dto.setComportamientoFinalVariable1("--");
                            dto.setComportamientoFinalVariable2("--");
                            dto.setComportamientoFinalVariable3("--");
                        });
            }
        }

        return dto;
    }

    /**
     * Obtiene el reporte final de todas las materias de un estudiante en un periodo.
     */
    public List<NotaCompletaDTO> obtenerReporteFinal(String nombrePeriodo, String nombreCurso, String cedulaEstudiante) {

        System.out.println("DEBUG obtenerReporteFinal(): periodo=" + nombrePeriodo +
                " | curso=" + nombreCurso + " | cedula=" + cedulaEstudiante);

        // Resolver entidades
        PeriodoAcademico periodo = (nombrePeriodo != null && !nombrePeriodo.isBlank())
                ? periodoAcademicoService.buscarPorNombre(nombrePeriodo)
                : null;

        Estudiante est = (cedulaEstudiante != null && !cedulaEstudiante.isBlank())
                ? estudianteService.buscarPorCedula(cedulaEstudiante)
                : null;

        Curso curso = null;
        if (periodo != null && nombreCurso != null && !nombreCurso.isBlank()) {
            // Ideal: método que busque curso por nombre + periodo
            curso = cursoService.buscarCursoPorPeriodoAndNombre(periodo, nombreCurso);
        }
        if (curso == null && nombreCurso != null && !nombreCurso.isBlank()) {
            // fallback: por nombre solamente
            curso = cursoService.buscarPorNombre(nombreCurso);
        }

        if (periodo == null || est == null) {
            System.out.println("DEBUG obtenerReporteFinal(): Falta periodo o estudiante. Retornando lista vacía.");
            return java.util.Collections.emptyList();
        }

        // Obtener todas las materias del reporte (pasamos materia=null para TODAS)
        // Ajusta si tu método requiere exactamente null vs ""
        List<NotaCompletaDTO> dtos = obtenerNotasCompletas(
                nombrePeriodo,
                nombreCurso,
                null,                // MATERIA = TODAS
                cedulaEstudiante,
                null                 // trimestreSeleccionado (null = todos)
        );

        System.out.println("DEBUG obtenerReporteFinal(): size dtos antes de compFinal = " + dtos.size());

        // Recuperar comportamiento final (si hay curso)
        if (curso != null) {
            comportamientoCursoFinalRepository
                    .findByEstudianteAndCursoAndPeriodo(est, curso, periodo)
                    .ifPresent(cf -> {
                        System.out.println("DEBUG obtenerReporteFinal(): compFinal encontrado -> "
                                + cf.getComportamientoPrimerTrim() + ", "
                                + cf.getComportamientoSegundoTrim() + ", "
                                + cf.getComportamientoTercerTrim());
                        // Copiar en todos los DTO
                        for (NotaCompletaDTO dto : dtos) {
                            dto.setComportamientoFinalVariable1(cf.getComportamientoPrimerTrim());
                            dto.setComportamientoFinalVariable2(cf.getComportamientoSegundoTrim());
                            dto.setComportamientoFinalVariable3(cf.getComportamientoTercerTrim());
                        }
                    });
        } else {
            System.out.println("DEBUG obtenerReporteFinal(): curso nulo; no se busca comportamiento final.");
        }

        return dtos;
    }


    public List<Notas> obtenerNotasPorCurso(Long cursoId) {
        return notasRepository.obtenerNotasPorCursoYMaterias(cursoId);
    }



}















