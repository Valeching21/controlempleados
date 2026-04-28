package com.proyecto.controlempleados.model;

import jakarta.persistence.*;

/**
 * Entidad que representa a un usuario del sistema.
 * 
 * Se mapea a la tabla "usuarios" en la base de datos.
 * 
 * Contiene:
 * - Datos personales
 * - Credenciales de acceso
 * - Rol del usuario
 * 
 * Restricciones:
 * - Cédula única
 * - Username único
 * - Todos los campos obligatorios
 * 
 * Seguridad:
 * - La contraseña se almacena encriptada
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    /**
     * Identificador único del usuario (clave primaria)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Cédula única del usuario
     */
    @Column(nullable = false, unique = true)
    private String cedula;

    /**
     * Nombre completo del usuario
     */
    @Column(nullable = false)
    private String nombre;

    /**
     * Nombre de usuario para login (único)
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Contraseña del usuario (almacenada encriptada con BCrypt)
     */
    @Column(nullable = false)
    private String password;

    /**
     * Correo electrónico del usuario
     */
    @Column(nullable = false, unique = true)
    private String correo;

    /**
     * Rol del usuario (ADMIN o EMPLEADO)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    // Constructor vacío requerido por JPA
    public Usuario() {}

    // Constructor completo
    public Usuario(String cedula, String nombre, String username, String password, String correo, Rol rol) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.username = username;
        this.password = password;
        this.correo = correo;
        this.rol = rol;
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}