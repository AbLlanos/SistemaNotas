package com.itsqmet.proyecto_vinculacion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotasController {

    //1.Ruta Form -- Engloba asistencia -- posibles sudivisiones

    @GetMapping("/pages/Admin/notasForm")
    public String mostrarNivelEducativoForm(){
        return "pages/Admin/notasForm";
    }

}
