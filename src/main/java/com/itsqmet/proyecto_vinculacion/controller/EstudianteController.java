package com.itsqmet.proyecto_vinculacion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EstudianteController {

    //1.Ruta General

    @GetMapping("/pages/Admin/estudianteVista")
    public String mostrarEstudianteVista(){
        return "pages/Admin/estudianteVista";
    }

    //2.Ruta Form

    @GetMapping("/pages/Admin/estudianteForm")
    public String mostrarEstudianteForm(){
        return "pages/Admin/estudianteForm";
    }


}
