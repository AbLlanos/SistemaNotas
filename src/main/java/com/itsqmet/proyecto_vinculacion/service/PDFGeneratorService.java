package com.itsqmet.proyecto_vinculacion.service;

import com.itextpdf.kernel.pdf.*;
        import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
        import com.itextpdf.io.image.ImageDataFactory;
import com.itsqmet.proyecto_vinculacion.entity.Estudiante;
import com.itsqmet.proyecto_vinculacion.entity.Notas;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;

@Service
public class PDFGeneratorService {


        public void generarDesdeNota(Notas nota,
                                     String periodo,
                                     String curso,
                                     String materia,
                                     String cedula,
                                     String trimestre,
                                     OutputStream outputStream) throws IOException {

            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Logo
            /*Image logo = new Image(ImageDataFactory.create("src/main/resources/static/logo.png"));
            logo.setWidth(80);
            logo.setHeight(80);
            document.add(logo);*/

            // Información general
            document.add(new Paragraph("REPORTE ACADÉMICO").setBold().setFontSize(16));
            document.add(new Paragraph("Periodo: " + periodo));
            document.add(new Paragraph("Curso: " + curso));
            document.add(new Paragraph("Materia: " + materia));
            document.add(new Paragraph("Trimestre: " + trimestre));
            document.add(new Paragraph("Cédula: " + cedula));
            document.add(new Paragraph("\n"));

            // Información del estudiante
            Estudiante est = nota.getEstudiante();
            document.add(new Paragraph("Estudiante: " + est.getNombre() + " " + est.getApellido()));
            document.add(new Paragraph("Cédula: " + est.getCedula()));

            document.add(new Paragraph("\n"));

            // Tabla dinámica según filtros
            Table tabla = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2, 2, 2}))
                    .useAllAvailableWidth();

            tabla.addHeaderCell("Materia");
            tabla.addHeaderCell("Deber");
            tabla.addHeaderCell("Taller");
            tabla.addHeaderCell("Pruebas");
            tabla.addHeaderCell("Participación");
            tabla.addHeaderCell("Nota Final");
/*
            tabla.addCell(nota.getMateria().getNombre());
            tabla.addCell(String.valueOf(nota.getDeber()));
            tabla.addCell(String.valueOf(nota.getTaller()));
            tabla.addCell(String.valueOf(nota.getPruebas()));
            tabla.addCell(String.valueOf(nota.getParticipacion()));
            tabla.addCell(String.valueOf(nota.getNotaTotal()));
*/
            document.add(tabla);

            // Footer
            document.add(new Paragraph("\nDocumento generado automáticamente").setFontSize(10).setTextAlignment(TextAlignment.CENTER));
            document.close();
        }
    }