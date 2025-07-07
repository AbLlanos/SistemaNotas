package com.itsqmet.proyecto_vinculacion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PeriodoController {

    //1.Ruta General

    @GetMapping("/pages/Admin/periodoAcademicoVista")
    public String mostrarPeriodoAcademicoVista(){
        return "pages/Admin/periodoAcademicoVista";
    }

    //2.Ruta Form

    @GetMapping("/pages/Admin/periodoAcademicoForm")
    public String mostrarPeriodoAcademicoForm(){
        return "pages/Admin/periodoAcademicoForm";
    }

}
