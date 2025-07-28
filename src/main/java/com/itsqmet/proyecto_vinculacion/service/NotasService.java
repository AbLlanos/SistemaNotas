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




    public List<NotaCompletaDTO> obtenerNotasCompletas(String nombrePeriodo,
                                                       String nombreCurso,
                                                       String nombreMateria,
                                                       String cedula,
                                                       String nombreTrimestre,
                                                       String nivelFiltro) {

        System.out.println("📥 Entrando a obtenerNotasCompletas()");
        System.out.println("🎯 Filtros recibidos:");
        System.out.println(" - Periodo: " + nombrePeriodo);
        System.out.println(" - Curso: " + nombreCurso);
        System.out.println(" - Materia: " + nombreMateria);
        System.out.println(" - Cédula: " + cedula);
        System.out.println(" - Trimestre: " + nombreTrimestre);
        System.out.println(" - Nivel filtro: " + nivelFiltro);

        Specification<Notas> spec = NotasSpecification.filtrarPorCampos(
                nombrePeriodo, nombreCurso, nombreMateria, cedula, nombreTrimestre, nivelFiltro);

        List<Notas> notas = notasRepository.findAll(spec);
        System.out.println("📝 Total notas encontradas con spec: " + notas.size());

        // Filtro adicional por nivel educativo
        if (nivelFiltro != null && !nivelFiltro.isBlank()) {
            notas = notas.stream()
                    .filter(n -> n.getNivelEducativo() != null
                            && n.getNivelEducativo().getNombre() != null
                            && n.getNivelEducativo().getNombre().toLowerCase().replace(" ", "").equals(nivelFiltro))
                    .collect(Collectors.toList());
            System.out.println("🧪 Total notas después de filtrar por nivel educativo (" + nivelFiltro + "): " + notas.size());
        }

        Map<String, NotaCompletaDTO> mapaNotas = new LinkedHashMap<>();

        for (Notas n : notas) {
            String key = n.getEstudiante().getCedula() + "|" +
                    n.getMateria().getNombre() + "|" +
                    n.getPeriodoAcademico().getNombre();

            System.out.println("🧩 Procesando clave agrupación: " + key);

            NotaCompletaDTO dto = mapaNotas.computeIfAbsent(key, k -> {
                NotaCompletaDTO nuevoDto = new NotaCompletaDTO();
                nuevoDto.setIdNota(n.getId());
                nuevoDto.setTipoMateria(n.getMateria() != null ? n.getMateria().getTipoMateria() : null);

                if (n.getEstudiante() != null) {
                    String ced = n.getEstudiante().getCedula();
                    String nombre = n.getEstudiante().getNombre();
                    String apellido = n.getEstudiante().getApellido();

                    nuevoDto.setCedulaEstudiante(ced);
                    nuevoDto.setNombreCompletoEstudiante(
                            (nombre != null ? nombre : ""));
                    System.out.println("👤 Estudiante: " + nuevoDto.getNombreCompletoEstudiante() + " (" + ced + ")");
                } else {
                    nuevoDto.setCedulaEstudiante("---");
                    nuevoDto.setNombreCompletoEstudiante("---");
                }

                nuevoDto.setAreaMateria(n.getMateria() != null ? n.getMateria().getNombre() : "---");
                nuevoDto.setNombreCurso(n.getCurso() != null ? n.getCurso().getNombre() : "---");
                nuevoDto.setNombrePeriodo(n.getPeriodoAcademico() != null ? n.getPeriodoAcademico().getNombre() : "---");

                return nuevoDto;
            });

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

        // Aquí asignas las notas cualitativas finales, con variable diferente en el for
        for (NotaCompletaDTO dtoFinal : lista) {
            Estudiante estudiante = estudianteService.buscarPorCedula(dtoFinal.getCedulaEstudiante());
            Curso curso = cursoService.buscarPorNombre(dtoFinal.getNombreCurso());
            PeriodoAcademico periodo = periodoAcademicoService.buscarPorNombre(dtoFinal.getNombrePeriodo());

            if (estudiante != null && curso != null && periodo != null) {
                notaCualitativaFinalRepository.findByEstudianteAndCursoAndPeriodo(estudiante, curso, periodo)
                        .ifPresent(ncf -> {
                            dtoFinal.setNotaCualitativaFinalPrimerTrim(ncf.getNotaFinalPrimerTrim());
                            dtoFinal.setNotaCualitativaFinalSegundoTrim(ncf.getNotaFinalSegundoTrim());
                            dtoFinal.setNotaCualitativaFinalTercerTrim(ncf.getNotaFinalTercerTrim());
                        });
            } else {
                dtoFinal.setNotaCualitativaFinalPrimerTrim("---");
                dtoFinal.setNotaCualitativaFinalSegundoTrim("---");
                dtoFinal.setNotaCualitativaFinalTercerTrim("---");
            }
        }

        double NOTA_MINIMA_APROBACION = 7.0;
        for (NotaCompletaDTO dto : lista) {
            calcularEstado(dto, NOTA_MINIMA_APROBACION);
        }

        System.out.println("✅ Total DTOs construidos: " + lista.size());
        return lista;
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













@Autowired
private  NivelEducativoService nivelEducativoService;


    @Transactional
    public void guardarNotasDesdeFormulario(NotaCompletaDTO form) {
        // ----- Resolver estudiante -----
        Estudiante estudiante = null;
        if (form.getCedulaEstudiante() != null && !form.getCedulaEstudiante().isBlank()) {
            estudiante = estudianteService.buscarPorCedula(form.getCedulaEstudiante());

            if (form.getNivelEducativoId() != null) {
                NivelEducativo nivel = nivelEducativoService.buscarOptionalPorId(form.getNivelEducativoId());
                if (nivel != null && (estudiante.getNivelEducativo() == null ||
                        !nivel.getId().equals(estudiante.getNivelEducativo().getId()))) {
                    estudiante.setNivelEducativo(nivel);
                    estudianteService.guardarEstudiante(estudiante);
                }
            }
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
                throw new IllegalStateException("Ya existe un registro de notas para este estudiante,curso, materia y periodo.");
            }
        }

        // ----- Trimestres -----
        Trimestre t1 = trimestreService.buscarPorNombre("Primer Trimestre");
        Trimestre t2 = trimestreService.buscarPorNombre("Segundo Trimestre");
        Trimestre t3 = trimestreService.buscarPorNombre("Tercer Trimestre");

        // Guardar para cada trimestre nota, comportamiento y asistencia

        // Primer trimestre

// Primer trimestre
        upsertNotaYRelacionados(estudiante, materia, periodo, t1,
                form.getNotaNumericaPrimerTrim(), form.getNotaCualitativaPrimerTrim(),
                form.getAsistenciaPrimerTrim(), form.getFaltasJustificadasPrimerTrim(),
                form.getFaltasInjustificadasPrimerTrim(), form.getAtrasosPrimerTrim(),
                form.getComportamientoPrimerTrim(), form.getTotalAsistenciaPrimerTrim(), curso);
        upsertComportamiento(estudiante, materia, periodo, t1, form.getComportamientoPrimerTrim());
        upsertAsistencia(estudiante, materia, periodo, t1,
                form.getAsistenciaPrimerTrim(), form.getFaltasJustificadasPrimerTrim(),
                form.getFaltasInjustificadasPrimerTrim(), form.getAtrasosPrimerTrim(),
                form.getTotalAsistenciaPrimerTrim());

// Guardar y propagar asistencia primer trimestre
        guardarAsistenciaYPropagar(estudiante, curso, periodo, t1, materia,
                form.getAsistenciaPrimerTrim(), form.getFaltasJustificadasPrimerTrim(),
                form.getFaltasInjustificadasPrimerTrim(), form.getAtrasosPrimerTrim(),
                form.getTotalAsistenciaPrimerTrim());

// Segundo trimestre
        upsertNotaYRelacionados(estudiante, materia, periodo, t2,
                form.getNotaNumericaSegundoTrim(), form.getNotaCualitativaSegundoTrim(),
                form.getAsistenciaSegundoTrim(), form.getFaltasJustificadasSegundoTrim(),
                form.getFaltasInjustificadasSegundoTrim(), form.getAtrasosSegundoTrim(),
                form.getComportamientoSegundoTrim(), form.getTotalAsistenciaSegundoTrim(), curso);
        upsertComportamiento(estudiante, materia, periodo, t2, form.getComportamientoSegundoTrim());
        upsertAsistencia(estudiante, materia, periodo, t2,
                form.getAsistenciaSegundoTrim(), form.getFaltasJustificadasSegundoTrim(),
                form.getFaltasInjustificadasSegundoTrim(), form.getAtrasosSegundoTrim(),
                form.getTotalAsistenciaSegundoTrim());

// Guardar y propagar asistencia segundo trimestre
        guardarAsistenciaYPropagar(estudiante, curso, periodo, t2, materia,
                form.getAsistenciaSegundoTrim(), form.getFaltasJustificadasSegundoTrim(),
                form.getFaltasInjustificadasSegundoTrim(), form.getAtrasosSegundoTrim(),
                form.getTotalAsistenciaSegundoTrim());

// Tercer trimestre
        upsertNotaYRelacionados(estudiante, materia, periodo, t3,
                form.getNotaNumericaTercerTrim(), form.getNotaCualitativaTercerTrim(),
                form.getAsistenciaTercerTrim(), form.getFaltasJustificadasTercerTrim(),
                form.getFaltasInjustificadasTercerTrim(), form.getAtrasosTercerTrim(),
                form.getComportamientoTercerTrim(), form.getTotalAsistenciaTercerTrim(), curso);
        upsertComportamiento(estudiante, materia, periodo, t3, form.getComportamientoTercerTrim());
        upsertAsistencia(estudiante, materia, periodo, t3,
                form.getAsistenciaTercerTrim(), form.getFaltasJustificadasTercerTrim(),
                form.getFaltasInjustificadasTercerTrim(), form.getAtrasosTercerTrim(),
                form.getTotalAsistenciaTercerTrim());

// Guardar y propagar asistencia tercer trimestre
        guardarAsistenciaYPropagar(estudiante, curso, periodo, t3, materia,
                form.getAsistenciaTercerTrim(), form.getFaltasJustificadasTercerTrim(),
                form.getFaltasInjustificadasTercerTrim(), form.getAtrasosTercerTrim(),
                form.getTotalAsistenciaTercerTrim());


        // Guardar comportamiento final por curso
        if (curso != null &&
                (notBlank(form.getComportamientoFinalVariable1()) ||
                        notBlank(form.getComportamientoFinalVariable2()) ||
                        notBlank(form.getComportamientoFinalVariable3()))) {
            upsertComportamientoCursoFinal(estudiante, curso, periodo,
                    form.getComportamientoFinalVariable1(),
                    form.getComportamientoFinalVariable2(),
                    form.getComportamientoFinalVariable3());
        }

        // --- Guardar nota cualitativa final ---
        if (curso != null &&
                (notBlank(form.getNotaCualitativaFinalPrimerTrim()) ||
                        notBlank(form.getNotaCualitativaFinalSegundoTrim()) ||
                        notBlank(form.getNotaCualitativaFinalTercerTrim()))) {

            upsertNotaCualitativaFinal(
                    estudiante, curso, periodo,
                    form.getNotaCualitativaFinalPrimerTrim(),
                    form.getNotaCualitativaFinalSegundoTrim(),
                    form.getNotaCualitativaFinalTercerTrim()
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

        // Asignar curso
        nota.setCurso(curso);

        // ** Asignar nivel educativo **
        if (estudiante != null && estudiante.getNivelEducativo() != null) {
            nota.setNivelEducativo(estudiante.getNivelEducativo());
        } else {
            nota.setNivelEducativo(null);
        }

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


    @Transactional
    public void guardarAsistenciaYPropagar(Estudiante estudiante, Curso curso, PeriodoAcademico periodo,
                                           Trimestre trimestre, Materia materia,
                                           Integer asistencias,
                                           Integer faltasJustificadas,
                                           Integer faltasInjustificadas,
                                           Integer atrasos,
                                           Integer totalAsistencias) {

        // Guardar asistencia para la materia original
        upsertAsistencia(estudiante, materia, periodo, trimestre,
                asistencias, faltasJustificadas, faltasInjustificadas, atrasos, totalAsistencias);

        System.out.println("Materia original: " + materia.getNombre());
        System.out.println("Propagando asistencia a materias visibles y con periodo visible del curso: " + curso.getNombre());

        // Obtener materias visibles del curso y cuyo periodo esté visible
        List<Materia> materiasDelCurso = materiaService.listarMateriasVisiblesPorCurso(curso.getId());

        for (Materia mat : materiasDelCurso) {
            System.out.println("Materia a actualizar: " + mat.getNombre());
            if (!mat.getId().equals(materia.getId())) {
                upsertAsistencia(estudiante, mat, periodo, trimestre,
                        asistencias, faltasJustificadas, faltasInjustificadas, atrasos, totalAsistencias);
            }
        }


}



    @Autowired
    private NotaCualitativaFinalRepository notaCualitativaFinalRepository;


    private void upsertNotaCualitativaFinal(
            Estudiante estudiante,
            Curso curso,
            PeriodoAcademico periodo,
            String nota1,
            String nota2,
            String nota3
    ) {
        NotaCualitativaFinal ent = notaCualitativaFinalRepository
                .findByEstudianteAndCursoAndPeriodo(estudiante, curso, periodo)
                .orElseGet(() -> {
                    NotaCualitativaFinal nueva = new NotaCualitativaFinal();
                    nueva.setEstudiante(estudiante);
                    nueva.setCurso(curso);
                    nueva.setPeriodo(periodo);
                    return nueva;
                });

        ent.setNotaFinalPrimerTrim(nota1);
        ent.setNotaFinalSegundoTrim(nota2);
        ent.setNotaFinalTercerTrim(nota3);

        notaCualitativaFinalRepository.save(ent);
    }












    public NotaCompletaDTO obtenerNotaCompletaPorId(Long idNota) {
        Notas nota = buscarNotaPorId(idNota);
        if (nota == null) {
            throw new RuntimeException("Nota no encontrada con id: " + idNota);
        }

        String nombreCurso = "---";
        Long cursoId = null;
        Long periodoId = null;
        Curso cursoRelacionado = null; // para comportamiento final

        if (nota.getMateria() != null && nota.getMateria().getCursos() != null && !nota.getMateria().getCursos().isEmpty()) {
            cursoRelacionado = nota.getMateria().getCursos().get(0);
            nombreCurso = cursoRelacionado.getNombre();
            cursoId = cursoRelacionado.getId();
            if (cursoRelacionado.getPeriodoAcademico() != null) {
                periodoId = cursoRelacionado.getPeriodoAcademico().getId();
            }
        }

        // Obtener nivelFiltro a partir del estudiante de la nota
        String nivelFiltro = (nota.getEstudiante() != null && nota.getEstudiante().getNivelEducativo() != null)
                ? nota.getEstudiante().getNivelEducativo().getNombre().toLowerCase().replace(" ", "")
                : null;

        List<NotaCompletaDTO> dtos = obtenerNotasCompletas(
                nota.getPeriodoAcademico().getNombre(),
                nombreCurso,
                nota.getMateria().getNombre(),
                nota.getEstudiante().getCedula(),
                null,  // nombreTrimestre
                nivelFiltro
        );

        NotaCompletaDTO dto = dtos.isEmpty() ? new NotaCompletaDTO() : dtos.get(0);

        // asegurar id nota y otros campos
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
                    ((e.getNombre() != null ? e.getNombre() : ""))
            );

            // Recuperar Comportamiento Final (si existe)
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


    /**
     * Retorna un solo NotaCompletaDTO por idNota.
     */

    public NotaCompletaDTO obtenerNotaCompletaPorIdParaPDF(Long idNota, String nombreTrimestre) {
        System.out.println("DEBUG obtenerNotaCompletaPorIdParaPDF(): Buscando nota con ID = " + idNota);

        Notas nota = buscarNotaPorId(idNota);
        if (nota == null) {
            System.out.println("ERROR: Nota no encontrada con ID = " + idNota);
            throw new RuntimeException("Nota no encontrada con id: " + idNota);
        }

        String nombreCurso = "---";
        Curso cursoRelacionado = null;
        if (nota.getMateria() != null && nota.getMateria().getCursos() != null && !nota.getMateria().getCursos().isEmpty()) {
            cursoRelacionado = nota.getMateria().getCursos().get(0);
            nombreCurso = cursoRelacionado.getNombre();
        }

        System.out.println("DEBUG: nombreCurso = " + nombreCurso);

        String nivelFiltro = nota.getEstudiante() != null && nota.getEstudiante().getNivelEducativo() != null
                ? nota.getEstudiante().getNivelEducativo().getNombre().toLowerCase().replace(" ", "")
                : null;

        System.out.println("DEBUG: nivelFiltro = " + nivelFiltro);

        List<NotaCompletaDTO> dtos = obtenerNotasCompletas(
                nota.getPeriodoAcademico().getNombre(),
                nombreCurso,
                nota.getMateria().getNombre(),
                nota.getEstudiante().getCedula(),
                null,
                nivelFiltro
        );

        System.out.println("DEBUG: notas encontradas = " + dtos.size());

        NotaCompletaDTO dto = dtos.isEmpty() ? new NotaCompletaDTO() : dtos.get(0);

        // Aquí se limpia según el trimestre recibido
        if (nombreTrimestre != null && !nombreTrimestre.isBlank()) {
            switch (nombreTrimestre) {
                case "Primer Trimestre":
                    dto.setNotaNumericaSegundoTrim(null);
                    dto.setNotaNumericaTercerTrim(null);
                    dto.setNotaCualitativaSegundoTrim("--");
                    dto.setNotaCualitativaTercerTrim("--");
                    dto.setAsistenciaSegundoTrim(null);
                    dto.setAsistenciaTercerTrim(null);
                    dto.setComportamientoSegundoTrim("--");
                    dto.setComportamientoTercerTrim("--");
                    dto.setComportamientoFinalVariable2("--");
                    dto.setComportamientoFinalVariable3("--");
                    dto.setNotaCualitativaFinalSegundoTrim("--");
                    dto.setNotaCualitativaFinalTercerTrim("--");
                    break;

                case "Segundo Trimestre":
                    dto.setNotaNumericaPrimerTrim(null);
                    dto.setNotaNumericaTercerTrim(null);
                    dto.setNotaCualitativaPrimerTrim("--");
                    dto.setNotaCualitativaTercerTrim("--");
                    dto.setAsistenciaPrimerTrim(null);
                    dto.setAsistenciaTercerTrim(null);
                    dto.setComportamientoPrimerTrim("--");
                    dto.setComportamientoTercerTrim("--");
                    dto.setComportamientoFinalVariable1("--");
                    dto.setComportamientoFinalVariable3("--");
                    dto.setNotaCualitativaFinalPrimerTrim("--");
                    dto.setNotaCualitativaFinalTercerTrim("--");
                    break;

                case "Tercer Trimestre":
                    dto.setNotaNumericaPrimerTrim(null);
                    dto.setNotaNumericaSegundoTrim(null);
                    dto.setNotaCualitativaPrimerTrim("--");
                    dto.setNotaCualitativaSegundoTrim("--");
                    dto.setAsistenciaPrimerTrim(null);
                    dto.setAsistenciaSegundoTrim(null);
                    dto.setComportamientoPrimerTrim("--");
                    dto.setComportamientoSegundoTrim("--");
                    dto.setComportamientoFinalVariable1("--");
                    dto.setComportamientoFinalVariable2("--");
                    dto.setNotaCualitativaFinalPrimerTrim("--");
                    dto.setNotaCualitativaFinalSegundoTrim("--");
                    break;

                default:
                    // No hacer nada o limpiar todo según necesidad
                    break;
            }
        }

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
            dto.setNombreCompletoEstudiante(((e.getNombre() != null ? e.getNombre() : "")));

            System.out.println("DEBUG: Estudiante = " + e.getCedula() + " - " + e.getNombre());

            if (cursoRelacionado != null && nota.getPeriodoAcademico() != null) {
                comportamientoCursoFinalRepository.findByEstudianteAndCursoAndPeriodo(e, cursoRelacionado, nota.getPeriodoAcademico())
                        .ifPresentOrElse(cf -> {
                            System.out.println("DEBUG: Comportamiento encontrado: "
                                    + cf.getComportamientoPrimerTrim() + ", "
                                    + cf.getComportamientoSegundoTrim() + ", "
                                    + cf.getComportamientoTercerTrim());
                            dto.setComportamientoFinalVariable1(cf.getComportamientoPrimerTrim());
                            dto.setComportamientoFinalVariable2(cf.getComportamientoSegundoTrim());
                            dto.setComportamientoFinalVariable3(cf.getComportamientoTercerTrim());
                        }, () -> {
                            System.out.println("DEBUG: Comportamiento no encontrado para el estudiante.");
                            dto.setComportamientoFinalVariable1("--");
                            dto.setComportamientoFinalVariable2("--");
                            dto.setComportamientoFinalVariable3("--");
                        });
            } else {
                System.out.println("DEBUG: No hay curso relacionado o periodo académico, no se busca comportamiento.");
            }
        }

        return dto;
    }

    /**
     * Obtiene el reporte final de todas las materias de un estudiante en un periodo.
     */
    public List<NotaCompletaDTO> obtenerReporteFinal(String nombrePeriodo, String nombreCurso, String cedulaEstudiante, String nombreTrimestre) {

        System.out.println("DEBUG obtenerReporteFinal():");
        System.out.println(" - nombrePeriodo = " + nombrePeriodo);
        System.out.println(" - nombreCurso = " + nombreCurso);
        System.out.println(" - cedulaEstudiante = " + cedulaEstudiante);
        System.out.println(" - nombreTrimestre = " + nombreTrimestre);

        PeriodoAcademico periodo = (nombrePeriodo != null && !nombrePeriodo.isBlank())
                ? periodoAcademicoService.buscarPorNombre(nombrePeriodo)
                : null;

        Estudiante est = (cedulaEstudiante != null && !cedulaEstudiante.isBlank())
                ? estudianteService.buscarPorCedula(cedulaEstudiante)
                : null;

        Curso curso = null;
        if (periodo != null && nombreCurso != null && !nombreCurso.isBlank()) {
            curso = cursoService.buscarCursoPorPeriodoAndNombre(periodo, nombreCurso);
        }
        if (curso == null && nombreCurso != null && !nombreCurso.isBlank()) {
            curso = cursoService.buscarPorNombre(nombreCurso);
            System.out.println("DEBUG: Curso encontrado por nombre sin periodo = " + (curso != null ? curso.getNombre() : "null"));
        }

        if (periodo == null) {
            System.out.println("ERROR: Periodo académico no encontrado.");
        }
        if (est == null) {
            System.out.println("ERROR: Estudiante no encontrado.");
        }

        if (periodo == null || est == null) {
            System.out.println("DEBUG obtenerReporteFinal(): Retornando lista vacía por falta de datos.");
            return java.util.Collections.emptyList();
        }

        String nivelFiltro = (est.getNivelEducativo() != null)
                ? est.getNivelEducativo().getNombre().toLowerCase().replace(" ", "")
                : null;

        System.out.println("DEBUG: nivelFiltro = " + nivelFiltro);

        // Aquí le pasamos null al nombreTrimestre para obtener todas las notas
        List<NotaCompletaDTO> dtos = obtenerNotasCompletas(
                periodo.getNombre(),
                nombreCurso,
                null, // nombreMateria
                est.getCedula(),
                null, // nombreTrimestre en obtenerNotasCompletas para traer todo
                nivelFiltro
        );

        System.out.println("DEBUG: Notas encontradas = " + dtos.size());

        // Ahora filtramos en el DTO según el trimestre seleccionado
        if (nombreTrimestre != null && !nombreTrimestre.equalsIgnoreCase("todos")) {
            for (NotaCompletaDTO dto : dtos) {
                switch (nombreTrimestre) {
                    case "Primer Trimestre":
                        // Segundo trimestre
                        dto.setNotaNumericaSegundoTrim(null);
                        dto.setNotaCualitativaSegundoTrim(null);
                        dto.setAsistenciaSegundoTrim(null);
                        dto.setFaltasJustificadasSegundoTrim(null);
                        dto.setFaltasInjustificadasSegundoTrim(null);
                        dto.setAtrasosSegundoTrim(null);
                        dto.setComportamientoSegundoTrim(null);
                        dto.setTotalAsistenciaSegundoTrim(null);

                        // Tercer trimestre
                        dto.setNotaNumericaTercerTrim(null);
                        dto.setNotaCualitativaTercerTrim(null);
                        dto.setAsistenciaTercerTrim(null);
                        dto.setFaltasJustificadasTercerTrim(null);
                        dto.setFaltasInjustificadasTercerTrim(null);
                        dto.setAtrasosTercerTrim(null);
                        dto.setComportamientoTercerTrim(null);
                        dto.setTotalAsistenciaTercerTrim(null);

                        // Comportamiento final y nota cualitativa final de 2do y 3er trimestre
                        dto.setComportamientoFinalVariable2(null);
                        dto.setComportamientoFinalVariable3(null);
                        dto.setNotaCualitativaFinalSegundoTrim(null);
                        dto.setNotaCualitativaFinalTercerTrim(null);

                        dto.setAsistenciaFinalVariable2(null);
                        dto.setAsistenciaFinalVariable3(null);

                        break;

                    case "Segundo Trimestre":
                        // Primer trimestre
                        dto.setNotaNumericaPrimerTrim(null);
                        dto.setNotaCualitativaPrimerTrim(null);
                        dto.setAsistenciaPrimerTrim(null);
                        dto.setFaltasJustificadasPrimerTrim(null);
                        dto.setFaltasInjustificadasPrimerTrim(null);
                        dto.setAtrasosPrimerTrim(null);
                        dto.setComportamientoPrimerTrim(null);
                        dto.setTotalAsistenciaPrimerTrim(null);

                        // Tercer trimestre
                        dto.setNotaNumericaTercerTrim(null);
                        dto.setNotaCualitativaTercerTrim(null);
                        dto.setAsistenciaTercerTrim(null);
                        dto.setFaltasJustificadasTercerTrim(null);
                        dto.setFaltasInjustificadasTercerTrim(null);
                        dto.setAtrasosTercerTrim(null);
                        dto.setComportamientoTercerTrim(null);
                        dto.setTotalAsistenciaTercerTrim(null);

                        // Comportamiento final y nota cualitativa final de 1er y 3er trimestre
                        dto.setComportamientoFinalVariable1(null);
                        dto.setComportamientoFinalVariable3(null);
                        dto.setNotaCualitativaFinalPrimerTrim(null);
                        dto.setNotaCualitativaFinalTercerTrim(null);

                        dto.setAsistenciaFinalVariable1(null);
                        dto.setAsistenciaFinalVariable3(null);

                        break;

                    case "Tercer Trimestre":
                        // Primer trimestre
                        dto.setNotaNumericaPrimerTrim(null);
                        dto.setNotaCualitativaPrimerTrim(null);
                        dto.setAsistenciaPrimerTrim(null);
                        dto.setFaltasJustificadasPrimerTrim(null);
                        dto.setFaltasInjustificadasPrimerTrim(null);
                        dto.setAtrasosPrimerTrim(null);
                        dto.setComportamientoPrimerTrim(null);
                        dto.setTotalAsistenciaPrimerTrim(null);

                        // Segundo trimestre
                        dto.setNotaNumericaSegundoTrim(null);
                        dto.setNotaCualitativaSegundoTrim(null);
                        dto.setAsistenciaSegundoTrim(null);
                        dto.setFaltasJustificadasSegundoTrim(null);
                        dto.setFaltasInjustificadasSegundoTrim(null);
                        dto.setAtrasosSegundoTrim(null);
                        dto.setComportamientoSegundoTrim(null);
                        dto.setTotalAsistenciaSegundoTrim(null);

                        // Comportamiento final y nota cualitativa final de 1er y 2do trimestre
                        dto.setComportamientoFinalVariable1(null);
                        dto.setComportamientoFinalVariable2(null);
                        dto.setNotaCualitativaFinalPrimerTrim(null);
                        dto.setNotaCualitativaFinalSegundoTrim(null);

                        dto.setAsistenciaFinalVariable1(null);
                        dto.setAsistenciaFinalVariable2(null);

                        break;

                    default:
                        // No hacer nada si es "todos" o no coincide
                        break;
                }
            }
        }

        return dtos;
    }



    public List<Notas> obtenerNotasPorCurso(Long cursoId) {
        return notasRepository.obtenerNotasPorCursoYMaterias(cursoId);
    }



}















