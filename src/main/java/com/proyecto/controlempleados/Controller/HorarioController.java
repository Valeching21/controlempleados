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


@Controller
@RequestMapping("/horarios")
public class HorarioController {

    private final HorarioService service;
    private final UsuarioRepository usuarioRepo;

    public HorarioController(HorarioService service, UsuarioRepository usuarioRepo) {
        this.service = service;
        this.usuarioRepo = usuarioRepo;
    }

    
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

        
        String estado = service.estadoUsuario(usuario);
        model.addAttribute("estado", estado);

      
        boolean enLinea = estado.equals("En línea");
        model.addAttribute("enLinea", enLinea);

        return "horarios";
    }


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

    @GetMapping("/reporte-pdf")
public void generarReportePDF(HttpServletResponse response) throws Exception {

    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "attachment; filename=reporte_horarios.pdf");

    List<Horario> lista = service.listarTodos();

    PdfWriter writer = new PdfWriter(response.getOutputStream());
    PdfDocument pdf = new PdfDocument(writer);
    Document document = new Document(pdf);

    // Título
    document.add(new Paragraph("REPORTE DE HORARIOS").setBold().setFontSize(16));

    // Tabla
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

        long horas = 0;
        if (h.getHoraEntrada() != null && h.getHoraSalida() != null) {
            Duration duracion = Duration.between(h.getHoraEntrada(), h.getHoraSalida());
            horas = duracion.toHours();
        }

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