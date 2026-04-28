package com.proyecto.controlempleados.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.controlempleados.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

    boolean existsByUsername(String username);

    boolean existsByCedula(String cedula);

    boolean existsByCorreo(String correo);

    Optional<Usuario> findByUsername(String username);
}