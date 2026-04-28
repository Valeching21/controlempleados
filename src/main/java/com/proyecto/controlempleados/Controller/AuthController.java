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

//aqui esta el controlador para manejar el login, registro y perfil de usuario
@Controller
public class AuthController {

    // aqui se inyecta el repositorio para poder guardar y consultar los usuarios en la BD
    @Autowired
    private UsuarioRepository usuarioRepository;
    // aqui se inyecta el password encoder para encriptar las contraseñas antes de guardarlas en la BD
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    // aqui se definen las rutas para el login, registro y perfil de usuario
    @GetMapping("/")
    public String inicio(){
        return "redirect:/login";
    }
    //aqui es la ruta para mostrar el formulario de login
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    //aqui es la ruta para mostrar el formulario de registro
    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }
    //aqui es la ruta para el registro de un nuevo usuario, se recibe al Usuario con los datos del formulario, se encripta la contraseña, se asigna el rol de EMPLEADO y se guarda en la BD, para cambiar el rol a ADMIN se puede hacer manualmente en la BD 
    @PostMapping("/registro")
    public String guardarUsuario(Usuario usuario){

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRol(Rol.EMPLEADO);

        usuarioRepository.save(usuario);

        return "redirect:/login";
    }
    //aqui es la ruta para mostrar el perfil del usuario logueado
    @GetMapping("/home")
    public String home(Authentication auth, Model model){

        String username = auth.getName();

        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);

        model.addAttribute("usuario", usuario);

        return "home";
    }
    //aqui es la ruta para actualizar el perfil del usuario 
    public String actualizarPerfil(
            @RequestParam Long id,
            @RequestParam String nombre,
            @RequestParam String username,
            @RequestParam String correo,
            Authentication auth,
            Model model) {

        Usuario usuarioDB = usuarioRepository.findById(id).orElse(null);

        if (usuarioDB != null) {
            usuarioDB.setNombre(nombre);
            usuarioDB.setUsername(username);
            usuarioDB.setCorreo(correo);

            usuarioRepository.save(usuarioDB);

            Authentication nuevaAuth = new UsernamePasswordAuthenticationToken(
                    usuarioDB.getUsername(),
                    auth.getCredentials(),
                    auth.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(nuevaAuth);

            model.addAttribute("usuario", usuarioDB);
        }

        return "home";
    }
}