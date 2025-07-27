package com.itsqmet.proyecto_vinculacion.service;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itsqmet.proyecto_vinculacion.dto.NotaCompletaDTO;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PDFGeneratorService {

    /**
     * Genera un PDF en el OutputStream provisto.
     * @param notas     Una o varias filas (materias) para el estudiante.
     * @param trimestre "Primer Trimestre" | "Segundo Trimestre" | "Tercer Trimestre" | null/""/"todos"
     */
    public void generarPdfNotas(List<NotaCompletaDTO> notas, String trimestre, OutputStream outputStream) throws IOException {
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("REPORTE ACADÉMICO").setBold().setFontSize(16));

        if (trimestre != null && !trimestre.isBlank() && !"todos".equalsIgnoreCase(trimestre)) {
            document.add(new Paragraph("Trimestre: " + trimestre));
        } else {
            document.add(new Paragraph("Trimestres: Todos"));
        }
        document.add(new Paragraph("\n"));

        // Tabla base (col ancho: Materia, Nota, Asistencias, Comportamiento)
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2}))
                .useAllAvailableWidth();

        tabla.addHeaderCell("Materia");
        tabla.addHeaderCell("Nota");
        tabla.addHeaderCell("Asistencias");
        tabla.addHeaderCell("Comportamiento");

        for (NotaCompletaDTO dto : notas) {
            tabla.addCell(dto.getAreaMateria() != null ? dto.getAreaMateria() : "---");

            if ("Primer Trimestre".equalsIgnoreCase(trimestre)) {
                tabla.addCell(nStr(dto.getNotaNumericaPrimerTrim()));
                tabla.addCell(nStr(dto.getAsistenciaPrimerTrim()));
                tabla.addCell(sStr(dto.getComportamientoPrimerTrim()));
            } else if ("Segundo Trimestre".equalsIgnoreCase(trimestre)) {
                tabla.addCell(nStr(dto.getNotaNumericaSegundoTrim()));
                tabla.addCell(nStr(dto.getAsistenciaSegundoTrim()));
                tabla.addCell(sStr(dto.getComportamientoSegundoTrim()));
            } else if ("Tercer Trimestre".equalsIgnoreCase(trimestre)) {
                tabla.addCell(nStr(dto.getNotaNumericaTercerTrim()));
                tabla.addCell(nStr(dto.getAsistenciaTercerTrim()));
                tabla.addCell(sStr(dto.getComportamientoTercerTrim()));
            } else {
                // TODOS LOS TRIMESTRES (resumen compacto)
                String notasTotales = "P1:" + nStr(dto.getNotaNumericaPrimerTrim())
                        + " | P2:" + nStr(dto.getNotaNumericaSegundoTrim())
                        + " | P3:" + nStr(dto.getNotaNumericaTercerTrim());
                tabla.addCell(notasTotales);

                int totalAsist = nullToZero(dto.getTotalAsistenciaPrimerTrim())
                        + nullToZero(dto.getTotalAsistenciaSegundoTrim())
                        + nullToZero(dto.getTotalAsistenciaTercerTrim());
                tabla.addCell(String.valueOf(totalAsist));

                // Podrías concatenar comportamiento por trimestre si quieres
                tabla.addCell("-");
            }
        }

        document.add(tabla);
        document.add(new Paragraph("\nDocumento generado automáticamente")
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));

        document.close();
    }

    private String nStr(Number n) {
        return n == null ? "-" : n.toString();
    }

    private String sStr(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    private int nullToZero(Integer i) { return i == null ? 0 : i; }









    private String getCursoDesdeNotas(List<NotaCompletaDTO> notas) {
        if (notas == null || notas.isEmpty()) return "---";
        return notas.get(0).getNombreCurso(); // o como hayas nombrado ese campo en el DTO
    }

    private boolean esComplementaria(NotaCompletaDTO dto) {
        return dto.getTipoMateria() != null && dto.getTipoMateria().equalsIgnoreCase("complementaria");
    }






    private String getNotaFinalCualitativaPorTrimestre(List<NotaCompletaDTO> notas, String trimestre) {
        String nota = "--";
        for (NotaCompletaDTO dto : notas) {
            switch (trimestre) {
                case "Primer Trimestre":
                    if (dto.getNotaCualitativaPrimerTrim() != null) {
                        nota = dto.getNotaCualitativaPrimerTrim();
                    }
                    break;
                case "Segundo Trimestre":
                    if (dto.getNotaCualitativaSegundoTrim() != null) {
                        nota = dto.getNotaCualitativaSegundoTrim();
                    }
                    break;
                case "Tercer Trimestre":
                    if (dto.getNotaCualitativaTercerTrim() != null) {
                        nota = dto.getNotaCualitativaTercerTrim();
                    }
                    break;
            }
        }
        return nota;
    }



    public void generarReporteNotas(
            String nombreEstudiante,
            String periodo,
            List<NotaCompletaDTO> notas,
            String trimestreSeleccionado,
            OutputStream outputStream
    ) throws IOException {

        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4.rotate());
        document.setMargins(10, 10, 10, 10);
// --- ENCABEZADO INSTITUCIONAL ---
        Table encabezado = new Table(UnitValue.createPercentArray(new float[]{20f, 60f, 20f}))
                .useAllAvailableWidth();

        try {
            String logoPath = new ClassPathResource("static/img/logo.png").getFile().getAbsolutePath();
            ImageData imageData = ImageDataFactory.create(logoPath);
            Image img = new Image(imageData);
            img.setHeight(40);

            encabezado.addCell(new Cell()
                    .add(img)
                    .setBorder(Border.NO_BORDER)
                    .setPaddingLeft(150)
            );

        } catch (Exception e) {
            encabezado.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        }

// Título central
        encabezado.addCell(
                new Cell()
                        .add(new Paragraph("UNIDAD EDUCATIVA PARTICULAR  \"LINCOLN LARREA BENALCÁZAR\""))
                        .setFontSize(12)
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBorder(Border.NO_BORDER)
                        .setPaddingTop(15)
        );

// Segundo logo a la derecha
        try {
            String logoDerPath = new ClassPathResource("static/img/logo4.png").getFile().getAbsolutePath();
            ImageData imageData2 = ImageDataFactory.create(logoDerPath);
            Image img2 = new Image(imageData2);
            img2.setHeight(40);

            encabezado.addCell(new Cell()
                    .add(img2)
                    .setBorder(Border.NO_BORDER)
                    .setPaddingRight(30)
            );

        } catch (Exception e) {
            encabezado.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        }

        document.add(encabezado);



        // Subtítulo
        document.add(new Paragraph("REPORTE DE CALIFICACIONES")
                .setFontSize(10)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(2)
        );

        document.add(new Paragraph("AÑO LECTIVO: " + (periodo != null ? periodo : "---"))
                .setFontSize(10)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(2)
                .setMarginBottom(8)
        );

        // Datos del estudiante alineados a la izquierda
        // Tabla con 2 columnas: una para texto, otra para "relleno" si fuera necesario (en este caso no usamos)
        Table datosEst = new Table(UnitValue.createPercentArray(new float[]{2, 8})).useAllAvailableWidth();

// Columna 1: etiquetas ("Estudiante" y "Grado"), en una celda, apiladas verticalmente
        Paragraph etiquetas = new Paragraph()
                .add(new Text("Estudiante:").setBold().setFontSize(12))
                .add("\n") // salto de línea
                .add(new Text("Grado:").setBold().setFontSize(12))
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginLeft(10);

// Columna 2: valores (nombre estudiante y grado), en una celda, apilados verticalmente
        Paragraph valores = new Paragraph()
                .add(new Text(nombreEstudiante != null ? nombreEstudiante : "---").setFontSize(12))
                .add("\n") // salto de línea
                .add(new Text(getCursoDesdeNotas(notas)).setFontSize(12))
                .setTextAlignment(TextAlignment.LEFT)
                .setMarginLeft(10)
                .setMarginBottom(10);

        datosEst.addCell(new Cell().add(etiquetas).setBorder(Border.NO_BORDER).setPadding(0));
        datosEst.addCell(new Cell().add(valores).setBorder(Border.NO_BORDER).setPadding(0));

        document.add(datosEst);

// Finalmente, añade la tabla al documento para que se imprima





        // Separar materias
        List<NotaCompletaDTO> notasRegular = notas.stream()
                .filter(n -> !esComplementaria(n)).collect(Collectors.toList());

        List<NotaCompletaDTO> notasComplementarias = notas.stream()
                .filter(this::esComplementaria).collect(Collectors.toList());






// === Tabla Notas Regulares ===
// === Tabla Notas Regulares ===

        Color grisPlomo = new DeviceRgb(180, 180, 180);

        if (!notasRegular.isEmpty()) {
            // ================= PRIMERA TABLA ======================
            float[] columnWidthsPrimeraTabla = {20f, 10f, 10f, 10f, 10f, 10f, 10f}; // 7 columnas
            Table tablaNotas = new Table(UnitValue.createPercentArray(columnWidthsPrimeraTabla)).useAllAvailableWidth();

            // --- Cabecera ---
            tablaNotas.addHeaderCell(celdaCabecera("Área", grisPlomo).setFontSize(10));
            tablaNotas.addHeaderCell(celdaCabecera("Primer Trimestre", grisPlomo).setFontSize(10));
            tablaNotas.addHeaderCell(celdaCabecera("Cualitativa", grisPlomo).setFontSize(10));
            tablaNotas.addHeaderCell(celdaCabecera("Segundo Trimestre", grisPlomo).setFontSize(10));
            tablaNotas.addHeaderCell(celdaCabecera("Cualitativa", grisPlomo).setFontSize(10));
            tablaNotas.addHeaderCell(celdaCabecera("Tercer Trimestre", grisPlomo).setFontSize(10));
            tablaNotas.addHeaderCell(celdaCabecera("Cualitativa", grisPlomo).setFontSize(10));

            double sumaNota1T = 0; int countNota1T = 0;
            double sumaNota2T = 0; int countNota2T = 0;
            double sumaNota3T = 0; int countNota3T = 0;

            for (NotaCompletaDTO dto : notasRegular) {
                tablaNotas.addCell(new Cell().add(new Paragraph(safeVal(dto.getAreaMateria()))).setFontSize(10));

                tablaNotas.addCell(new Cell().add(new Paragraph(mostrarTrimestre(dto.getNotaNumericaPrimerTrim(), "Primer Trimestre", trimestreSeleccionado))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
                tablaNotas.addCell(new Cell().add(new Paragraph(mostrarTrimestre(dto.getNotaCualitativaPrimerTrim(), "Primer Trimestre", trimestreSeleccionado))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));

                tablaNotas.addCell(new Cell().add(new Paragraph(mostrarTrimestre(dto.getNotaNumericaSegundoTrim(), "Segundo Trimestre", trimestreSeleccionado))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
                tablaNotas.addCell(new Cell().add(new Paragraph(mostrarTrimestre(dto.getNotaCualitativaSegundoTrim(), "Segundo Trimestre", trimestreSeleccionado))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));

                tablaNotas.addCell(new Cell().add(new Paragraph(mostrarTrimestre(dto.getNotaNumericaTercerTrim(), "Tercer Trimestre", trimestreSeleccionado))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
                tablaNotas.addCell(new Cell().add(new Paragraph(mostrarTrimestre(dto.getNotaCualitativaTercerTrim(), "Tercer Trimestre", trimestreSeleccionado))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));

                if (mostrarColumna("Primer Trimestre", trimestreSeleccionado) && dto.getNotaNumericaPrimerTrim() != null) {
                    sumaNota1T += dto.getNotaNumericaPrimerTrim();
                    countNota1T++;
                }
                if (mostrarColumna("Segundo Trimestre", trimestreSeleccionado) && dto.getNotaNumericaSegundoTrim() != null) {
                    sumaNota2T += dto.getNotaNumericaSegundoTrim();
                    countNota2T++;
                }
                if (mostrarColumna("Tercer Trimestre", trimestreSeleccionado) && dto.getNotaNumericaTercerTrim() != null) {
                    sumaNota3T += dto.getNotaNumericaTercerTrim();
                    countNota3T++;
                }
            }

            // Fila de promedios
            tablaNotas.addCell(new Cell().add(new Paragraph("PROMEDIO")).setBold().setFontSize(10));
            tablaNotas.addCell(formatProm(countNota1T, sumaNota1T)).setFontSize(10).setTextAlignment(TextAlignment.CENTER);
            tablaNotas.addCell(new Cell().add(new Paragraph(notasRegular.stream()
                    .map(NotaCompletaDTO::getNotaCualitativaFinalPrimerTrim).filter(Objects::nonNull).findFirst().orElse("--"))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
            tablaNotas.addCell(formatProm(countNota2T, sumaNota2T)).setFontSize(10).setTextAlignment(TextAlignment.CENTER);
            tablaNotas.addCell(new Cell().add(new Paragraph(notasRegular.stream()
                    .map(NotaCompletaDTO::getNotaCualitativaFinalSegundoTrim).filter(Objects::nonNull).findFirst().orElse("--"))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
            tablaNotas.addCell(formatProm(countNota3T, sumaNota3T)).setFontSize(10).setTextAlignment(TextAlignment.CENTER);
            tablaNotas.addCell(new Cell().add(new Paragraph(notasRegular.stream()
                    .map(NotaCompletaDTO::getNotaCualitativaFinalTercerTrim).filter(Objects::nonNull).findFirst().orElse("--"))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));

            // Complementarias
            if (!notasComplementarias.isEmpty()) {
                tablaNotas.addCell(new Cell(1, 7).add(new Paragraph("Actividades Complementarias").setBold().setFontSize(12))
                        .setTextAlignment(TextAlignment.LEFT).setBackgroundColor(new DeviceRgb(230, 230, 230)));

                for (NotaCompletaDTO dto : notasComplementarias) {
                    tablaNotas.addCell(new Cell().add(new Paragraph(safeVal(dto.getAreaMateria()))).setFontSize(10));
                    tablaNotas.addCell(new Cell().add(new Paragraph(mostrarTrimestre(dto.getNotaNumericaPrimerTrim(), "Primer Trimestre", trimestreSeleccionado))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
                    tablaNotas.addCell(new Cell().add(new Paragraph(mostrarTrimestre(dto.getNotaCualitativaPrimerTrim(), "Primer Trimestre", trimestreSeleccionado))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
                    tablaNotas.addCell(new Cell().add(new Paragraph(mostrarTrimestre(dto.getNotaNumericaSegundoTrim(), "Segundo Trimestre", trimestreSeleccionado))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
                    tablaNotas.addCell(new Cell().add(new Paragraph(mostrarTrimestre(dto.getNotaCualitativaSegundoTrim(), "Segundo Trimestre", trimestreSeleccionado))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
                    tablaNotas.addCell(new Cell().add(new Paragraph(mostrarTrimestre(dto.getNotaNumericaTercerTrim(), "Tercer Trimestre", trimestreSeleccionado))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
                    tablaNotas.addCell(new Cell().add(new Paragraph(mostrarTrimestre(dto.getNotaCualitativaTercerTrim(), "Tercer Trimestre", trimestreSeleccionado))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
                }
            }

            // ================= SEGUNDA TABLA: COMPORTAMIENTO Y ASISTENCIA ======================
            Table tablaComportamiento = new Table(UnitValue.createPercentArray(new float[]{20f, 8f, 8f, 8f})).useAllAvailableWidth();


            tablaComportamiento.addHeaderCell(celdaCabecera("Comportamiento", grisPlomo).setFontSize(10));
            tablaComportamiento.addHeaderCell(celdaCabecera("Primer Trimestre", grisPlomo).setFontSize(10));
            tablaComportamiento.addHeaderCell(celdaCabecera("Segundo Trimestre", grisPlomo).setFontSize(10));
            tablaComportamiento.addHeaderCell(celdaCabecera("Tercer Trimestre", grisPlomo).setFontSize(10));

            for (int i = 0; i < Math.min(notasRegular.size(), 1); i++) {
                NotaCompletaDTO dto = notasRegular.get(i);
                tablaComportamiento.addCell(new Cell().setFontSize(10).setBorderLeft(Border.NO_BORDER).setBorderBottom(Border.NO_BORDER));
                tablaComportamiento.addCell(new Cell().add(new Paragraph(safeVal(dto.getComportamientoFinalVariable1()))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
                tablaComportamiento.addCell(new Cell().add(new Paragraph(safeVal(dto.getComportamientoFinalVariable2()))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
                tablaComportamiento.addCell(new Cell().add(new Paragraph(safeVal(dto.getComportamientoFinalVariable3()))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
            }



            tablaComportamiento.addCell(new Cell(1, 1).setBorder(Border.NO_BORDER));
            tablaComportamiento.addCell(new Cell(1, 3).add(new Paragraph("TRIMESTRE").setBold().setFontSize(10).setBackgroundColor(grisPlomo)).setTextAlignment(TextAlignment.CENTER));

            tablaComportamiento.addCell(new Cell(1, 1).setBorder(Border.NO_BORDER));
            tablaComportamiento.addCell(new Paragraph("I").setBold().setTextAlignment(TextAlignment.CENTER).setFontSize(10));
            tablaComportamiento.addCell(new Paragraph("II").setBold().setTextAlignment(TextAlignment.CENTER).setFontSize(10));
            tablaComportamiento.addCell(new Paragraph("III").setBold().setTextAlignment(TextAlignment.CENTER).setFontSize(10));

            NotaCompletaDTO primerDto = notasRegular.get(0);

            tablaComportamiento.addCell(new Paragraph("Asistencias").setBold().setFontSize(10));
            tablaComportamiento.addCell(new Cell().add(new Paragraph(stringSeguro(primerDto.getAsistenciaPrimerTrim()))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
            tablaComportamiento.addCell(new Cell().add(new Paragraph(stringSeguro(primerDto.getAsistenciaSegundoTrim()))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
            tablaComportamiento.addCell(new Cell().add(new Paragraph(stringSeguro(primerDto.getAsistenciaTercerTrim()))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));

            tablaComportamiento.addCell(new Paragraph("Faltas Just.").setBold().setFontSize(10));
            tablaComportamiento.addCell(new Cell().add(new Paragraph(stringSeguro(primerDto.getFaltasJustificadasPrimerTrim()))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
            tablaComportamiento.addCell(new Cell().add(new Paragraph(stringSeguro(primerDto.getFaltasJustificadasSegundoTrim()))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
            tablaComportamiento.addCell(new Cell().add(new Paragraph(stringSeguro(primerDto.getFaltasJustificadasTercerTrim()))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));

            tablaComportamiento.addCell(new Paragraph("Faltas Injust.").setBold().setFontSize(10));
            tablaComportamiento.addCell(new Cell().add(new Paragraph(stringSeguro(primerDto.getFaltasInjustificadasPrimerTrim()))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
            tablaComportamiento.addCell(new Cell().add(new Paragraph(stringSeguro(primerDto.getFaltasInjustificadasSegundoTrim()))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
            tablaComportamiento.addCell(new Cell().add(new Paragraph(stringSeguro(primerDto.getFaltasInjustificadasTercerTrim()))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));

            tablaComportamiento.addCell(new Paragraph("Atrasos").setBold().setFontSize(10));
            tablaComportamiento.addCell(new Cell().add(new Paragraph(stringSeguro(primerDto.getAtrasosPrimerTrim()))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
            tablaComportamiento.addCell(new Cell().add(new Paragraph(stringSeguro(primerDto.getAtrasosSegundoTrim()))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
            tablaComportamiento.addCell(new Cell().add(new Paragraph(stringSeguro(primerDto.getAtrasosTercerTrim()))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));

            tablaComportamiento.addCell(
                    new Cell()
                            .add(new Paragraph(" ").setBold().setFontSize(10))
                            .setBorderLeft(Border.NO_BORDER)
                            .setBorderBottom(Border.NO_BORDER)
            );
            tablaComportamiento.addCell(new Cell().add(new Paragraph(stringSeguro(primerDto.getTotalAsistenciaPrimerTrim()))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
            tablaComportamiento.addCell(new Cell().add(new Paragraph(stringSeguro(primerDto.getTotalAsistenciaSegundoTrim()))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
            tablaComportamiento.addCell(new Cell().add(new Paragraph(stringSeguro(primerDto.getTotalAsistenciaTercerTrim()))).setTextAlignment(TextAlignment.CENTER).setFontSize(10));

            // ========== TABLA CONTENEDORA QUE UNE AMBAS LADO A LADO ==========
            Table tablaContenedora = new Table(UnitValue.createPercentArray(new float[]{70f, 30f})).useAllAvailableWidth();
            tablaContenedora.addCell(new Cell().add(tablaNotas).setBorder(Border.NO_BORDER));
            tablaContenedora.addCell(new Cell().add(tablaComportamiento).setBorder(Border.NO_BORDER).setMarginRight(5));

            document.add(tablaContenedora);
            document.add(new Paragraph("\n"));
        }






















        // Un poco más de espacio antes del comportamiento final
        document.add(new Paragraph("\n\n"));







        // Crear una tabla de 2 columnas
        Table footerTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                .useAllAvailableWidth()
                .setMarginTop(30);

// Celda izquierda: "DIRECCIÓN" con padding izquierdo ~3cm (≈ 85pt)
        Cell direccionCell = new Cell()
                .add(new Paragraph("DIRECCIÓN").setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setPaddingLeft(85) // Aprox 3cm
                .setTextAlignment(TextAlignment.LEFT);

// Celda derecha: "SECRETARÍA" con padding derecho ~3cm (≈ 85pt)

        Cell secretariaCell = new Cell()
                .add(new Paragraph("SECRETARÍA").setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setPaddingRight(85) // Aprox 3cm
                .setTextAlignment(TextAlignment.RIGHT);

// Agregar las celdas
        footerTable.addCell(direccionCell);
        footerTable.addCell(secretariaCell);

// Agregar la tabla al documento
        document.add(footerTable);

        document.close();
    }










    /* -----------------------------------------------------------------------
     * Helper: devuelve "--" si el texto es nulo o está vacío
     * ----------------------------------------------------------------------- */
    private String safeVal(String val) {
        return (val == null || val.trim().isEmpty()) ? "--" : val.trim();
    }

    private String stringSeguro(Integer val) {
        return val != null ? val.toString() : "";
    }
























    public void generarReporteNotas1(
            String nombreEstudiante,
            String periodo,
            List<NotaCompletaDTO> notas,
            String trimestreSeleccionado,
            OutputStream outputStream
    ) throws IOException {

        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);

        // ORIENTACIÓN HORIZONTAL
        Document document = new Document(pdf, PageSize.A4.rotate());
        document.setMargins(20, 20, 20, 20); // opcional

        // --- CABECERA ---
        document.add(new Paragraph("REPORTE ACADÉMICO").setBold().setFontSize(18));
        document.add(new Paragraph("Estudiante: " + (nombreEstudiante != null ? nombreEstudiante : "---")));
        document.add(new Paragraph("Año Lectivo: " + (periodo != null ? periodo : "---")));
        document.add(new Paragraph("\n"));

        /* =======================================================================
         * TABLA PRINCIPAL: NOTAS / CUALITATIVAS / COMPORTAMIENTO
         * ======================================================================= */
        float[] columnWidths = {4, 2, 2, 2, 2, 2, 2, 2, 2, 2};
        Table tablaNotas = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

        // --- Primera fila de cabecera ---
        tablaNotas.addHeaderCell(new Cell(2, 1).add(new Paragraph("Materia").setBold()));
        tablaNotas.addHeaderCell(new Cell(2, 1).add(new Paragraph("Nota 1T").setBold()));
        tablaNotas.addHeaderCell(new Cell(2, 1).add(new Paragraph("Cualitativa 1T").setBold()));
        tablaNotas.addHeaderCell(new Cell(2, 1).add(new Paragraph("Nota 2T").setBold()));
        tablaNotas.addHeaderCell(new Cell(2, 1).add(new Paragraph("Cualitativa 2T").setBold()));
        tablaNotas.addHeaderCell(new Cell(2, 1).add(new Paragraph("Nota 3T").setBold()));
        tablaNotas.addHeaderCell(new Cell(2, 1).add(new Paragraph("Cualitativa 3T").setBold()));

        // "Comportamiento" ocupa 3 columnas
        tablaNotas.addHeaderCell(new Cell(1, 3)
                .add(new Paragraph("Comportamiento").setBold().setTextAlignment(TextAlignment.CENTER)));

        // --- Segunda fila de cabecera (debajo de Comportamiento) ---
        tablaNotas.addHeaderCell(new Paragraph("1T").setBold().setTextAlignment(TextAlignment.CENTER));
        tablaNotas.addHeaderCell(new Paragraph("2T").setBold().setTextAlignment(TextAlignment.CENTER));
        tablaNotas.addHeaderCell(new Paragraph("3T").setBold().setTextAlignment(TextAlignment.CENTER));

        // Acumuladores para promedios numéricos
        double sumaNota1T = 0; int countNota1T = 0;
        double sumaNota2T = 0; int countNota2T = 0;
        double sumaNota3T = 0; int countNota3T = 0;

        for (NotaCompletaDTO dto : notas) {
            // Materia
            tablaNotas.addCell(dto.getAreaMateria() != null ? dto.getAreaMateria() : "---");

            // Notas y cualitativas
            boolean show1T = mostrarColumna("Primer Trimestre", trimestreSeleccionado);
            tablaNotas.addCell(mostrarTrimestre(dto.getNotaNumericaPrimerTrim(), "Primer Trimestre", trimestreSeleccionado));
            tablaNotas.addCell(mostrarTrimestre(dto.getNotaCualitativaPrimerTrim(), "Primer Trimestre", trimestreSeleccionado));

            tablaNotas.addCell(mostrarTrimestre(dto.getNotaNumericaSegundoTrim(), "Segundo Trimestre", trimestreSeleccionado));
            tablaNotas.addCell(mostrarTrimestre(dto.getNotaCualitativaSegundoTrim(), "Segundo Trimestre", trimestreSeleccionado));

            tablaNotas.addCell(mostrarTrimestre(dto.getNotaNumericaTercerTrim(), "Tercer Trimestre", trimestreSeleccionado));
            tablaNotas.addCell(mostrarTrimestre(dto.getNotaCualitativaTercerTrim(), "Tercer Trimestre", trimestreSeleccionado));

            // Comportamientos
            tablaNotas.addCell(mostrarTrimestre(dto.getComportamientoPrimerTrim(), "Primer Trimestre", trimestreSeleccionado));
            tablaNotas.addCell(mostrarTrimestre(dto.getComportamientoSegundoTrim(), "Segundo Trimestre", trimestreSeleccionado));
            tablaNotas.addCell(mostrarTrimestre(dto.getComportamientoTercerTrim(), "Tercer Trimestre", trimestreSeleccionado));

            // Acumular promedios
            if (show1T && dto.getNotaNumericaPrimerTrim() != null) {
                sumaNota1T += dto.getNotaNumericaPrimerTrim();
                countNota1T++;
            }
            boolean show2T = mostrarColumna("Segundo Trimestre", trimestreSeleccionado);
            if (show2T && dto.getNotaNumericaSegundoTrim() != null) {
                sumaNota2T += dto.getNotaNumericaSegundoTrim();
                countNota2T++;
            }
            boolean show3T = mostrarColumna("Tercer Trimestre", trimestreSeleccionado);
            if (show3T && dto.getNotaNumericaTercerTrim() != null) {
                sumaNota3T += dto.getNotaNumericaTercerTrim();
                countNota3T++;
            }
        }

        /* ---------------- Fila de Promedios ---------------- */
        tablaNotas.addCell(new Cell(1, 1).add(new Paragraph("PROMEDIO")).setBold());

        // Nota/Cuali 1T
        if (mostrarColumna("Primer Trimestre", trimestreSeleccionado)) {
            tablaNotas.addCell(formatProm(countNota1T, sumaNota1T));
            tablaNotas.addCell("--");
        } else {
            tablaNotas.addCell("--");
            tablaNotas.addCell("--");
        }

        // Nota/Cuali 2T
        if (mostrarColumna("Segundo Trimestre", trimestreSeleccionado)) {
            tablaNotas.addCell(formatProm(countNota2T, sumaNota2T));
            tablaNotas.addCell("--");
        } else {
            tablaNotas.addCell("--");
            tablaNotas.addCell("--");
        }

        // Nota/Cuali 3T
        if (mostrarColumna("Tercer Trimestre", trimestreSeleccionado)) {
            tablaNotas.addCell(formatProm(countNota3T, sumaNota3T));
            tablaNotas.addCell("--");
        } else {
            tablaNotas.addCell("--");
            tablaNotas.addCell("--");
        }

        // Columnas de comportamiento en promedio
        tablaNotas.addCell("--");
        tablaNotas.addCell("--");
        tablaNotas.addCell("--");

        document.add(tablaNotas);
        document.add(new Paragraph("\n"));

        /* =======================================================================
         * TABLA DETALLADA DE ASISTENCIAS + FILA TOTAL POR MATERIA
         * ======================================================================= */
        float[] asistColumnWidths = {
                4,              // Materia
                2,2,2,2,2,      // 1T
                2,2,2,2,2,      // 2T
                2,2,2,2,2       // 3T
        };
        Table tablaAsistencias = new Table(UnitValue.createPercentArray(asistColumnWidths)).useAllAvailableWidth();

        tablaAsistencias.addHeaderCell("Materia");
        tablaAsistencias.addHeaderCell("Asist. 1T");
        tablaAsistencias.addHeaderCell("FJ 1T");
        tablaAsistencias.addHeaderCell("FI 1T");
        tablaAsistencias.addHeaderCell("Atr. 1T");
        tablaAsistencias.addHeaderCell("Comp. 1T");

        tablaAsistencias.addHeaderCell("Asist. 2T");
        tablaAsistencias.addHeaderCell("FJ 2T");
        tablaAsistencias.addHeaderCell("FI 2T");
        tablaAsistencias.addHeaderCell("Atr. 2T");
        tablaAsistencias.addHeaderCell("Comp. 2T");

        tablaAsistencias.addHeaderCell("Asist. 3T");
        tablaAsistencias.addHeaderCell("FJ 3T");
        tablaAsistencias.addHeaderCell("FI 3T");
        tablaAsistencias.addHeaderCell("Atr. 3T");
        tablaAsistencias.addHeaderCell("Comp. 3T");

        for (NotaCompletaDTO dto : notas) {
            // Fila detalle
            tablaAsistencias.addCell(dto.getAreaMateria() != null ? dto.getAreaMateria() : "---");

            // 1T
            tablaAsistencias.addCell(mostrarTrimestre(dto.getAsistenciaPrimerTrim(), "Primer Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell(mostrarTrimestre(dto.getFaltasJustificadasPrimerTrim(), "Primer Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell(mostrarTrimestre(dto.getFaltasInjustificadasPrimerTrim(), "Primer Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell(mostrarTrimestre(dto.getAtrasosPrimerTrim(), "Primer Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell(mostrarTrimestre(dto.getComportamientoPrimerTrim(), "Primer Trimestre", trimestreSeleccionado));

            // 2T
            tablaAsistencias.addCell(mostrarTrimestre(dto.getAsistenciaSegundoTrim(), "Segundo Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell(mostrarTrimestre(dto.getFaltasJustificadasSegundoTrim(), "Segundo Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell(mostrarTrimestre(dto.getFaltasInjustificadasSegundoTrim(), "Segundo Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell(mostrarTrimestre(dto.getAtrasosSegundoTrim(), "Segundo Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell(mostrarTrimestre(dto.getComportamientoSegundoTrim(), "Segundo Trimestre", trimestreSeleccionado));

            // 3T
            tablaAsistencias.addCell(mostrarTrimestre(dto.getAsistenciaTercerTrim(), "Tercer Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell(mostrarTrimestre(dto.getFaltasJustificadasTercerTrim(), "Tercer Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell(mostrarTrimestre(dto.getFaltasInjustificadasTercerTrim(), "Tercer Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell(mostrarTrimestre(dto.getAtrasosTercerTrim(), "Tercer Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell(mostrarTrimestre(dto.getComportamientoTercerTrim(), "Tercer Trimestre", trimestreSeleccionado));

            // Fila TOTAL ASISTENCIA por materia
            String etiquetaTot = "Total Asist. " + (dto.getAreaMateria() != null ? dto.getAreaMateria() : "");
            tablaAsistencias.addCell(new Cell(1, 1).add(new Paragraph(etiquetaTot)).setItalic());

            tablaAsistencias.addCell(new Cell(1, 5).add(new Paragraph(
                    mostrarTrimestre(dto.getTotalAsistenciaPrimerTrim(), "Primer Trimestre", trimestreSeleccionado))));

            tablaAsistencias.addCell(new Cell(1, 5).add(new Paragraph(
                    mostrarTrimestre(dto.getTotalAsistenciaSegundoTrim(), "Segundo Trimestre", trimestreSeleccionado))));

            tablaAsistencias.addCell(new Cell(1, 5).add(new Paragraph(
                    mostrarTrimestre(dto.getTotalAsistenciaTercerTrim(), "Tercer Trimestre", trimestreSeleccionado))));
        }

        document.add(tablaAsistencias);

        // Footer
        document.add(new Paragraph("\nDocumento generado automáticamente")
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));

        document.close();
    }

    /* =======================================================================
     * HELPERS
     * ======================================================================= */

    /** Devuelve "--" si el trimestre no debe mostrarse; en caso contrario el valor (o "--" si es null). */
    private String mostrarTrimestre(Object valor, String trimestre, String seleccionado) {
        if (mostrarColumna(trimestre, seleccionado)) {
            return valor != null ? valor.toString() : "--";
        }
        return "--";
    }

    /** Lógica de visibilidad de trimestre. */
    private boolean mostrarColumna(String trimestre, String seleccionado) {
        if (seleccionado == null || seleccionado.isBlank()) return true;
        if ("todos".equalsIgnoreCase(seleccionado)) return true;
        return trimestre.equalsIgnoreCase(seleccionado);
    }

    /** Formatea promedio o "--" si no hay datos. */
    private String formatProm(int count, double suma) {
        return count > 0 ? String.format("%.2f", (suma / count)) : "--";
    }

    private Cell celdaCabecera(String texto, Color fondo) {
        return new Cell().add(new Paragraph(texto).setBold())
                .setBackgroundColor(fondo)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

}
