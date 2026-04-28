package com.proyecto.controlempleados.Config;

//Configuración de seguridad con Spring Security
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

//Clase de configuración de seguridad
@Configuration
public class SecurityConfig {

    //Encriptación de contraseñas
    @Bean 
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //Configuración de seguridad
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
            .authorizeHttpRequests(auth -> auth

                //Rutas públicas
                .requestMatchers("/login","/registro","/css/**").permitAll()

                //Solo ADMIN puede acceder a empleados
                .requestMatchers("/empleados/**").hasRole("ADMIN")

                //SOLO ADMIN puede generar reportes
                .requestMatchers("/horarios/reporte-pdf").hasRole("ADMIN")

                //Todo lo demás requiere login
                .anyRequest().authenticated()
            )

            //Login personalizado
            .formLogin(login -> login
                    .loginPage("/login")
                    .defaultSuccessUrl("/home", true)
                    .permitAll()
            )

            //Logout
            .logout(logout -> logout
                    .logoutSuccessUrl("/login")
                    .permitAll()
            );

        return http.build();
    }
}