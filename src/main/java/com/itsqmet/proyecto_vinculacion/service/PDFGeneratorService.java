package com.itsqmet.proyecto_vinculacion.service;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itsqmet.proyecto_vinculacion.dto.NotaCompletaDTO;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

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








    public void generarReporteNotas(
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

}
