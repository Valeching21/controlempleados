package com.proyecto.controlempleados.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.proyecto.controlempleados.model.Usuario;
import com.proyecto.controlempleados.service.UsuarioService;
import com.proyecto.controlempleados.Repository.UsuarioRepository;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository){
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    // lista todos los usuarios, obtenemos la lista de usuarios desde el servicio y la pasamos al modelo para mostrarla en la vista
    @GetMapping
    public String listarUsuarios(Model model){
        model.addAttribute("usuarios", usuarioService.listar());
        return "usuarios/lista";
    }

    // formulario para crear un nuevo usuario, simplemente pasamos un nuevo objeto Usuario al formulario para que se pueda llenar
    @GetMapping("/nuevo")
    public String nuevoUsuario(Model model){
        model.addAttribute("usuario", new Usuario());
        return "usuarios/form";
    }

    // Para guardar un nuevo usuario, recibimos el objeto Usuario desde el formulario y lo pasamos al servicio para crear el usuario
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

    // Para poder editar un usuario, primero obtenemos el usuario por su ID y lo pasamos al formulario
    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model){

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario inválido"));

        model.addAttribute("usuario", usuario);

        return "usuarios/form";
    }

    // para eliminar un usuario, simplemente lo eliminamos por su ID y redirigimos a la lista de usuarios
    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id){

        usuarioRepository.deleteById(id);

        return "redirect:/admin/usuarios";
    }
}