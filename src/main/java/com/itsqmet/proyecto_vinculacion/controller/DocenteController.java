package com.itsqmet.proyecto_vinculacion.controller;

import com.itsqmet.proyecto_vinculacion.entity.Docente;
import com.itsqmet.proyecto_vinculacion.service.DocenteService;
import com.itsqmet.proyecto_vinculacion.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class DocenteController {

    @Autowired
    private DocenteService docenteService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/pages/Admin/docenteVista")
    public String mostrarDocenteVista(
            Model model,
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "cedula", required = false) String cedula,
            @RequestParam(name = "mostrarOcultos", required = false, defaultValue = "false") boolean mostrarOcultos) {

        List<Docente> docentes;

        if ((nombre == null || nombre.isEmpty()) && (cedula == null || cedula.isEmpty())) {
            docentes = docenteService.listarTodosDocentes();
        } else if (nombre != null && !nombre.isEmpty() && (cedula == null || cedula.isEmpty())) {
            docentes = docenteService.buscarPorNombre(nombre);
        } else if ((nombre == null || nombre.isEmpty()) && cedula != null && !cedula.isEmpty()) {
            docentes = docenteService.buscarPorCedula(cedula);
        } else {
            docentes = docenteService.buscarPorNombreYCedula(nombre, cedula);
        }

        if (!mostrarOcultos) {
            docentes = docentes.stream()
                    .filter(d -> Boolean.TRUE.equals(d.getVisible()))
                    .toList();
        }

        model.addAttribute("docentes", docentes);
        model.addAttribute("paramMostrarOcultos", mostrarOcultos);
        model.addAttribute("paramNombre", nombre);
        model.addAttribute("paramCedula", cedula);
        return "pages/Admin/docenteVista";
    }

    //2.Ruta Form
    @GetMapping("/pages/Admin/docenteForm")
    public String mostrarDocenteForm(Model model){
        model.addAttribute("docente", new Docente());
        return "pages/Admin/docenteForm";
    }

    //3.Insertar un Nuevo Docente
    @PostMapping("/guardarDocente")
    public String guardarDocente(
            @Valid @ModelAttribute Docente docente,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttrs) {

        docente.setRol("DOCENTE");

        // Validación con anotaciones
        if (result.hasErrors()) {
            model.addAttribute("docente", docente);
            return "pages/Admin/docenteForm";
        }

        boolean esEdicion = docente.getId() != null;

        // Validación manual de unicidad (email)
        Optional<Docente> docentePorEmail = docenteService.buscarPorEmail(docente.getEmail());
        if (docentePorEmail.isPresent()) {
            Docente otro = docentePorEmail.get();
            if (!esEdicion || !otro.getId().equals(docente.getId())) {
                result.rejectValue("email", "error.docente", "Ya existe un registro con este correo");
            }
        }

        // Validación manual de unicidad (cédula)
        Optional<Docente> docentePorCedula = docenteService.buscarOptionalCedula(docente.getCedula());
        if (docentePorCedula.isPresent()) {
            Docente otro = docentePorCedula.get();
            if (!esEdicion || !otro.getId().equals(docente.getId())) {
                result.rejectValue("cedula", "error.docente", "Ya existe un registro con esta cédula");
            }
        }

        // Verificamos errores después de validaciones manuales
        if (result.hasErrors()) {
            model.addAttribute("docente", docente);
            return "pages/Admin/docenteForm";
        }

        try {
            docenteService.guardarDocente(docente);
        } catch (DataIntegrityViolationException ex) {
            result.reject("error.general", "Ya existe un USUARIO con este correo o cédula.");
            model.addAttribute("docente", docente);
            return "pages/Admin/docenteForm";
        }

        redirectAttrs.addFlashAttribute("success", "Docente guardado correctamente.");
        return "redirect:/pages/Admin/docenteVista";
    }



    //4. Actualizar Docente
    @GetMapping("/editarDocente/{id}")
    public String actualizarDocente(@PathVariable Long id, Model model){
        Optional<Docente> docenteOpt  = docenteService.buscarDocenteId(id);

        if (docenteOpt.isPresent()) {
            model.addAttribute("docente", docenteOpt.get());
        } else {
            // Manejo cuando no encuentra docente
            return "redirect:/pages/Admin/docenteVista";
        }
        return "pages/Admin/docenteForm";
    }

    //5. Eliminar Docente
    @GetMapping("/eliminarDocente/{id}")
    public String eliminarDocente(@PathVariable Long id){
        docenteService.eliminarDocente(id);
        return "redirect:/pages/Admin/docenteVista";
    }

}
