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


    public boolean cedulaExiste(String cedula) {
        return usuarioRepository.existsByCedula(cedula);
    }

    public boolean emailExiste(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public Usuario guardarUsuario(Usuario usuario) throws Exception {
        if (cedulaExiste(usuario.getCedula())) {
            throw new Exception("La cédula ya está registrada.");
        }
        if (emailExiste(usuario.getEmail())) {
            throw new Exception("El email ya está registrado.");
        }
        return usuarioRepository.save(usuario);
    }

    public boolean existePorEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public boolean existePorCedula(String cedula) {
        return usuarioRepository.existsByCedula(cedula);
    }


}
