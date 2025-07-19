package com.itsqmet.proyecto_vinculacion.controller;

import com.itsqmet.proyecto_vinculacion.entity.Trimestre;
import com.itsqmet.proyecto_vinculacion.service.TrimestreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/pages/Admin")
public class TrimestreController {

    @Autowired
    private TrimestreService trimestreService;

    //1.Ruta General

    @GetMapping("/trimestreVista")
    public String mostrarTrimestreVista(@RequestParam(name = "nombre", required = false) String nombre, Model model) {
        List<Trimestre> lista;
        if (nombre == null || nombre.trim().isEmpty()) {
            // Si no se busca nada, mostrar todos
            lista = trimestreService.listarTodosPeriodos();
        } else {
            // Si hay texto, buscar por nombre que contenga
            lista = trimestreService.buscarPorNombreContiene(nombre);
        }
        model.addAttribute("trimestres", lista);
        model.addAttribute("nombre", nombre); // Para recordar el texto en el input
        return "pages/Admin/trimestreVista";
    }

    //2.Ruta Form


    @GetMapping("/trimestreForm")
    public String mostrarFormularioTrimestre(@RequestParam(required = false) Long id, Model model) {
        Trimestre trimestre = (id != null) ? trimestreService.buscarTrimestrePorId(id) : new Trimestre();
        model.addAttribute("trimestre", trimestre);
        return "pages/Admin/trimestreForm";
    }


    @PostMapping("/guardarTrimestre")
    public String guardarTrimestre(@ModelAttribute("trimestre") Trimestre trimestre) {
        trimestreService.guardarTrimestre(trimestre);
        return "redirect:/pages/Admin/trimestreVista";
    }



    // EDITAR (redirige al formulario con el ID)
    @GetMapping("/editarTrimestre/{id}")
    public String editarTrimestre(@PathVariable Long id, Model model) {
        Trimestre trimestre = trimestreService.buscarTrimestrePorId(id);
        if (trimestre == null) {
            return "redirect:/pages/Admin/trimestreVista";
        }
        model.addAttribute("trimestre", trimestre);
        return "pages/Admin/trimestreForm";
    }

    // ELIMINAR
    @GetMapping("/eliminarTrimestre/{id}")
    public String eliminarTrimestre(@PathVariable Long id) {
        trimestreService.eliminar(id);
        return "redirect:/pages/Admin/trimestreVista";
    }

}
