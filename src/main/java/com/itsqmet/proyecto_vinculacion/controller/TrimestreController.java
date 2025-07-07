package com.itsqmet.proyecto_vinculacion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TrimestreController {

    //1.Ruta General

    @GetMapping("/pages/Admin/trimestreVista")
    public String mostrarTrimestreVista(){
        return "pages/Admin/trimestreVista";
    }

    //2.Ruta Form

    @GetMapping("/pages/Admin/trimestreForm")
    public String mostrarTrimestreForm(){
        return "pages/Admin/trimestreForm";
    }

}
