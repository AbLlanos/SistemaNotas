package com.itsqmet.proyecto_vinculacion.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
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
        Document document = new Document(pdf);

        // --- CABECERA ---
        document.add(new Paragraph("REPORTE ACADÉMICO").setBold().setFontSize(18));
        document.add(new Paragraph("Estudiante: " + nombreEstudiante));
        document.add(new Paragraph("Año Lectivo: " + periodo));
        document.add(new Paragraph("\n"));

        // --- TABLA PRINCIPAL (Notas y Cualitativas) ---
        float[] columnWidths = {3, 2, 2, 2, 2, 2, 2};
        Table tablaNotas = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

        tablaNotas.addHeaderCell("Materia");
        tablaNotas.addHeaderCell("Nota 1T");
        tablaNotas.addHeaderCell("Cualitativa 1T");
        tablaNotas.addHeaderCell("Nota 2T");
        tablaNotas.addHeaderCell("Cualitativa 2T");
        tablaNotas.addHeaderCell("Nota 3T");
        tablaNotas.addHeaderCell("Cualitativa 3T");

        for (NotaCompletaDTO dto : notas) {
            tablaNotas.addCell(dto.getAreaMateria() != null ? dto.getAreaMateria() : "---");

            // Primer trimestre
            tablaNotas.addCell( mostrarTrimestre(dto.getNotaNumericaPrimerTrim(), "Primer Trimestre", trimestreSeleccionado));
            tablaNotas.addCell( mostrarTrimestre(dto.getNotaCualitativaPrimerTrim(), "Primer Trimestre", trimestreSeleccionado));

            // Segundo trimestre
            tablaNotas.addCell( mostrarTrimestre(dto.getNotaNumericaSegundoTrim(), "Segundo Trimestre", trimestreSeleccionado));
            tablaNotas.addCell( mostrarTrimestre(dto.getNotaCualitativaSegundoTrim(), "Segundo Trimestre", trimestreSeleccionado));

            // Tercer trimestre
            tablaNotas.addCell( mostrarTrimestre(dto.getNotaNumericaTercerTrim(), "Tercer Trimestre", trimestreSeleccionado));
            tablaNotas.addCell( mostrarTrimestre(dto.getNotaCualitativaTercerTrim(), "Tercer Trimestre", trimestreSeleccionado));
        }

        document.add(tablaNotas);
        document.add(new Paragraph("\n"));

        // --- CUADRO DE ASISTENCIAS Y COMPORTAMIENTO ---
        float[] asistColumnWidths = {3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
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
            tablaAsistencias.addCell(dto.getAreaMateria() != null ? dto.getAreaMateria() : "---");

            // 1T
            tablaAsistencias.addCell( mostrarTrimestre(dto.getAsistenciaPrimerTrim(), "Primer Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell( mostrarTrimestre(dto.getFaltasJustificadasPrimerTrim(), "Primer Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell( mostrarTrimestre(dto.getFaltasInjustificadasPrimerTrim(), "Primer Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell( mostrarTrimestre(dto.getAtrasosPrimerTrim(), "Primer Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell( mostrarTrimestre(dto.getComportamientoPrimerTrim(), "Primer Trimestre", trimestreSeleccionado));

            // 2T
            tablaAsistencias.addCell( mostrarTrimestre(dto.getAsistenciaSegundoTrim(), "Segundo Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell( mostrarTrimestre(dto.getFaltasJustificadasSegundoTrim(), "Segundo Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell( mostrarTrimestre(dto.getFaltasInjustificadasSegundoTrim(), "Segundo Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell( mostrarTrimestre(dto.getAtrasosSegundoTrim(), "Segundo Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell( mostrarTrimestre(dto.getComportamientoSegundoTrim(), "Segundo Trimestre", trimestreSeleccionado));

            // 3T
            tablaAsistencias.addCell( mostrarTrimestre(dto.getAsistenciaTercerTrim(), "Tercer Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell( mostrarTrimestre(dto.getFaltasJustificadasTercerTrim(), "Tercer Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell( mostrarTrimestre(dto.getFaltasInjustificadasTercerTrim(), "Tercer Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell( mostrarTrimestre(dto.getAtrasosTercerTrim(), "Tercer Trimestre", trimestreSeleccionado));
            tablaAsistencias.addCell( mostrarTrimestre(dto.getComportamientoTercerTrim(), "Tercer Trimestre", trimestreSeleccionado));
        }

        document.add(tablaAsistencias);

        // Footer
        document.add(new Paragraph("\nDocumento generado automáticamente")
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER));
        document.close();
    }

    private String mostrarTrimestre(Object valor, String trimestre, String seleccionado) {
        if (seleccionado == null || seleccionado.equalsIgnoreCase("todos") || trimestre.equalsIgnoreCase(seleccionado)) {
            return valor != null ? valor.toString() : "--";
        }
        return "--";
    }

}
