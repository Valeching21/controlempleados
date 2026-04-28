package com.proyecto.controlempleados.service;

import com.proyecto.controlempleados.model.Empleado;
import com.proyecto.controlempleados.Repository.EmpleadoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EmpleadoService {

    private final EmpleadoRepository repo;

    public EmpleadoService(EmpleadoRepository repo) {
        this.repo = repo;
    }

    public List<Empleado> listar(){
        return repo.findAll();
    }

    public Empleado crear(Empleado e){

       
        if(e.getId() != null){

            Empleado existente = repo.findById(e.getId()).orElse(null);

            if(existente != null){

                
                String cedulaOriginal = existente.getCedula();

                
                existente.setNombre(e.getNombre());
                existente.setApellidos(e.getApellidos());
                existente.setCorreo(e.getCorreo());
                existente.setTelefono(e.getTelefono());
                existente.setEdad(e.getEdad());
                existente.setPuesto(e.getPuesto());

            
                existente.setCedula(cedulaOriginal);

                return repo.save(existente);
            }
        }

        return repo.save(e);
    }

    public Empleado buscarPorId(Long id){
        return repo.findById(id).orElse(null);
    }

    public void eliminar(Long id){
        repo.deleteById(id);
    }

   
    public boolean existeCedula(String cedula){
        return repo.existsByCedula(cedula);
    }

    public boolean existeCorreo(String correo){
        return repo.existsByCorreo(correo);
    }

    public boolean existeTelefono(String telefono){
        return repo.existsByTelefono(telefono);
    }

    

    public boolean existeCedulaOtroEmpleado(String cedula, Long id){
        return repo.existsByCedulaAndIdNot(cedula, id);
    }

    public boolean existeCorreoOtroEmpleado(String correo, Long id){
        return repo.existsByCorreoAndIdNot(correo, id);
    }

    public boolean existeTelefonoOtroEmpleado(String telefono, Long id){
        return repo.existsByTelefonoAndIdNot(telefono, id);
    }
}