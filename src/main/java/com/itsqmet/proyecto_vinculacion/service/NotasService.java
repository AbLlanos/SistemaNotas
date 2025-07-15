package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.dto.NotaCompletaDTO;
import com.itsqmet.proyecto_vinculacion.entity.*;
import com.itsqmet.proyecto_vinculacion.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class NotasService {

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

        Specification<Notas> spec = NotasSpecification.filtrarPorCampos(
                nombrePeriodo, nombreCurso, nombreMateria, cedula, nombreTrimestre
        );

        return notasRepository.findAll(spec);
    }

    public List<Notas> obtenerNotasPorNivel(String nombreNivel) {
        return notasRepository.findByMateriaCursoNivelEducativoNombre(nombreNivel);
    }







    public List<NotaCompletaDTO> obtenerNotasCompletas(String nombrePeriodo,
                                                       String nombreCurso,
                                                       String nombreMateria,
                                                       String cedula,
                                                       String nombreTrimestre) {

        Specification<Notas> spec = NotasSpecification.filtrarPorCampos(
                nombrePeriodo, nombreCurso, nombreMateria, cedula, nombreTrimestre);

        List<Notas> notas = notasRepository.findAll(spec);

        // Buscar los trimestres Primero, Segundo y Tercero
        Trimestre t1 = trimestreService.buscarPorNombre("Primero");
        Trimestre t2 = trimestreService.buscarPorNombre("Segundo");
        Trimestre t3 = trimestreService.buscarPorNombre("Tercero");

        return notas.stream().map(n -> {
            NotaCompletaDTO dto = new NotaCompletaDTO();

            dto.setIdNota(n.getId());

            // Datos del estudiante
            if (n.getEstudiante() != null) {
                dto.setCedulaEstudiante(n.getEstudiante().getCedula());
                dto.setNombreCompletoEstudiante(n.getEstudiante().getNombre() + " " + n.getEstudiante().getApellido());
            } else {
                dto.setCedulaEstudiante("---");
                dto.setNombreCompletoEstudiante("---");
            }

            // "Área" como nombre de la materia
            dto.setAreaMateria(n.getMateria() != null ? n.getMateria().getNombre() : "---");

            // Nota y cualitativa (por ejemplo solo para primer trimestre, si quieres, modifica para otros trimestres)
            dto.setNotaNumericaPrimerTrim(n.getNotaNumerica());
            dto.setNotaCualitativaPrimerTrim(n.getNotaCualitativa());

            if (n.getEstudiante() != null && n.getMateria() != null && n.getPeriodoAcademico() != null) {

                // Primer trimestre
                if (t1 != null) {
                    asistenciaRepository.findByEstudianteAndMateriaAndTrimestreAndPeriodo(
                            n.getEstudiante(), n.getMateria(), t1, n.getPeriodoAcademico()
                    ).ifPresent(a -> {
                        dto.setAsistenciaPrimerTrim(a.getAsistencias());
                        dto.setFaltasJustificadasPrimerTrim(a.getFaltasJustificadas());
                        dto.setFaltasInjustificadasPrimerTrim(a.getFaltasInjustificadas());
                        dto.setAtrasosPrimerTrim(a.getAtrasos());

                        dto.setTotalAsistenciaPrimerTrim(
                                (a.getAsistencias() != null ? a.getAsistencias() : 0) +
                                        (a.getFaltasJustificadas() != null ? a.getFaltasJustificadas() : 0) +
                                        (a.getFaltasInjustificadas() != null ? a.getFaltasInjustificadas() : 0)
                        );
                    });

                    comportamientoRepository.findByEstudianteAndTrimestreAndPeriodo(
                            n.getEstudiante(), t1, n.getPeriodoAcademico()
                    ).ifPresent(c -> dto.setComportamientoPrimerTrim(c.getComportamiento()));
                }

                // Segundo trimestre
                if (t2 != null) {
                    asistenciaRepository.findByEstudianteAndMateriaAndTrimestreAndPeriodo(
                            n.getEstudiante(), n.getMateria(), t2, n.getPeriodoAcademico()
                    ).ifPresent(a -> {
                        dto.setAsistenciaSegundoTrim(a.getAsistencias());
                        dto.setFaltasJustificadasSegundoTrim(a.getFaltasJustificadas());
                        dto.setFaltasInjustificadasSegundoTrim(a.getFaltasInjustificadas());
                        dto.setAtrasosSegundoTrim(a.getAtrasos());

                        dto.setSetTotalAsistenciaSegundoTrim(
                                (a.getAsistencias() != null ? a.getAsistencias() : 0) +
                                        (a.getFaltasJustificadas() != null ? a.getFaltasJustificadas() : 0) +
                                        (a.getFaltasInjustificadas() != null ? a.getFaltasInjustificadas() : 0)
                        );
                    });

                    comportamientoRepository.findByEstudianteAndTrimestreAndPeriodo(
                            n.getEstudiante(), t2, n.getPeriodoAcademico()
                    ).ifPresent(c -> dto.setComportamientoSegundoTrim(c.getComportamiento()));
                }

                // Tercer trimestre
                if (t3 != null) {
                    asistenciaRepository.findByEstudianteAndMateriaAndTrimestreAndPeriodo(
                            n.getEstudiante(), n.getMateria(), t3, n.getPeriodoAcademico()
                    ).ifPresent(a -> {
                        dto.setAsistenciaTercerTrim(a.getAsistencias());
                        dto.setFaltasJustificadasTercerTrim(a.getFaltasJustificadas());
                        dto.setFaltasInjustificadasTercerTrim(a.getFaltasInjustificadas());
                        dto.setAtrasosTercerTrim(a.getAtrasos());

                        dto.setSetTotalAsistenciaTercerTrim(
                                (a.getAsistencias() != null ? a.getAsistencias() : 0) +
                                        (a.getFaltasJustificadas() != null ? a.getFaltasJustificadas() : 0) +
                                        (a.getFaltasInjustificadas() != null ? a.getFaltasInjustificadas() : 0)
                        );
                    });

                    comportamientoRepository.findByEstudianteAndTrimestreAndPeriodo(
                            n.getEstudiante(), t3, n.getPeriodoAcademico()
                    ).ifPresent(c -> dto.setComportamientoTercerTrim(c.getComportamiento()));
                }
            }

            return dto;
        }).toList();
    }






    @Transactional
    public void guardarNotasDesdeFormulario(NotaCompletaDTO form) {
        Estudiante estudiante = estudianteService.buscarPorCedula(form.getCedulaEstudiante());
        Materia materia = materiaService.buscarPorNombre(form.getAreaMateria());
        PeriodoAcademico periodo = periodoAcademicoService.buscarPorNombre("Mayo 2024 - Febrero 2025"); // o form.getNombrePeriodo()

        Trimestre t1 = trimestreService.buscarPorNombre("Primero");
        Trimestre t2 = trimestreService.buscarPorNombre("Segundo");
        Trimestre t3 = trimestreService.buscarPorNombre("Tercero");

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

        // Guardar Nota
        Notas nota = new Notas();
        nota.setEstudiante(estudiante);
        nota.setMateria(materia);
        nota.setPeriodoAcademico(periodo);
        nota.setTrimestre(trimestre);
        nota.setNotaNumerica(notaNum);
        nota.setNotaCualitativa(notaCual);
        notasRepository.save(nota);

        // Guardar Asistencia
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

        // Guardar Comportamiento
        Comportamiento comportamiento = comportamientoRepository
                .findByEstudianteAndTrimestreAndPeriodo(estudiante, trimestre, periodo)
                .orElse(new Comportamiento());

        comportamiento.setEstudiante(estudiante);
        comportamiento.setTrimestre(trimestre);
        comportamiento.setPeriodo(periodo);
        comportamiento.setComportamiento(comportamientoTexto);

        comportamientoRepository.save(comportamiento);
    }




}










