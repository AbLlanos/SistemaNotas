package com.itsqmet.proyecto_vinculacion.service;

import com.itsqmet.proyecto_vinculacion.entity.Usuario;
import com.itsqmet.proyecto_vinculacion.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {
@Autowired
private UsuarioRepository usuarioRepository;

    //VALIDAR SI EXISTE POR CEDULA
    public Optional<Usuario> obtenerPorCedulaExacta(String cedula){
        return usuarioRepository.findByCedula(cedula);
    }
    //VALIDAR SI EXISTE PRO CORREO
    public Optional<Usuario> obtenerPorEmailExacto(String email){
        return usuarioRepository.findByEmail(email);
    }
}
