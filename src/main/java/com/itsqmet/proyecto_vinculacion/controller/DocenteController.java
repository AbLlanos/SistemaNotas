package com.itsqmet.proyecto_vinculacion.controller;

import com.itsqmet.proyecto_vinculacion.entity.Docente;
import com.itsqmet.proyecto_vinculacion.service.DocenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class DocenteController {

    @Autowired
    private DocenteService docenteService;

    @GetMapping("/pages/Admin/docenteVista")
    public String mostrarDocenteVista(
            Model model,
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "cedula", required = false) String cedula) {

        List<Docente> docentes;

        if ((nombre == null || nombre.isEmpty()) && (cedula == null || cedula.isEmpty())) {
            docentes = docenteService.mostrarDocente();
        } else if (nombre != null && !nombre.isEmpty() && (cedula == null || cedula.isEmpty())) {
            docentes = docenteService.buscarPorNombre(nombre);
        } else if ((nombre == null || nombre.isEmpty()) && cedula != null && !cedula.isEmpty()) {
            docentes = docenteService.buscarPorCedula(cedula);
        } else {
            docentes = docenteService.buscarPorNombreYCedula(nombre, cedula);
        }

        model.addAttribute("docentes", docentes);
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
    public String guardarDocente(Docente docente){
        docente.setRol("DOCENTE");
        docenteService.guardarDocente(docente);
        return "redirect:/pages/Admin/docenteVista";
    }

    //4. Actualizar Docente
    @GetMapping("/editarDocente/{id}")
    public String actualizarDocente(@PathVariable Long id, Model model){
        Optional<Docente> docente  = docenteService.buscarDocenteId(id);
        model.addAttribute("docente",docente);
        return "pages/Admin/docenteForm";
    }

    //5. Eliminar Docente
    @GetMapping("/eliminarDocente/{id}")
    public String eliminarDocente(@PathVariable Long id){
        docenteService.eliminarDocente(id);
        return "redirect:/pages/Admin/docenteVista";
    }

}
