package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.entity.Admin;
import com.itsqmet.proyecto_vinculacion.entity.Curso;
import com.itsqmet.proyecto_vinculacion.entity.Estudiante;
import com.itsqmet.proyecto_vinculacion.entity.Materia;
import com.itsqmet.proyecto_vinculacion.repository.AdminRepository;
import com.itsqmet.proyecto_vinculacion.repository.CursoRepository;
import com.itsqmet.proyecto_vinculacion.repository.EstudianteRepository;
import com.itsqmet.proyecto_vinculacion.repository.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {


    @Autowired
    private AdminRepository adminRepository;


    public List<Admin> buscarPorNombreYCedula(String nombre, String cedula) {
        return adminRepository.findByNombreContainingIgnoreCaseAndCedulaContainingIgnoreCase(nombre, cedula);
    }

}

