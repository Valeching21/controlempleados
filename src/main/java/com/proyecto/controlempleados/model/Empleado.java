package com.proyecto.controlempleados.model;

import jakarta.persistence.*;

/**
 * Entidad que representa a un empleado en el sistema.
 * 
 * Se mapea a la tabla "empleados" en la base de datos.
 * 
 * Incluye:
 * - Datos personales
 * - Datos de contacto
 * - Información laboral
 * 
 * Restricciones:
 * - Cédula, correo y teléfono son únicos
 * - Todos los campos son obligatorios (no nulos)
 */
@Entity
@Table(name = "empleados")
public class Empleado {

    /**
     * Identificador único del empleado (clave primaria)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del empleado
     */
    @Column(nullable = false)
    private String nombre;

    /**
     * Apellidos del empleado
     */
    @Column(nullable = false)
    private String apellidos;

    /**
     * Cédula única del empleado
     */
    @Column(nullable = false, unique = true)
    private String cedula;

    /**
     * Correo electrónico único
     */
    @Column(nullable = false, unique = true)
    private String correo;

    /**
     * Número de teléfono único
     */
    @Column(nullable = false, unique = true)
    private String telefono;

    /**
     * Edad del empleado
     */
    @Column(nullable = false)
    private Integer edad;

    /**
     * Puesto o cargo del empleado
     */
    @Column(nullable = false)
    private String puesto;

    // Constructor vacío requerido por JPA
    public Empleado() {}

    // Constructor con todos los campos
    public Empleado(String nombre, String apellidos, String cedula, String correo, String telefono, Integer edad, String puesto) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.cedula = cedula;
        this.correo = correo;
        this.telefono = telefono;
        this.edad = edad;
        this.puesto = puesto;
    }

    // Getters y setters para todos los campos

    public Long getId() {
        return id;
    }

    public void setId(Long id) { 
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }
}