package com.itsqmet.proyecto_vinculacion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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

}
