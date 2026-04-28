package com.proyecto.controlempleados.service;

import com.proyecto.controlempleados.model.Empleado;
import com.proyecto.controlempleados.Repository.EmpleadoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Servicio que contiene la lógica de negocio para la gestión de empleados.
 * 
 * Funcionalidades:
 * - Crear y actualizar empleados
 * - Listar empleados
 * - Buscar por ID
 * - Eliminar empleados
 * - Validar duplicados (cédula, correo, teléfono)
 * 
 * Regla importante:
 * - La cédula NO puede modificarse una vez creada
 */
@Service
public class EmpleadoService {

    private final EmpleadoRepository repo;

    public EmpleadoService(EmpleadoRepository repo) {
        this.repo = repo;
    }

    /**
     * Lista todos los empleados registrados
     */
    public List<Empleado> listar(){
        return repo.findAll();
    }

    /**
     * Crea un nuevo empleado o actualiza uno existente.
     * 
     * Si el empleado tiene ID:
     * - Se considera edición
     * - Se mantienen datos originales como la cédula
     * 
     * Si no tiene ID:
     * - Se crea un nuevo registro
     */
    public Empleado crear(Empleado e){

        // 🔥 MODO EDICIÓN
        if(e.getId() != null){

            Empleado existente = repo.findById(e.getId()).orElse(null);

            if(existente != null){

                // Guardar la cédula original (no se puede modificar)
                String cedulaOriginal = existente.getCedula();

                // Actualizar datos permitidos
                existente.setNombre(e.getNombre());
                existente.setApellidos(e.getApellidos());
                existente.setCorreo(e.getCorreo());
                existente.setTelefono(e.getTelefono());
                existente.setEdad(e.getEdad());
                existente.setPuesto(e.getPuesto());

                // Restaurar cédula original
                existente.setCedula(cedulaOriginal);

                return repo.save(existente);
            }
        }

        // 🔥 MODO CREACIÓN
        return repo.save(e);
    }

    /**
     * Busca un empleado por su ID
     */
    public Empleado buscarPorId(Long id){
        return repo.findById(id).orElse(null);
    }

    /**
     * Elimina un empleado por su ID
     */
    public void eliminar(Long id){
        repo.deleteById(id);
    }

    // ================= VALIDACIONES =================

    /**
     * Verifica si existe una cédula duplicada
     */
    public boolean existeCedula(String cedula){
        return repo.existsByCedula(cedula);
    }

    /**
     * Verifica si existe un correo duplicado
     */
    public boolean existeCorreo(String correo){
        return repo.existsByCorreo(correo);
    }

    /**
     * Verifica si existe un teléfono duplicado
     */
    public boolean existeTelefono(String telefono){
        return repo.existsByTelefono(telefono);
    }

    /**
     * Verifica duplicado de cédula excluyendo el mismo empleado (edición)
     */
    public boolean existeCedulaOtroEmpleado(String cedula, Long id){
        return repo.existsByCedulaAndIdNot(cedula, id);
    }

    /**
     * Verifica duplicado de correo excluyendo el mismo empleado
     */
    public boolean existeCorreoOtroEmpleado(String correo, Long id){
        return repo.existsByCorreoAndIdNot(correo, id);
    }

    /**
     * Verifica duplicado de teléfono excluyendo el mismo empleado
     */
    public boolean existeTelefonoOtroEmpleado(String telefono, Long id){
        return repo.existsByTelefonoAndIdNot(telefono, id);
    }
}