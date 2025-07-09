package com.itsqmet.proyecto_vinculacion.controller;

import com.itsqmet.proyecto_vinculacion.entity.Docente;
import com.itsqmet.proyecto_vinculacion.service.DocenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class DocenteController {
    //1.Ruta General
    @GetMapping("/pages/Admin/docenteVista")
    public String mostrarDocenteVista(){
        return "pages/Admin/docenteVista";
    }

    //2.Ruta Form
    @GetMapping("/pages/Admin/docenteForm")
    public String mostrarDocenteForm(){
        return "pages/Admin/docenteForm";
    }
    @Autowired
    private DocenteService docenteService;

    //listar docentes
    @GetMapping("/docentes")
    public String listarDocentes( Model model){
        model.addAttribute("docentes",docenteService.mostrarDocente());
        return "pages/Admin/docenteVista";
    }
    //Insertar un Nuevo Docente
    @GetMapping("/docenteForm")
    public String formularioDocente(Model model){
        model.addAttribute("docente", new Docente());
        return "pages/Admin/docenteForm";
    }
    @PostMapping("/guardarDocente")
    public String guardarDocente(Docente docente){
        docenteService.guardarDocente(docente);
        return "redirect:/docentes";
    }
    //Actualizar Docente
    @GetMapping("/editarDocente/{id}")
    public String actualizarDocente(@PathVariable Long id, Model model){
        Optional<Docente> docente  = docenteService.buscarDocenteId(id);
        model.addAttribute("docente",docente);
        return "pages/Admin/docenteForm";
    }
    //Eliminar Docente
    @GetMapping("/eliminarDocente/{id}")
    public String eliminarDocente(@PathVariable Long id){
        docenteService.eliminarDocente(id);
        return "redirect:/docentes";
    }

}
