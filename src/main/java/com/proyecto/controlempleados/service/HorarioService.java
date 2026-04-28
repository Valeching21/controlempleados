package com.proyecto.controlempleados.service;

import com.proyecto.controlempleados.model.Horario;
import com.proyecto.controlempleados.model.Usuario;
import com.proyecto.controlempleados.Repository.HorarioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class HorarioService {

    private final HorarioRepository repo;

    public HorarioService(HorarioRepository repo) {
        this.repo = repo;
    }

    
    public List<Horario> listarTodos() {
        return repo.findAll();
    }

    
    public List<Horario> listarPorUsuario(Usuario usuario) {
        return repo.findByUsuario(usuario);
    }

    
    public Horario registrarEntrada(Usuario usuario) {

        Optional<Horario> activoOpt =
                repo.findTopByUsuarioAndHoraSalidaIsNullOrderByHoraEntradaDesc(usuario);

        
        if (activoOpt.isPresent()) {
            throw new RuntimeException("⚠️ Ya registraste una entrada. Debes marcar salida antes de volver a ingresar.");
        }

        Horario h = new Horario();
        h.setUsuario(usuario);
        h.setHoraEntrada(LocalDateTime.now());

        return repo.save(h);
    }

  
    public Horario registrarSalida(Usuario usuario) {

        Optional<Horario> activoOpt =
                repo.findTopByUsuarioAndHoraSalidaIsNullOrderByHoraEntradaDesc(usuario);


        if (activoOpt.isEmpty()) {
            throw new RuntimeException("⚠️ No puedes registrar salida sin haber marcado una entrada primero.");
        }

        Horario activo = activoOpt.get();


        if (LocalDateTime.now().isBefore(activo.getHoraEntrada())) {
            throw new RuntimeException("⚠️ La hora de salida no puede ser antes de la entrada.");
        }

        activo.setHoraSalida(LocalDateTime.now());

        return repo.save(activo);
    }

    public String estadoUsuario(Usuario usuario) {

        Optional<Horario> activoOpt =
                repo.findTopByUsuarioAndHoraSalidaIsNullOrderByHoraEntradaDesc(usuario);

        return activoOpt.isPresent() ? "En línea" : "Fuera de línea";
    }
    public String estado(Horario h) {
        if (h.getHoraEntrada() != null && h.getHoraSalida() == null) {
            return "En línea";
        }
        return "Fuera de línea";
    }
}