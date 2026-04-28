package com.proyecto.controlempleados.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.proyecto.controlempleados.model.Empleado;

/**
 * Repositorio para la entidad Empleado.
 * 
 * Extiende JpaRepository para proporcionar operaciones CRUD básicas
 * y define métodos personalizados para validaciones de unicidad.
 * 
 * Permite verificar:
 * - Cédula duplicada
 * - Correo duplicado
 * - Teléfono duplicado
 * 
 * También incluye validaciones para edición, excluyendo el mismo registro.
 */
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    /**
     * Verifica si ya existe un empleado con la misma cédula
     */
    boolean existsByCedula(String cedula);

    /**
     * Verifica si ya existe un empleado con el mismo correo
     */
    boolean existsByCorreo(String correo);

    /**
     * Verifica si ya existe un empleado con el mismo teléfono
     */
    boolean existsByTelefono(String telefono);

    /**
     * Verifica duplicado de cédula excluyendo el empleado actual (para edición)
     */
    boolean existsByCedulaAndIdNot(String cedula, Long id);

    /**
     * Verifica duplicado de correo excluyendo el empleado actual
     */
    boolean existsByCorreoAndIdNot(String correo, Long id);

    /**
     * Verifica duplicado de teléfono excluyendo el empleado actual
     */
    boolean existsByTelefonoAndIdNot(String telefono, Long id);

}