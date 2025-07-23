package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.entity.Docente;
import com.itsqmet.proyecto_vinculacion.entity.Estudiante;
import com.itsqmet.proyecto_vinculacion.repository.DocenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocenteService {

    //APROBADO

    @Autowired
    private DocenteRepository docenteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //1. Mostrar todos
    public List<Docente> listarTodosDocentes(){
        return docenteRepository.findAll();
    }

    //2. Guardar
    public  Docente guardarDocente(Docente docente){
        return docenteRepository.save(docente);
    }

    //3. Eliminar por ID
    public void eliminarDocente(Long id){
        docenteRepository.deleteById(id);
    }

    //4. Buscar por ID
    public Optional<Docente> buscarDocenteId(Long id){
        return  docenteRepository.findById(id);
    }

    // 5. Consultas adicionales

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

    public Optional<Docente> buscarPorEmail(String email) {
        return docenteRepository.findByEmail(email);
    }

    public Optional<Docente> buscarOptionalCedula(String cedula) {
        return docenteRepository.findByCedula(cedula);
    }

}
