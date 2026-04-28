package com.proyecto.controlempleados.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Clase de configuración de seguridad del sistema de control de empleados.
 * 
 * Se encarga de:
 * - Definir las rutas públicas y protegidas
 * - Controlar el acceso según roles (ADMIN / EMPLEADO)
 * - Configurar el login personalizado
 * - Manejar el cierre de sesión
 * - Encriptar contraseñas con BCrypt
 */
@Configuration
public class SecurityConfig {

    /**
     * Bean encargado de encriptar las contraseñas usando BCrypt.
     * Esto evita almacenar contraseñas en texto plano.
     */
    @Bean 
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura las reglas de seguridad de la aplicación.
     * Define qué rutas son públicas, cuáles requieren autenticación
     * y cuáles están restringidas por rol.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
            .authorizeHttpRequests(auth -> auth

                // Rutas públicas accesibles sin autenticación
                .requestMatchers("/login","/registro","/css/**").permitAll()

                // Solo usuarios con rol ADMIN pueden gestionar empleados
                .requestMatchers("/empleados/**").hasRole("ADMIN")

                // Solo ADMIN puede generar reportes en PDF
                .requestMatchers("/horarios/reporte-pdf").hasRole("ADMIN")

                //Cualquier otra ruta requiere usuario autenticado
                .anyRequest().authenticated()
            )

            //Configuración del login personalizado
            .formLogin(login -> login
                    .loginPage("/login") // Vista personalizada
                    .defaultSuccessUrl("/home", true) // Redirección al iniciar sesión
                    .permitAll()
            )

            //Configuración del logout
            .logout(logout -> logout
                    .logoutSuccessUrl("/login") // Redirige al login al cerrar sesión
                    .permitAll()
            );

        return http.build();
    }
}