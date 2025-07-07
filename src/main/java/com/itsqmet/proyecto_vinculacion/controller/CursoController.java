package com.itsqmet.proyecto_vinculacion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CursoController {

    //1.Ruta General

    @GetMapping("/pages/Admin/cursoVista")
    public String mostrarCursoVista(){
        return "pages/Admin/cursoVista";
    }

    //2.Ruta Form

    @GetMapping("/pages/Admin/cursoForm")
    public String mostrarCursoForm(){
        return "pages/Admin/cursoForm";
    }


}
