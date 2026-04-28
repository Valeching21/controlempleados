package com.proyecto.controlempleados.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.proyecto.controlempleados.model.Usuario;
import com.proyecto.controlempleados.service.UsuarioService;
import com.proyecto.controlempleados.Repository.UsuarioRepository;

/**
 * Controlador encargado de la gestión de usuarios (solo ADMIN).
 * 
 * Funcionalidades:
 * - Listar usuarios
 * - Crear nuevos usuarios
 * - Editar usuarios existentes
 * - Eliminar usuarios
 * 
 * Aplica validaciones a través del servicio para evitar duplicados
 * de cédula y username.
 */
@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioController {

    // Servicio que contiene la lógica de negocio de usuarios
    private final UsuarioService usuarioService;

    // Repositorio para consultas directas
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository){
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Muestra la lista de todos los usuarios registrados
     */
    @GetMapping
    public String listarUsuarios(Model model){
        model.addAttribute("usuarios", usuarioService.listar());
        return "usuarios/lista";
    }

    /**
     * Muestra el formulario para crear un nuevo usuario
     */
    @GetMapping("/nuevo")
    public String nuevoUsuario(Model model){
        model.addAttribute("usuario", new Usuario());
        return "usuarios/form";
    }

    /**
     * Guarda un nuevo usuario en el sistema.
     * 
     * La lógica de validación y encriptación se maneja en el servicio.
     */
    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario){

        usuarioService.crearUsuario(
                usuario.getCedula(),
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getRol()
        );

        return "redirect:/admin/usuarios";
    }

    /**
     * Carga los datos de un usuario para edición
     */
    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model){

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario inválido"));

        model.addAttribute("usuario", usuario);

        return "usuarios/form";
    }

    /**
     * Elimina un usuario por su ID
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id){

        usuarioRepository.deleteById(id);

        return "redirect:/admin/usuarios";
    }
}