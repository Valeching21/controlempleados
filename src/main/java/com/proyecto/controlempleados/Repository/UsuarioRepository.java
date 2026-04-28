package com.proyecto.controlempleados.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.controlempleados.model.Usuario;

/**
 * Repositorio para la entidad Usuario.
 * 
 * Extiende JpaRepository para proporcionar operaciones CRUD básicas
 * y define métodos personalizados para validaciones y autenticación.
 * 
 * Funcionalidades:
 * - Validar duplicados (username, cédula, correo)
 * - Buscar usuario por username (usado en login)
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

    /**
     * Verifica si ya existe un usuario con el mismo username
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si ya existe un usuario con la misma cédula
     */
    boolean existsByCedula(String cedula);

    /**
     * Verifica si ya existe un usuario con el mismo correo
     */
    boolean existsByCorreo(String correo);

    /**
     * Busca un usuario por su username
     * 
     * Este método es clave para la autenticación en Spring Security
     */
    Optional<Usuario> findByUsername(String username);
}