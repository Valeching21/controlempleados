package com.proyecto.controlempleados.Init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.proyecto.controlempleados.model.Rol;
import com.proyecto.controlempleados.model.Usuario;
import com.proyecto.controlempleados.Repository.UsuarioRepository;

/**
 * Clase encargada de inicializar datos en la base de datos
 * al momento de iniciar la aplicación.
 * 
 * Funcionalidad:
 * - Crear un usuario administrador por defecto
 *   si no existen usuarios registrados.
 * 
 * Esto permite acceder al sistema sin necesidad
 * de registrar manualmente el primer usuario ADMIN.
 */
@Configuration
public class DataInitializer {

    /**
     * Se ejecuta automáticamente al iniciar la aplicación.
     * 
     * Verifica si la base de datos está vacía y, en ese caso,
     * crea un usuario administrador con credenciales por defecto.
     */
    @Bean
    CommandLineRunner initUsuarios(UsuarioRepository repo, PasswordEncoder encoder){

        return args -> {

            // Verifica si no existen usuarios registrados
            if(repo.count() == 0){

                // Creación de usuario administrador por defecto
                Usuario admin = new Usuario(
                        "111111111",
                        "Administrador",
                        "admin",
                        encoder.encode("admin123"), // contraseña encriptada
                        "admin@empresa.com",
                        Rol.ADMIN
                );

                // Guardar en base de datos
                repo.save(admin);
            }
        };
    }
}