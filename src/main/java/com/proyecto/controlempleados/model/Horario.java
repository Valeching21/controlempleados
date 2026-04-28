package com.proyecto.controlempleados.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa un registro de horario de un usuario.
 * 
 * Cada registro contiene:
 * - Hora de entrada
 * - Hora de salida
 * - Usuario asociado
 * 
 * Relación:
 * - Muchos horarios pueden pertenecer a un mismo usuario (ManyToOne)
 */
@Entity
@Table(name = "horarios")
public class Horario {

    /**
     * Identificador único del horario (clave primaria)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Fecha y hora en la que el usuario registra su entrada
     */
    @Column(nullable = false)
    private LocalDateTime horaEntrada;

    /**
     * Fecha y hora en la que el usuario registra su salida
     * Puede ser null mientras el usuario esté activo (en línea)
     */
    private LocalDateTime horaSalida;

    /**
     * Usuario al que pertenece este registro de horario
     * Relación ManyToOne: muchos horarios → un usuario
     */
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Constructor vacío requerido por JPA
    public Horario() {}

    // Getters y setters

    public Long getId() {
        return id;
    }

    public LocalDateTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalDateTime horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public LocalDateTime getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(LocalDateTime horaSalida) {
        this.horaSalida = horaSalida;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}