package com.proyecto.controlempleados.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.proyecto.controlempleados.model.Usuario;
import com.proyecto.controlempleados.service.UsuarioService;
import com.proyecto.controlempleados.Repository.UsuarioRepository;

/**
 * Controlador encargado de la gestión de usuarios del sistema.
 * 
 * IMPORTANTE:
 * Este módulo está diseñado para ser utilizado únicamente por usuarios ADMIN,
 * lo cual se controla a nivel de seguridad.
 * 
 * Funcionalidades principales:
 * - Listar todos los usuarios
 * - Crear nuevos usuarios
 * - Editar usuarios existentes
 * - Eliminar usuarios
 * 
 * Este controlador NO contiene lógica de negocio compleja,
 * ya que esta se delega al servicio (UsuarioService),
 * siguiendo el patrón de arquitectura MVC.
 */
@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioController {

    /**
     * Servicio que contiene la lógica de negocio.
     * 
     * Aquí se manejan:
     * - Validaciones (cédula única, username único)
     * - Encriptación de contraseña (BCrypt)
     * - Creación de usuarios
     */
    private final UsuarioService usuarioService;

    /**
     * Repositorio utilizado para consultas directas a la base de datos.
     * 
     * Se usa principalmente en casos simples como:
     * - Buscar usuario por ID
     * - Eliminar usuario
     */
    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor con inyección de dependencias.
     * 
     * Spring se encarga de inyectar automáticamente las instancias necesarias.
     */
    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository){
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Muestra la lista completa de usuarios registrados en el sistema.
     * 
     * Flujo:
     * 1. Llama al servicio para obtener todos los usuarios
     * 2. Envía la lista a la vista mediante el modelo
     * 3. Retorna la vista "usuarios/lista"
     */
    @GetMapping
    public String listarUsuarios(Model model){

        // Obtener lista desde el servicio
        model.addAttribute("usuarios", usuarioService.listar());

        // Retornar vista Thymeleaf
        return "usuarios/lista";
    }

    /**
     * Muestra el formulario para registrar un nuevo usuario.
     * 
     * Se envía un objeto vacío para que Thymeleaf pueda mapear los campos.
     */
    @GetMapping("/nuevo")
    public String nuevoUsuario(Model model){

        // Objeto vacío para el formulario
        model.addAttribute("usuario", new Usuario());

        return "usuarios/form";
    }

    /**
     * Guarda un nuevo usuario en el sistema.
     * 
     * Funcionamiento:
     * 1. Recibe los datos del formulario
     * 2. Envía los datos al servicio
     * 3. El servicio valida:
     *    - Que la cédula no esté repetida
     *    - Que el username no esté repetido
     *    - Encripta la contraseña
     * 4. Guarda el usuario en la base de datos
     * 
     * NOTA:
     * No se maneja lógica aquí para mantener el controlador limpio.
     */
    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario){

        usuarioService.crearUsuario(
                usuario.getCedula(),
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getRol()
        );

        // Redirige a la lista después de guardar
        return "redirect:/admin/usuarios";
    }

    /**
     * Carga un usuario existente para editarlo.
     * 
     * Flujo:
     * 1. Recibe el ID desde la URL
     * 2. Busca el usuario en la base de datos
     * 3. Si no existe, lanza error
     * 4. Envía el usuario a la vista para editar
     */
    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model){

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario inválido"));

        model.addAttribute("usuario", usuario);

        return "usuarios/form";
    }

    /**
     * Elimina un usuario del sistema.
     * 
     * Funcionamiento:
     * - Recibe el ID desde la URL
     * - Elimina el registro directamente en la base de datos
     * - Redirige a la lista de usuarios después de eliminar
     * 
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id){

        usuarioRepository.deleteById(id);

        return "redirect:/admin/usuarios";
    }
}