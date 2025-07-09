package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.entity.Docente;
import com.itsqmet.proyecto_vinculacion.repository.DocenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocenteService {
    @Autowired
    private DocenteRepository docenteRepository;
    //cifrar la contraseña
    @Autowired
    private PasswordEncoder passwordEncoder;

    //Mostrar los docentes
    public List<Docente> mostrarDocente(){
        return docenteRepository.findAll();
    }

    //Buscar docente por ID
    public Optional<Docente> buscarDocenteId(Long id){
        return  docenteRepository.findById(id);
    }
    //Buscar docente por cedula
    public Optional<Docente> buscarDocenteCedula(String cedula){
        return docenteRepository.findByCedula(cedula);
    }
    //Guardar docente
    public  Docente guardarDocente(Docente docente){
        return docenteRepository.save(docente);
    }
    //Eliminar el docente
    public void eliminarDocente(Long id){
        docenteRepository.deleteById(id);
    }
}
