package com.itsqmet.proyecto_vinculacion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {


    //Ruta para el index-login
    @GetMapping("/index")
    public String mostrarIndex() {
        return "index";
    }


}
