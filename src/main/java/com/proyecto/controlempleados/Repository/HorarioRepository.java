package com.proyecto.controlempleados.Repository;

import com.proyecto.controlempleados.model.Horario;
import com.proyecto.controlempleados.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HorarioRepository extends JpaRepository<Horario, Long> {

    List<Horario> findByUsuario(Usuario usuario);

    Optional<Horario> findTopByUsuarioAndHoraSalidaIsNullOrderByHoraEntradaDesc(Usuario usuario);
}
