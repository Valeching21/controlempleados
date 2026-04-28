package com.proyecto.controlempleados;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Spring Boot.
 * 
 * Función:
 * - Inicia la aplicación
 * - Carga el contexto de Spring
 * - Detecta automáticamente los componentes (@Controller, @Service, @Repository)
 * 
 * Esta clase es el punto de entrada del sistema de control de empleados.
 */
@SpringBootApplication
public class ControlempleadosApplication {

	public static void main(String[] args) {
		SpringApplication.run(ControlempleadosApplication.class, args);
	}

}