package com.proyecto.controlempleados.Repository;

import com.proyecto.controlempleados.model.Horario;
import com.proyecto.controlempleados.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Horario.
 * 
 * Proporciona operaciones CRUD básicas mediante JpaRepository
 * y métodos personalizados para la gestión de horarios.
 * 
 * Funcionalidades:
 * - Listar horarios por usuario
 * - Obtener el horario activo (sin salida) más reciente
 */
public interface HorarioRepository extends JpaRepository<Horario, Long> {

    /**
     * Obtiene todos los horarios asociados a un usuario específico
     */
    List<Horario> findByUsuario(Usuario usuario);

    /**
     * Busca el último horario activo del usuario (sin hora de salida),
     * ordenado por la hora de entrada más reciente.
     * 
     * Se utiliza para validar:
     * - Si el usuario ya tiene una entrada activa
     * - Evitar múltiples entradas sin salida
     */
    Optional<Horario> findTopByUsuarioAndHoraSalidaIsNullOrderByHoraEntradaDesc(Usuario usuario);
}