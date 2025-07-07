package com.itsqmet.proyecto_vinculacion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NivelEducativoController {

    //1.Ruta General

    @GetMapping("/pages/Admin/nivelEducativoVista")
    public String mostrarNivelEducativoVista(){
        return "pages/Admin/docenteVista";
    }

    //2.Ruta Form

    @GetMapping("/pages/Admin/nivelEducativoForm")
    public String mostrarNivelEducativoForm(){
        return "pages/Admin/nivelEducativoForm";
    }

}
