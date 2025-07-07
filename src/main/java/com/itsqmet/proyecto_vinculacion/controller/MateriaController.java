package com.itsqmet.proyecto_vinculacion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MateriaController {

    //1.Ruta General

    @GetMapping("/pages/Admin/materiaVista")
    public String mostrarMateriaVista(){
        return "pages/Admin/materiaVista";
    }

    //2.Ruta Form

    @GetMapping("/pages/Admin/materiaForm")
    public String mostrarMateriaForm(){
        return "pages/Admin/materiaForm";
    }

}
