package com.proyecto.controlempleados.model;

/**
 * Enumeración que define los roles de usuario en el sistema.
 * 
 * Se utiliza para controlar el acceso a las funcionalidades
 * mediante Spring Security.
 * 
 * Roles disponibles:
 * - ADMIN: acceso completo al sistema (gestión de empleados, usuarios, reportes)
 * - EMPLEADO: acceso limitado (registro de horarios y visualización de su información)
 */
public enum Rol {

    ADMIN,
    EMPLEADO

}