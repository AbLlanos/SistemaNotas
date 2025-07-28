package com.itsqmet.proyecto_vinculacion.controller;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;
import com.itsqmet.proyecto_vinculacion.dto.CursoDTO;
import com.itsqmet.proyecto_vinculacion.dto.EstudianteOptionDTO;
import com.itsqmet.proyecto_vinculacion.dto.MateriaOptionDTO;
import com.itsqmet.proyecto_vinculacion.dto.NotaCompletaDTO;
import com.itsqmet.proyecto_vinculacion.entity.*;
import com.itsqmet.proyecto_vinculacion.service.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.swing.text.Document;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pages/Admin")
public class AdminController {

    @Autowired
    private NotasService notasService;

    @Autowired
    private CursoService cursoService;

    @Autowired
    private MateriaService materiaService;

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private TrimestreService trimestreService;

    @Autowired
    private PeriodoAcademicoService periodoAcademicoService;

    @Autowired
    private NivelEducativoService nivelEducativoService;

    @Autowired
    private PDFGeneratorService pdfGeneratorService;

    @Autowired
    private AdminService adminService;


    @GetMapping("/vistaAdmin")
    public String vistaAdmin() {
        return "pages/Admin/vistaAdmin";
    }


    /* ==========================================
       1. Vista principal con filtros
       ========================================== */
    @GetMapping("/adminPerfilVista")
    public String mostrarAdminVista(
            Model model,
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "cedula", required = false) String cedula,
            @RequestParam(name = "mostrarOcultos", required = false, defaultValue = "false") boolean mostrarOcultos) {

        List<Admin> admins;

        if ((nombre == null || nombre.isEmpty()) && (cedula == null || cedula.isEmpty())) {
            admins = adminService.listarTodosAdmin();
        } else if (nombre != null && !nombre.isEmpty() && (cedula == null || cedula.isEmpty())) {
            admins = adminService.buscarPorNombre(nombre);
        } else if ((nombre == null || nombre.isEmpty()) && cedula != null && !cedula.isEmpty()) {
            admins = adminService.buscarPorCedula(cedula);
        } else {
            admins = adminService.buscarPorNombreYCedula(nombre, cedula);
        }

        // Filtra si mostrarOcultos == false
        if (!mostrarOcultos) {
            admins = admins.stream()
                    .filter(a -> Boolean.TRUE.equals(a.getVisible()))
                    .toList();
        }

        model.addAttribute("admins", admins);
        model.addAttribute("paramNombre", nombre);
        model.addAttribute("paramCedula", cedula);
        model.addAttribute("paramMostrarOcultos", mostrarOcultos);

        return "pages/Admin/adminPerfilVista";
    }

    /* ==========================================
       2. Formulario NUEVO Admin
       ========================================== */
    @GetMapping("/adminPerfilForm")
    public String mostrarAdminForm(Model model) {
        Admin admin = new Admin();
        admin.setVisible(true);
        model.addAttribute("admin", admin);
        return "pages/Admin/adminPerfilForm";
    }

    @PostMapping("/guardarAdmin")
    public String guardarAdmin(
            @Valid @ModelAttribute("admin") Admin admin,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        boolean esEdicion = admin.getId() != null;

        // Validación manual de email duplicado
        Optional<Admin> adminPorEmail = adminService.listarTodosAdmin().stream()
                .filter(a -> a.getEmail().equalsIgnoreCase(admin.getEmail()))
                .findFirst();
        if (adminPorEmail.isPresent() && (!esEdicion || !adminPorEmail.get().getId().equals(admin.getId()))) {
            result.rejectValue("email", "error.admin", "Ya existe un registro con este correo.");
        }

        // Validación manual de cédula duplicada
        Optional<Admin> adminPorCedula = adminService.listarTodosAdmin().stream()
                .filter(a -> a.getCedula().equalsIgnoreCase(admin.getCedula()))
                .findFirst();
        if (adminPorCedula.isPresent() && (!esEdicion || !adminPorCedula.get().getId().equals(admin.getId()))) {
            result.rejectValue("cedula", "error.admin", "Ya existe un registro con esta cédula.");
        }

        if (result.hasErrors()) {
            model.addAttribute("admin", admin);
            return "pages/Admin/adminPerfilForm";
        }

        try {
            admin.setRol("ADMIN");
            adminService.guardarAdmin(admin);
            redirectAttributes.addFlashAttribute("success", "Admin guardado correctamente.");
            return "redirect:/pages/Admin/adminPerfilVista";

        } catch (DataIntegrityViolationException ex) {
            // Captura errores de integridad únicos (por si la validación manual falló)
            result.reject("error.general", "Ya existe un administrador con ese correo o cédula.");
            model.addAttribute("admin", admin);
            return "pages/Admin/adminPerfilForm";
        }
    }


    /* ==========================================
       4. Editar Admin
       ========================================== */
    @GetMapping("/editarAdmin/{id}")
    public String editarAdmin(@PathVariable Long id, Model model) {
        Admin admin = adminService.buscarAdminPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Admin no encontrado: " + id));
        model.addAttribute("admin", admin);
        return "pages/Admin/adminPerfilForm";
    }

    /* ==========================================
       5. Eliminar Admin
       ========================================== */
    @GetMapping("/eliminarAdmin/{id}")
    public String eliminarAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.eliminarAdmin(id);
            redirectAttributes.addFlashAttribute("success", "Admin eliminado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar: " + e.getMessage());
        }
        return "redirect:/pages/Admin/adminPerfilVista";
    }
/*
    @GetMapping("/admin/generar-pdf")
    public void generarPdf(
            @RequestParam("idNota") Long idNota,
            @RequestParam(value = "periodo", required = false) String periodo,
            @RequestParam(value = "curso", required = false) String curso,
            @RequestParam(value = "materia", required = false) String materia,
            @RequestParam(value = "cedula", required = false) String cedula,
            @RequestParam(value = "trimestre", required = false) String trimestre,
            HttpServletResponse response
    ) throws IOException {

        // Si quieres que prevalezca idNota sobre filtros:
        NotaCompletaDTO unica = notasService.obtenerNotaCompletaPorIdParaPDF(idNota);
        List<NotaCompletaDTO> datos = java.util.Collections.singletonList(unica);

        // (Opcional) Si no se envía idNota válida, podrías caer a filtros:
        // if (unica == null) { datos = notasService.obtenerNotasParaPDF(periodo, curso, materia, cedula, trimestre); }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"notas.pdf\"");

        pdfGeneratorService.generarPdfNotas(datos, trimestre, response.getOutputStream());
    }
*/
}