package com.proyecto.controlempleados.service;

import com.proyecto.controlempleados.model.Horario;
import com.proyecto.controlempleados.model.Usuario;
import com.proyecto.controlempleados.Repository.HorarioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

/**
 * Servicio que contiene la lógica de negocio para la gestión de horarios.
 * 
 * Funcionalidades:
 * - Registrar entrada
 * - Registrar salida
 * - Listar horarios
 * - Obtener estado del usuario (En línea / Fuera de línea)
 * 
 * Validaciones:
 * - No permitir múltiples entradas sin salida
 * - No permitir salida sin entrada previa
 * - No permitir salida antes de la entrada
 */
@Service
public class HorarioService {

    private final HorarioRepository repo;

    public HorarioService(HorarioRepository repo) {
        this.repo = repo;
    }

    /**
     * Lista todos los horarios (usado por ADMIN)
     */
    public List<Horario> listarTodos() {
        return repo.findAll();
    }

    /**
     * Lista los horarios de un usuario específico
     */
    public List<Horario> listarPorUsuario(Usuario usuario) {
        return repo.findByUsuario(usuario);
    }

    /**
     * Registra la hora de entrada del usuario
     * 
     * Valida que no exista una entrada activa sin salida
     */
    public Horario registrarEntrada(Usuario usuario) {

        Optional<Horario> activoOpt =
                repo.findTopByUsuarioAndHoraSalidaIsNullOrderByHoraEntradaDesc(usuario);

        //No permitir múltiples entradas sin salida
        if (activoOpt.isPresent()) {
            throw new RuntimeException("⚠️ Ya registraste una entrada. Debes marcar salida antes de volver a ingresar.");
        }

        Horario h = new Horario();
        h.setUsuario(usuario);
        h.setHoraEntrada(LocalDateTime.now());

        return repo.save(h);
    }

    /**
     * Registra la hora de salida del usuario
     * 
     * Validaciones:
     * - Debe existir una entrada previa
     * - La salida no puede ser antes de la entrada
     */
    public Horario registrarSalida(Usuario usuario) {

        Optional<Horario> activoOpt =
                repo.findTopByUsuarioAndHoraSalidaIsNullOrderByHoraEntradaDesc(usuario);

        //No permitir salida sin entrada
        if (activoOpt.isEmpty()) {
            throw new RuntimeException("⚠️ No puedes registrar salida sin haber marcado una entrada primero.");
        }

        Horario activo = activoOpt.get();

        //Validar que la salida sea después de la entrada
        if (LocalDateTime.now().isBefore(activo.getHoraEntrada())) {
            throw new RuntimeException("⚠️ La hora de salida no puede ser antes de la entrada.");
        }

        activo.setHoraSalida(LocalDateTime.now());

        return repo.save(activo);
    }

    /**
     * Obtiene el estado actual del usuario
     * 
     * - En línea: tiene entrada sin salida
     * - Fuera de línea: no tiene entrada activa
     */
    public String estadoUsuario(Usuario usuario) {

        Optional<Horario> activoOpt =
                repo.findTopByUsuarioAndHoraSalidaIsNullOrderByHoraEntradaDesc(usuario);

        return activoOpt.isPresent() ? "En línea" : "Fuera de línea";
    }

    /**
     * Determina el estado de un registro específico
     */
    public String estado(Horario h) {
        if (h.getHoraEntrada() != null && h.getHoraSalida() == null) {
            return "En línea";
        }
        return "Fuera de línea";
    }
}