package com.proyecto.controlempleados.Controller;


import com.proyecto.controlempleados.model.Horario;
import com.proyecto.controlempleados.model.Usuario;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;
import com.proyecto.controlempleados.Repository.UsuarioRepository;
import com.proyecto.controlempleados.service.HorarioService;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Controlador encargado de la gestión de horarios de los empleados.
 * 
 * Funcionalidades:
 * - Visualizar horarios
 * - Registrar entrada
 * - Registrar salida
 * - Generar reporte en PDF
 * 
 * Aplica validaciones para evitar:
 * - Entradas duplicadas
 * - Salidas sin entrada previa
 */
@Controller
@RequestMapping("/horarios")
public class HorarioController {

    // Servicio con la lógica de negocio de horarios
    private final HorarioService service;

    // Repositorio para obtener el usuario autenticado
    private final UsuarioRepository usuarioRepo;

    public HorarioController(HorarioService service, UsuarioRepository usuarioRepo) {
        this.service = service;
        this.usuarioRepo = usuarioRepo;
    }

    /**
     * Muestra los horarios según el rol:
     * - ADMIN: ve todos los registros
     * - EMPLEADO: solo sus propios horarios
     */
    @GetMapping
    public String verHorarios(Authentication auth, Model model) {

        Usuario usuario = usuarioRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Horario> horarios;

        if (usuario.getRol().name().equals("ADMIN")) {
            horarios = service.listarTodos();
        } else {
            horarios = service.listarPorUsuario(usuario);
        }

        model.addAttribute("horarios", horarios);

        // Estado del usuario (En línea / Fuera de línea)
        String estado = service.estadoUsuario(usuario);
        model.addAttribute("estado", estado);

        model.addAttribute("enLinea", estado.equals("En línea"));

        return "horarios";
    }

    /**
     * Registra la hora de entrada del usuario autenticado.
     * Valida que no exista una entrada activa sin salida.
     */
    @PostMapping("/entrada")
    public String entrada(Authentication auth, RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = usuarioRepo.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            service.registrarEntrada(usuario);

            redirectAttributes.addFlashAttribute("success", "Entrada registrada correctamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/horarios";
    }

    /**
     * Registra la hora de salida del usuario autenticado.
     * Valida que exista una entrada previa.
     */
    @PostMapping("/salida")
    public String salida(Authentication auth, RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = usuarioRepo.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            service.registrarSalida(usuario);

            redirectAttributes.addFlashAttribute("success", "Salida registrada correctamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/horarios";
    }

    /**
     * Genera un reporte en PDF con todos los horarios del sistema.
     * Incluye:
     * - Usuario
     * - Hora de entrada
     * - Hora de salida
     * - Horas trabajadas
     * - Estado
     */
    @GetMapping("/reporte-pdf")
    public void generarReportePDF(HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_horarios.pdf");

        List<Horario> lista = service.listarTodos();

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("REPORTE DE HORARIOS")
                .setBold()
                .setFontSize(16));

        Table table = new Table(5);

        table.addCell("Usuario");
        table.addCell("Entrada");
        table.addCell("Salida");
        table.addCell("Horas");
        table.addCell("Estado");

        for (Horario h : lista) {

            String usuario = h.getUsuario().getUsername();
            String entrada = h.getHoraEntrada() != null ? h.getHoraEntrada().toString() : "";
            String salida = h.getHoraSalida() != null ? h.getHoraSalida().toString() : "";

            // Cálculo de horas trabajadas
            long horas = 0;
            if (h.getHoraEntrada() != null && h.getHoraSalida() != null) {
                Duration duracion = Duration.between(h.getHoraEntrada(), h.getHoraSalida());
                horas = duracion.toHours();
            }

            // Determinar estado
            String estado = (h.getHoraEntrada() != null && h.getHoraSalida() == null)
                    ? "En linea"
                    : "Fuera de linea";

            table.addCell(usuario);
            table.addCell(entrada);
            table.addCell(salida);
            table.addCell(String.valueOf(horas));
            table.addCell(estado);
        }

        document.add(table);
        document.close();
    }
}