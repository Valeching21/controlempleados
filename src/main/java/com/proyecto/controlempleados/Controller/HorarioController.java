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
 * Este controlador maneja todas las acciones relacionadas con:
 * - Visualización de horarios
 * - Registro de entrada
 * - Registro de salida
 * - Generación de reportes en PDF
 * 
 * Además, trabaja en conjunto con Spring Security para identificar
 * al usuario autenticado y aplicar reglas según su rol.
 */
@Controller
@RequestMapping("/horarios")
public class HorarioController {

    /**
     * Servicio que contiene toda la lógica de negocio.
     * Aquí se realizan validaciones importantes como:
     * - No permitir doble entrada
     * - No permitir salida sin entrada
     */
    private final HorarioService service;

    /**
     * Repositorio para acceder a la base de datos y obtener
     * información del usuario autenticado.
     */
    private final UsuarioRepository usuarioRepo;

    /**
     * Constructor con inyección de dependencias.
     * Spring automáticamente inyecta el servicio y el repositorio.
     */
    public HorarioController(HorarioService service, UsuarioRepository usuarioRepo) {
        this.service = service;
        this.usuarioRepo = usuarioRepo;
    }

    /**
     * Método para visualizar los horarios.
     * 
     * Funcionamiento:
     * 1. Obtiene el usuario autenticado (por username)
     * 2. Verifica el rol del usuario
     * 3. Si es ADMIN -> muestra todos los horarios
     * 4. Si es EMPLEADO -> muestra solo sus registros
     * 
     * También calcula el estado del usuario:
     * - En línea: si tiene entrada sin salida
     * - Fuera de línea: si ya marcó salida
     */
    @GetMapping
    public String verHorarios(Authentication auth, Model model) {

        // Obtener el usuario autenticado desde la base de datos
        Usuario usuario = usuarioRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Horario> horarios;

        // Control de acceso según rol
        if (usuario.getRol().name().equals("ADMIN")) {
            // ADMIN puede ver todos los registros del sistema
            horarios = service.listarTodos();
        } else {
            // EMPLEADO solo puede ver sus propios horarios
            horarios = service.listarPorUsuario(usuario);
        }

        // Enviar lista de horarios a la vista
        model.addAttribute("horarios", horarios);

        // Obtener estado actual del usuario (En línea / Fuera de línea)
        String estado = service.estadoUsuario(usuario);
        model.addAttribute("estado", estado);

        // Variable booleana para usar en Thymeleaf (colores, etiquetas, etc.)
        model.addAttribute("enLinea", estado.equals("En línea"));

        return "horarios";
    }

    /**
     * Método para registrar la entrada del usuario.
     * 
     * Validación importante:
     * - No permite registrar entrada si ya existe una entrada activa
     *   (es decir, sin salida).
     * 
     * Manejo de errores:
     * - Se captura la excepción y se envía un mensaje a la vista.
     */
    @PostMapping("/entrada")
    public String entrada(Authentication auth, RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = usuarioRepo.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Llamado al servicio que contiene la lógica de validación
            service.registrarEntrada(usuario);

            redirectAttributes.addFlashAttribute("success", "Entrada registrada correctamente");

        } catch (Exception e) {
            // Mensaje de error si incumple reglas de negocio
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/horarios";
    }

    /**
     * Método para registrar la salida del usuario.
     * 
     * Validación importante:
     * - No permite registrar salida si no existe una entrada previa.
     * 
     * Esto evita inconsistencias en los datos.
     */
    @PostMapping("/salida")
    public String salida(Authentication auth, RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = usuarioRepo.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Lógica para cerrar el registro de horario activo
            service.registrarSalida(usuario);

            redirectAttributes.addFlashAttribute("success", "Salida registrada correctamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/horarios";
    }

    /**
     * Método para generar un reporte en PDF.
     * 
     * Este reporte contiene:
     * - Usuario
     * - Hora de entrada
     * - Hora de salida
     * - Horas trabajadas
     * - Estado del registro
     * 
     * Se utiliza la librería iText PDF para generar el documento.
     */
    @GetMapping("/reporte-pdf")
    public void generarReportePDF(HttpServletResponse response) throws Exception {
        
        // Configuración del tipo de archivo (PDF)
        response.setContentType("application/pdf");

        // Indica al navegador que descargue el archivo
        response.setHeader("Content-Disposition", "attachment; filename=reporte_horarios.pdf");

        // Obtener todos los registros
        List<Horario> lista = service.listarTodos();

        // Crear el documento PDF
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Título del reporte
        document.add(new Paragraph("REPORTE DE HORARIOS")
                .setBold()
                .setFontSize(16));

        // Tabla con 5 columnas
        Table table = new Table(5);

        table.addCell("Usuario");
        table.addCell("Entrada");
        table.addCell("Salida");
        table.addCell("Horas");
        table.addCell("Estado");

        // Recorrer todos los horarios
        for (Horario h : lista) {

            String usuario = h.getUsuario().getUsername();
            String entrada = h.getHoraEntrada() != null ? h.getHoraEntrada().toString() : "";
            String salida = h.getHoraSalida() != null ? h.getHoraSalida().toString() : "";

            /**
             * Cálculo de horas trabajadas:
             * Se usa Duration para obtener la diferencia entre entrada y salida.
             */
            long horas = 0;
            if (h.getHoraEntrada() != null && h.getHoraSalida() != null) {
                Duration duracion = Duration.between(h.getHoraEntrada(), h.getHoraSalida());
                horas = duracion.toHours();
            }

            /**
             * Determinación del estado:
             * - En línea: tiene entrada pero no salida
             * - Fuera de línea: ya registró salida
             */
            String estado = (h.getHoraEntrada() != null && h.getHoraSalida() == null)
                    ? "En linea"
                    : "Fuera de linea";

            // Agregar datos a la tabla
            table.addCell(usuario);
            table.addCell(entrada);
            table.addCell(salida);
            table.addCell(String.valueOf(horas));
            table.addCell(estado);
        }

        // Agregar tabla al documento
        document.add(table);

        // Cerrar documento (muy importante)
        document.close();
    }
}