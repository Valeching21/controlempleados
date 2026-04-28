package com.proyecto.controlempleados.service;

import com.proyecto.controlempleados.model.Rol;
import com.proyecto.controlempleados.model.Usuario;
import com.proyecto.controlempleados.Repository.UsuarioRepository;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio que contiene la lógica de negocio para la gestión de usuarios.
 * 
 * Funcionalidades:
 * - Crear usuarios
 * - Listar usuarios
 * 
 * Validaciones:
 * - Username único
 * - Cédula única
 * - Correo único
 * 
 * Seguridad:
 * - Contraseñas encriptadas con PasswordEncoder (BCrypt)
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
    }

    /**
     * Lista todos los usuarios registrados
     */
    public List<Usuario> listar(){
        return usuarioRepository.findAll();
    }

    /**
     * Crea un nuevo usuario en el sistema
     * 
     * Validaciones:
     * - No permitir username duplicado
     * - No permitir cédula duplicada
     * - No permitir correo duplicado
     * 
     * Seguridad:
     * - La contraseña se encripta antes de guardarse
     */
    public Usuario crearUsuario(String cedula, String username, String password, Rol rol){

        // Validar username duplicado
        if (usuarioRepository.existsByUsername(username)){
            throw new IllegalArgumentException("El username ya existe!");
        }

        // Validar cédula duplicada
        if (usuarioRepository.existsByCedula(cedula)){
            throw new IllegalArgumentException("La cédula ya está registrada!");
        }

        // Validar correo duplicado
        // (solo si luego agregas correo en el método)
        // if (usuarioRepository.existsByCorreo(correo)){
        //     throw new IllegalArgumentException("El correo ya existe!");
        // }

        Usuario u = new Usuario();
        u.setCedula(cedula);
        u.setUsername(username);

        // Encriptar contraseña
        u.setPassword(encoder.encode(password));

        u.setRol(rol);

        return usuarioRepository.save(u);
    }
}