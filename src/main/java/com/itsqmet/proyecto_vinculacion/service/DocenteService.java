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

    //Guardar docente
    public  Docente guardarDocente(Docente docente){
        return docenteRepository.save(docente);
    }

    //Eliminar el docente
    public void eliminarDocente(Long id){
        docenteRepository.deleteById(id);
    }

    //Buscar docente por ID
    public Optional<Docente> buscarDocenteId(Long id){
        return  docenteRepository.findById(id);
    }

    //Buscar docente por Nombre
    public List<Docente> buscarPorNombre(String nombre) {
        return docenteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    //Buscar docente por Cédula
    public List<Docente> buscarPorCedula(String cedula) {
        return docenteRepository.findByCedulaContainingIgnoreCase(cedula);
    }

    //Buscar docente con dos filtros
    public List<Docente> buscarPorNombreYCedula(String nombre, String cedula) {
        return docenteRepository.findByNombreContainingIgnoreCaseAndCedulaContainingIgnoreCase(nombre, cedula);
    }


}
