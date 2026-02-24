package com.utc.proyecto1.controller;

import com.utc.proyecto1.entity.Supervisa;
import com.utc.proyecto1.entity.Cargo;
import com.utc.proyecto1.repository.SupervisaRepository;
import com.utc.proyecto1.service.EmailService;
import com.utc.proyecto1.repository.CargoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/supervisiones")
public class SupervisaController {
    
    @Autowired
    private SupervisaRepository supervisaRepository;
    
    @Autowired
    private CargoRepository cargoRepository;
    
    @Autowired
    private EmailService emailService;
    
    // ============= LISTAR TODAS LAS SUPERVISIONES CON ESTADÍSTICAS =============
    @GetMapping
    public String listarSupervisiones(Model model) {
        // Obtener todas las supervisiones
        List<Supervisa> supervisiones = supervisaRepository.findAllByOrderByFechaCreadoSupDesc();
        model.addAttribute("supervisiones", supervisiones);
        
        // ===== CALCULAR ESTADÍSTICAS =====
        
        // 1. TOTAL SUPERVISIONES
        long totalSupervisiones = supervisiones.size();
        model.addAttribute("totalSupervisiones", totalSupervisiones);
        
        // 2. SUPERVISIONES DE HOY
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = LocalDate.now().plusDays(1).atStartOfDay();
        long supervisionesHoy = supervisaRepository.findByFechaCreadoSupBetween(inicioHoy, finHoy).size();
        model.addAttribute("supervisionesHoy", supervisionesHoy);
        
        // 3. SUPERVISIONES DE ESTA SEMANA
        LocalDateTime inicioSemana = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1).atStartOfDay();
        LocalDateTime finSemana = LocalDate.now().plusDays(7 - LocalDate.now().getDayOfWeek().getValue()).atStartOfDay();
        long supervisionesSemana = supervisaRepository.findByFechaCreadoSupBetween(inicioSemana, finSemana).size();
        model.addAttribute("supervisionesSemana", supervisionesSemana);
        
        // 4. CARGOS SUPERVISADOS (DISTINTOS)
        long cargosSupervisados = supervisiones.stream()
                .map(s -> s.getCargo() != null ? s.getCargo().getCodigoCar() : null)
                .filter(codigo -> codigo != null)
                .distinct()
                .count();
        model.addAttribute("cargosSupervisados", cargosSupervisados);
        
        return "supervisa/list";
    }
    
    // ============= VER DETALLE DE SUPERVISIÓN =============
    @GetMapping("/ver/{codigo}")
    public String verSupervision(@PathVariable Long codigo, Model model, RedirectAttributes redirectAttributes) {
        Optional<Supervisa> supervision = supervisaRepository.findById(codigo);
        if (supervision.isPresent()) {
            model.addAttribute("supervision", supervision.get());
            return "supervisa/view";
        } else {
            redirectAttributes.addFlashAttribute("error", "Supervisión no encontrada");
            return "redirect:/supervisiones";
        }
    }
    
    // ============= MOSTRAR FORMULARIO NUEVA SUPERVISIÓN =============
    @GetMapping("/nuevo")
    public String nuevaSupervision(Model model) {
        model.addAttribute("supervision", new Supervisa());
        model.addAttribute("cargos", cargoRepository.findAll());
        return "supervisa/form";
    }
    
    // ============= GUARDAR NUEVA SUPERVISIÓN (CON EMAIL) =============
    @PostMapping("/guardar")
    public String guardarSupervision(@ModelAttribute Supervisa supervision, 
                                     @RequestParam(required = false) Long cargoId,
                                     @RequestParam(required = false) String emailSup,
                                     RedirectAttributes redirectAttributes) {
        try {
            // Validar que el código no exista
            if (supervision.getCodigoSup() != null && supervisaRepository.existsById(supervision.getCodigoSup())) {
                redirectAttributes.addFlashAttribute("error", "Ya existe una supervisión con ese código");
                return "redirect:/supervisiones/nuevo";
            }
            
            // Validar email si se proporcionó
            if (emailSup != null && !emailSup.isEmpty() && !esEmailValido(emailSup)) {
                redirectAttributes.addFlashAttribute("error", "El formato del email no es válido");
                return "redirect:/supervisiones/nuevo";
            }
            
            // Asignar cargo si se proporcionó un ID
            if (cargoId != null) {
                Optional<Cargo> cargo = cargoRepository.findById(cargoId);
                if (cargo.isPresent()) {
                    supervision.setCargo(cargo.get());
                    supervision.setCodigoCargoSup(cargo.get().getCodigoCar());
                } else {
                    redirectAttributes.addFlashAttribute("error", "El cargo seleccionado no existe");
                    return "redirect:/supervisiones/nuevo";
                }
            }
            
            // Asignar email
            supervision.setEmailSup(emailSup);
            
            // Establecer fechas
            supervision.setFechaCreadoSup(LocalDateTime.now());
            supervision.setFechaEditadoSup(LocalDateTime.now());
            
            // Guardar supervisión
            supervisaRepository.save(supervision);
            
            // Enviar notificación por email si hay dirección
            if (emailSup != null && !emailSup.isEmpty()) {
                enviarNotificacionSupervision(supervision, "creada");
            }
            
            redirectAttributes.addFlashAttribute("exito", "Supervisión guardada exitosamente");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }
        return "redirect:/supervisiones";
    }
    
    // ============= MOSTRAR FORMULARIO EDITAR SUPERVISIÓN =============
    @GetMapping("/editar/{codigo}")
    public String editarSupervision(@PathVariable Long codigo, Model model, RedirectAttributes redirectAttributes) {
        Optional<Supervisa> supervision = supervisaRepository.findById(codigo);
        if (supervision.isPresent()) {
            model.addAttribute("supervision", supervision.get());
            model.addAttribute("cargos", cargoRepository.findAll());
            return "supervisa/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Supervisión no encontrada");
            return "redirect:/supervisiones";
        }
    }
    
    // ============= ACTUALIZAR SUPERVISIÓN EXISTENTE (CON EMAIL) =============
    @PostMapping("/actualizar/{codigo}")
    public String actualizarSupervision(@PathVariable Long codigo, 
                                        @ModelAttribute Supervisa supervisionDetails,
                                        @RequestParam(required = false) Long cargoId,
                                        @RequestParam(required = false) String emailSup,
                                        RedirectAttributes redirectAttributes) {
        try {
            Optional<Supervisa> supervisionOptional = supervisaRepository.findById(codigo);
            
            if (supervisionOptional.isPresent()) {
                Supervisa supervision = supervisionOptional.get();
                
                // Validar email si se proporcionó
                if (emailSup != null && !emailSup.isEmpty() && !esEmailValido(emailSup)) {
                    redirectAttributes.addFlashAttribute("error", "El formato del email no es válido");
                    return "redirect:/supervisiones/editar/" + codigo;
                }
                
                // Actualizar código de cargo
                supervision.setCodigoCargoSup(supervisionDetails.getCodigoCargoSup());
                
                // Actualizar email
                String emailAnterior = supervision.getEmailSup();
                supervision.setEmailSup(emailSup);
                
                // Actualizar cargo si se seleccionó uno nuevo
                if (cargoId != null) {
                    Optional<Cargo> cargo = cargoRepository.findById(cargoId);
                    if (cargo.isPresent()) {
                        supervision.setCargo(cargo.get());
                        supervision.setCodigoCargoSup(cargo.get().getCodigoCar());
                    }
                }
                
                // Actualizar fecha de edición
                supervision.setFechaEditadoSup(LocalDateTime.now());
                
                // Guardar cambios
                supervisaRepository.save(supervision);
                
                // Enviar notificación si el email cambió o si hay email
                if (emailSup != null && !emailSup.isEmpty()) {
                    if (!emailSup.equals(emailAnterior)) {
                        enviarNotificacionSupervision(supervision, "actualizada (email modificado)");
                    } else {
                        enviarNotificacionSupervision(supervision, "actualizada");
                    }
                }
                
                redirectAttributes.addFlashAttribute("exito", "Supervisión actualizada exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Supervisión no encontrada");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
        }
        return "redirect:/supervisiones";
    }
    
    // ============= ELIMINAR SUPERVISIÓN (CON NOTIFICACIÓN) =============
    @GetMapping("/eliminar/{codigo}")
    public String eliminarSupervision(@PathVariable Long codigo, RedirectAttributes redirectAttributes) {
        try {
            Optional<Supervisa> supervisionOpt = supervisaRepository.findById(codigo);
            
            if (supervisionOpt.isPresent()) {
                Supervisa supervision = supervisionOpt.get();
                String emailResponsable = supervision.getEmailSup();
                
                // Eliminar supervisión
                supervisaRepository.deleteById(codigo);
                
                // Notificar eliminación si hay email
                if (emailResponsable != null && !emailResponsable.isEmpty()) {
                    enviarNotificacionEliminacion(supervision, emailResponsable);
                }
                
                redirectAttributes.addFlashAttribute("exito", "Supervisión eliminada exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Supervisión no encontrada");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/supervisiones";
    }
    
    // ============= FILTRAR SUPERVISIONES POR CARGO =============
    @GetMapping("/cargo/{cargoId}")
    public String supervisionesPorCargo(@PathVariable Long cargoId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Cargo> cargo = cargoRepository.findById(cargoId);
        if (cargo.isPresent()) {
            List<Supervisa> supervisiones = supervisaRepository.findByCargoCodigoCar(cargoId);
            model.addAttribute("supervisiones", supervisiones);
            model.addAttribute("filtroCargo", cargo.get().getNombreCar());
            model.addAttribute("cargos", cargoRepository.findAll());
            
            // También agregar estadísticas para la vista filtrada
            long totalSupervisiones = supervisiones.size();
            model.addAttribute("totalSupervisiones", totalSupervisiones);
            model.addAttribute("supervisionesHoy", 0L); // Simplificado
            model.addAttribute("supervisionesSemana", 0L); // Simplificado
            model.addAttribute("cargosSupervisados", 1L); // Solo este cargo
            
            return "supervisa/list";
        } else {
            redirectAttributes.addFlashAttribute("error", "Cargo no encontrado");
            return "redirect:/supervisiones";
        }
    }
    
    // ============= VERIFICAR SI EXISTE SUPERVISIÓN (AJAX) =============
    @GetMapping("/verificar/{codigo}")
    @ResponseBody
    public boolean existeSupervision(@PathVariable Long codigo) {
        return supervisaRepository.existsById(codigo);
    }
    
    // ============= OBTENER SUPERVISIONES RECIENTES =============
    @GetMapping("/recientes")
    public String supervisionesRecientes(Model model) {
        model.addAttribute("supervisiones", supervisaRepository.findAllByOrderByFechaCreadoSupDesc());
        model.addAttribute("titulo", "Supervisiones Recientes");
        return "supervisa/list";
    }
    
    // ============= ENVIAR EMAIL MANUALMENTE =============
    @PostMapping("/enviar-email/{codigo}")
    public String enviarEmailManual(@PathVariable Long codigo, 
                                    @RequestParam String asunto,
                                    @RequestParam String mensaje,
                                    RedirectAttributes redirectAttributes) {
        try {
            Optional<Supervisa> supervisionOpt = supervisaRepository.findById(codigo);
            
            if (supervisionOpt.isPresent()) {
                Supervisa supervision = supervisionOpt.get();
                String email = supervision.getEmailSup();
                
                if (email != null && !email.isEmpty()) {
                    emailService.enviarCorreoSimple(email, asunto, mensaje);
                    redirectAttributes.addFlashAttribute("exito", "Email enviado correctamente a " + email);
                } else {
                    redirectAttributes.addFlashAttribute("error", "Esta supervisión no tiene email asignado");
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Supervisión no encontrada");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al enviar email: " + e.getMessage());
        }
        return "redirect:/supervisiones/ver/" + codigo;
    }
    
    // ============= MÉTODOS PRIVADOS PARA EMAILS =============
    
    /**
     * Envía notificación de creación/actualización de supervisión
     */
    private void enviarNotificacionSupervision(Supervisa supervision, String accion) {
        try {
            String email = supervision.getEmailSup();
            if (email == null || email.isEmpty()) return;
            
            String asunto = "📋 Notificación: Supervisión " + accion + " #" + supervision.getCodigoSup();
            String contenido = generarContenidoEmail(supervision, accion);
            
            emailService.enviarCorreoSimple(email, asunto, contenido);
            System.out.println("✅ Notificación enviada a: " + email);
            
        } catch (Exception e) {
            System.out.println("❌ Error al enviar notificación: " + e.getMessage());
        }
    }
    
    /**
     * Envía notificación de eliminación de supervisión
     */
    private void enviarNotificacionEliminacion(Supervisa supervision, String email) {
        try {
            String asunto = "🗑️ Notificación: Supervisión eliminada #" + supervision.getCodigoSup();
            String contenido = "Se ha eliminado la siguiente supervisión:\n\n" +
                              "Código: " + supervision.getCodigoSup() + "\n" +
                              "Código Cargo: " + supervision.getCodigoCargoSup() + "\n" +
                              "Cargo: " + (supervision.getCargo() != null ? supervision.getCargo().getNombreCar() : "N/A") + "\n" +
                              "Fecha de eliminación: " + LocalDateTime.now() + "\n\n" +
                              "Si tiene preguntas, contacte al administrador del sistema.";
            
            emailService.enviarCorreoSimple(email, asunto, contenido);
            System.out.println("✅ Notificación de eliminación enviada a: " + email);
            
        } catch (Exception e) {
            System.out.println("❌ Error al enviar notificación de eliminación: " + e.getMessage());
        }
    }
    
    /**
     * Genera el contenido del email para notificaciones
     */
    private String generarContenidoEmail(Supervisa supervision, String accion) {
        StringBuilder contenido = new StringBuilder();
        contenido.append("Se ha ").append(accion).append(" una supervisión.\n\n");
        contenido.append("📌 DETALLES DE LA SUPERVISIÓN:\n");
        contenido.append("================================\n");
        contenido.append("🔹 Código: ").append(supervision.getCodigoSup()).append("\n");
        contenido.append("🔹 Código de Cargo: ").append(supervision.getCodigoCargoSup()).append("\n");
        
        if (supervision.getCargo() != null) {
            contenido.append("🔹 Nombre del Cargo: ").append(supervision.getCargo().getNombreCar()).append("\n");
        }
        
        contenido.append("🔹 Fecha de creación: ").append(supervision.getFechaCreadoSup()).append("\n");
        contenido.append("🔹 Última edición: ").append(supervision.getFechaEditadoSup()).append("\n");
        contenido.append("================================\n\n");
        contenido.append("📧 Email de contacto: ").append(supervision.getEmailSup() != null ? supervision.getEmailSup() : "No especificado").append("\n\n");
        contenido.append("Este es un mensaje automático, por favor no responder.");
        
        return contenido.toString();
    }
    
    /**
     * Valida formato de email
     */
    private boolean esEmailValido(String email) {
        if (email == null || email.isEmpty()) return true; // Email opcional
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
    
    // ============= ENDPOINT PARA PROBAR EMAIL =============
    @GetMapping("/test-email")
    @ResponseBody
    public String testEmail(@RequestParam String email) {
        try {
            emailService.enviarCorreoSimple(email, 
                                           "🔔 Prueba de Email - Sistema Supervisiones", 
                                           "Hola,\n\nEste es un correo de prueba desde el módulo de supervisiones.\n\nSaludos!");
            return "✅ Email de prueba enviado a: " + email;
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }
    
    // ============= ENDPOINT PARA VER ESTADÍSTICAS EN JSON =============
    @GetMapping("/estadisticas")
    @ResponseBody
    public String verEstadisticas() {
        List<Supervisa> supervisiones = supervisaRepository.findAll();
        
        long totalSupervisiones = supervisiones.size();
        
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime finHoy = LocalDate.now().plusDays(1).atStartOfDay();
        long supervisionesHoy = supervisaRepository.findByFechaCreadoSupBetween(inicioHoy, finHoy).size();
        
        LocalDateTime inicioSemana = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1).atStartOfDay();
        LocalDateTime finSemana = LocalDate.now().plusDays(7 - LocalDate.now().getDayOfWeek().getValue()).atStartOfDay();
        long supervisionesSemana = supervisaRepository.findByFechaCreadoSupBetween(inicioSemana, finSemana).size();
        
        long cargosSupervisados = supervisiones.stream()
                .map(s -> s.getCargo() != null ? s.getCargo().getCodigoCar() : null)
                .filter(codigo -> codigo != null)
                .distinct()
                .count();
        
        return String.format(
            "📊 ESTADÍSTICAS DE SUPERVISIONES:\n" +
            "════════════════════════════════\n" +
            "📋 Total Supervisiones: %d\n" +
            "📅 Supervisiones Hoy: %d\n" +
            "📆 Supervisiones Esta Semana: %d\n" +
            "👥 Cargos Supervisados: %d\n",
            totalSupervisiones, supervisionesHoy, supervisionesSemana, cargosSupervisados
        );
    }
}