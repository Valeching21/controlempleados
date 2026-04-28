package com.proyecto.controlempleados.service;

import com.proyecto.controlempleados.model.Usuario;
import com.proyecto.controlempleados.Repository.UsuarioRepository;

import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio personalizado que implementa UserDetailsService.
 * 
 * Se encarga de:
 * - Cargar un usuario desde la base de datos
 * - Convertirlo en un objeto UserDetails
 * - Asignar sus roles como autoridades de Spring Security
 * 
 * Este servicio es utilizado automáticamente por Spring Security
 * durante el proceso de autenticación (login).
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Carga un usuario por su username desde la base de datos.
     * 
     * Si el usuario existe:
     * - Retorna un objeto UserDetails con username, password y roles
     * 
     * Si no existe:
     * - Lanza excepción UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                List.of(
                        // Se asigna el rol con el formato requerido por Spring Security
                        new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())
                )
        );
    }
}