package com.proyecto.controlempleados.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.proyecto.controlempleados.model.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    boolean existsByCedula(String cedula);

    boolean existsByCorreo(String correo);

    boolean existsByTelefono(String telefono);

    boolean existsByCedulaAndIdNot(String cedula, Long id);

    boolean existsByCorreoAndIdNot(String correo, Long id);

    boolean existsByTelefonoAndIdNot(String telefono, Long id);

}