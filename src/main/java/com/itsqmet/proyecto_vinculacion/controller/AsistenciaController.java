package com.itsqmet.proyecto_vinculacion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AsistenciaController {


    //1.Ruta Form

    @GetMapping("/pages/Admin/asistenciaForm")
    public String mostrarAsistenciaForm(){
        return "pages/Admin/asistenciaForm";
    }

}
