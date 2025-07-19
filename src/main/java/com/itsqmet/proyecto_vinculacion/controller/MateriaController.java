package com.itsqmet.proyecto_vinculacion.controller;

import com.itsqmet.proyecto_vinculacion.entity.Materia;
import com.itsqmet.proyecto_vinculacion.repository.MateriaRepository;
import com.itsqmet.proyecto_vinculacion.service.MateriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class MateriaController {


    @Autowired
    private MateriaService materiaService;

    @Autowired
    private MateriaRepository materiaRepository;

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
