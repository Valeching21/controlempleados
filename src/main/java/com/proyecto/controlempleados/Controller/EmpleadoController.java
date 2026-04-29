package com.proyecto.controlempleados.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.proyecto.controlempleados.model.Empleado;
import com.proyecto.controlempleados.service.EmpleadoService;

/**
 * Controlador encargado de la gestión de empleados.
 * 
 * Funcionalidades:
 * - Listar empleados
 * - Crear nuevos empleados
 * - Editar empleados existentes
 * - Eliminar empleados
 * 
 * Incluye validaciones para evitar duplicados de cédula, correo y teléfono.
 */
@Controller
@RequestMapping("/empleados")
public class EmpleadoController {

    // Servicio que contiene la lógica de negocio de empleados
    private final EmpleadoService empleadoService;

    // Inyección por constructor (buena práctica)
    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    /**
     * Muestra la lista de todos los empleados registrados
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("empleados", empleadoService.listar());
        return "empleados";
    }

    /**
     * Muestra el formulario para crear un nuevo empleado
     */
    @GetMapping("/guardar")
    public String mostrarFormularioCreacion(Model model) {
        model.addAttribute("empleado", new Empleado());
        return "empleado-form";
    }

    /**
     * Guarda un empleado nuevo o actualiza uno existente.
     * 
     * Validaciones:
     * - No permitir cédula duplicada
     * - No permitir correo duplicado
     * - No permitir teléfono duplicado
     * 
     * Se diferencia entre creación y edición mediante el ID.
     */
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("empleado") Empleado empleado,
                          BindingResult br,
                          Model model) {

        // Limpiar espacios en blanco
        empleado.setCedula(empleado.getCedula().trim());
        empleado.setCorreo(empleado.getCorreo().trim());
        empleado.setTelefono(empleado.getTelefono().trim());

        if (br.hasErrors()) {
            return "empleado-form";
        }

        //VALIDACIÓN PARA EDICIÓN
        if (empleado.getId() != null) {

            // Verifica duplicados excluyendo el mismo registro
            if (empleadoService.existeCedulaOtroEmpleado(empleado.getCedula(), empleado.getId())) {
                model.addAttribute("error", "Esa cédula ya existe");
                return "empleado-form";
            }
            // Verifica duplicados excluyendo el mismo registro
            if (empleadoService.existeCorreoOtroEmpleado(empleado.getCorreo(), empleado.getId())) {
                model.addAttribute("error", "Ese correo ya existe");
                return "empleado-form";
            }
            // Verifica duplicados excluyendo el mismo registro
            if (empleadoService.existeTelefonoOtroEmpleado(empleado.getTelefono(), empleado.getId())) {
                model.addAttribute("error", "Ese teléfono ya existe");
                return "empleado-form";
            }

        } 
        //VALIDACIÓN PARA CREACIÓN 
        else {

            if (empleadoService.existeCedula(empleado.getCedula())) {
                model.addAttribute("error", "Esa cédula ya existe");
                return "empleado-form";
            }

            if (empleadoService.existeCorreo(empleado.getCorreo())) {
                model.addAttribute("error", "Ese correo ya existe");
                return "empleado-form";
            }

            if (empleadoService.existeTelefono(empleado.getTelefono())) {
                model.addAttribute("error", "Ese teléfono ya existe");
                return "empleado-form";
            }
        }

        // Guardar empleado en base de datos
        empleadoService.crear(empleado);

        return "redirect:/empleados";
    }

    /**
     * Carga los datos de un empleado para edición
     * Funcionamiento:
     * - Recibe el ID desde la URL
     * - Busca el empleado en la base de datos
     * - Si no existe, redirige a la lista de empleados
     * - Si existe, envía los datos a la vista para editar
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        Empleado empleado = empleadoService.buscarPorId(id);

        // Si no existe, redirige a la lista
        if (empleado == null) {
            return "redirect:/empleados";
        }

        model.addAttribute("empleado", empleado);

        return "empleado-form";
    }

    /**
     * Elimina un empleado por su ID
     * Funcionamiento:
     * - Recibe el ID desde la URL
     * - Elimina el registro directamente en la base de datos
     * - Redirige a la lista de empleados después de eliminar
     */
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {

        empleadoService.eliminar(id);

        return "redirect:/empleados";
    }
}