package com.proyecto.controlempleados.Init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.proyecto.controlempleados.model.Rol;
import com.proyecto.controlempleados.model.Usuario;
import com.proyecto.controlempleados.Repository.UsuarioRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsuarios(UsuarioRepository repo, PasswordEncoder encoder){

        return args -> {

            if(repo.count()==0){

                Usuario admin = new Usuario(
                        "111111111",
                        "Administrador",
                        "admin",
                        encoder.encode("admin123"),
                        "admin@empresa.com",
                        Rol.ADMIN
                );

                repo.save(admin);

            }

        };
    }

}