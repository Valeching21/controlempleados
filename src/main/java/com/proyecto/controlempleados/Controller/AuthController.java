package com.proyecto.controlempleados.Controller;

import com.proyecto.controlempleados.model.Rol;
import com.proyecto.controlempleados.model.Usuario;
import com.proyecto.controlempleados.Repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

/**
 * Controlador encargado de la autenticación y gestión del usuario.
 * 
 * Funcionalidades:
 * - Login
 * - Registro de usuarios
 * - Visualización del perfil
 * - Actualización de datos del usuario autenticado
 */
@Controller
public class AuthController {

    // Repositorio para acceder a los usuarios en la base de datos
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Encoder para encriptar contraseñas
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Redirige la raíz del sistema al login
     */
    @GetMapping("/")
    public String inicio(){
        return "redirect:/login";
    }

    /**
     * Muestra la vista de login
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Muestra el formulario de registro de usuarios
     */
    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    /**
     * Registra un nuevo usuario en el sistema
     * - Encripta la contraseña
     * - Asigna rol EMPLEADO por defecto
     */
    @PostMapping("/registro")
    public String guardarUsuario(Usuario usuario){

        // Encriptar contraseña antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Asignar rol por defecto
        usuario.setRol(Rol.EMPLEADO);

        // Guardar en base de datos
        usuarioRepository.save(usuario);

        return "redirect:/login";
    }

    /**
     * Muestra el perfil del usuario autenticado
     */
    @GetMapping("/home")
    public String home(Authentication auth, Model model){

        // Obtener username desde Spring Security
        String username = auth.getName();

        // Buscar usuario en base de datos
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);

        // Enviar datos a la vista
        model.addAttribute("usuario", usuario);

        return "home";
    }

    /**
     * Actualiza los datos del perfil del usuario autenticado
     * y refresca la sesión para reflejar los cambios inmediatamente
     */
    @PostMapping("/home")
    public String actualizarPerfil(
            @RequestParam Long id,
            @RequestParam String nombre,
            @RequestParam String username,
            @RequestParam String correo,
            Authentication auth,
            Model model) {

        Usuario usuarioDB = usuarioRepository.findById(id).orElse(null);

        if (usuarioDB != null) {

            // Actualizar datos básicos
            usuarioDB.setNombre(nombre);
            usuarioDB.setUsername(username);
            usuarioDB.setCorreo(correo);

            usuarioRepository.save(usuarioDB);

            //Refrescar autenticación para evitar logout
            Authentication nuevaAuth = new UsernamePasswordAuthenticationToken(
                    usuarioDB.getUsername(),
                    auth.getCredentials(),
                    auth.getAuthorities()
            );

            // Actualizar el contexto de seguridad con la nueva autenticación
            SecurityContextHolder.getContext().setAuthentication(nuevaAuth);

            model.addAttribute("usuario", usuarioDB);
        }

        return "home";
    }
}