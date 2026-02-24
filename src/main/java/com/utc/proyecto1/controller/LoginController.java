package com.utc.proyecto1.controller;

import com.utc.proyecto1.entity.Usuario;
import com.utc.proyecto1.repository.UsuarioRepository;
import com.utc.proyecto1.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class LoginController {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private EmailService emailService;
    
    @GetMapping("/login")
    public String loginForm(Model model, HttpSession session) {
        System.out.println("=== MOSTRANDO FORMULARIO LOGIN ===");
        // Verificar si ya hay sesión activa
        if (session.getAttribute("usuario") != null) {
            System.out.println("Usuario ya tiene sesión, redirigiendo a inicio");
            return "redirect:/";
        }
        return "login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String username, 
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        
        System.out.println("=== INTENTO DE LOGIN ===");
        System.out.println("Username recibido: '" + username + "'");
        System.out.println("Password recibido: '" + password + "'");
        
        try {
            // Buscar usuario en la base de datos
            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
            
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                System.out.println("Usuario encontrado en BD:");
                System.out.println("  - Username: " + usuario.getUsername());
                System.out.println("  - Password en BD: '" + usuario.getPassword() + "'");
                System.out.println("  - Activo: " + usuario.getActivo());
                System.out.println("  - Rol: " + usuario.getRol());
                
                // Verificar contraseña y estado activo
                boolean passwordCorrecta = usuario.getPassword().equals(password);
                System.out.println("¿Password coincide? " + passwordCorrecta);
                
                if (passwordCorrecta && usuario.getActivo()) {
                    System.out.println("=== LOGIN EXITOSO ===");
                    
                    // Actualizar último acceso
                    usuario.setUltimoAcceso(LocalDateTime.now());
                    usuarioRepository.save(usuario);
                    
                    // Guardar en sesión - IMPORTANTE: asegurar que se guarda
                    session.setAttribute("usuario", usuario);
                    session.setAttribute("usuarioId", usuario.getIdUsuario());
                    session.setAttribute("username", usuario.getUsername());
                    session.setAttribute("nombreCompleto", usuario.getNombreCompleto());
                    session.setAttribute("rol", usuario.getRol());
                    
                    System.out.println("Sesión guardada con ID: " + session.getId());
                    System.out.println("Atributos de sesión:");
                    System.out.println("  - usuario: " + (session.getAttribute("usuario") != null ? "OK" : "NULL"));
                    System.out.println("  - username: " + session.getAttribute("username"));
                    
                    // Enviar notificación de login exitoso
                    try {
                        String asunto = "🔔 Notificación de Acceso - Sistema";
                        String contenido = "Hola " + usuario.getNombreCompleto() + ",\n\n" +
                                          "Se ha registrado un acceso a tu cuenta:\n" +
                                          "📅 Fecha y hora: " + LocalDateTime.now() + "\n" +
                                          "🖥️ Desde: Sistema de Gestión\n\n" +
                                          "Si no fuiste tú, contacta al administrador.\n\n" +
                                          "Saludos,\nEquipo del Sistema";
                        
                        emailService.enviarCorreoSimple(usuario.getEmail(), asunto, contenido);
                        System.out.println("✅ Notificación de acceso enviada a: " + usuario.getEmail());
                    } catch (Exception e) {
                        System.out.println("⚠️ No se pudo enviar notificación de acceso: " + e.getMessage());
                    }
                    
                    // Redirigir al inicio
                    return "redirect:/";
                } else {
                    System.out.println("=== LOGIN FALLIDO: Credenciales incorrectas ===");
                    if (!passwordCorrecta) System.out.println("  Motivo: Contraseña incorrecta");
                    if (!usuario.getActivo()) System.out.println("  Motivo: Usuario inactivo");
                }
            } else {
                System.out.println("=== LOGIN FALLIDO: Usuario no encontrado '" + username + "' ===");
            }
        } catch (Exception e) {
            System.out.println("=== ERROR EN LOGIN ===");
            System.out.println("Excepción: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Si llegamos aquí, el login falló
        redirectAttributes.addFlashAttribute("error", "Usuario o contraseña incorrectos");
        return "redirect:/login";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        System.out.println("=== CERRANDO SESIÓN ===");
        System.out.println("Sesión ID antes de invalidar: " + session.getId());
        session.invalidate();
        System.out.println("Sesión invalidada");
        return "redirect:/login";
    }
    
    @GetMapping("/registro")
    public String registroForm(Model model) {
        System.out.println("=== MOSTRANDO FORMULARIO REGISTRO ===");
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }
    
    @PostMapping("/registro")
    public String registro(@ModelAttribute Usuario usuario,
                           @RequestParam String confirmPassword,
                           RedirectAttributes redirectAttributes) {
        
        System.out.println("=== INTENTO DE REGISTRO ===");
        System.out.println("Username: " + usuario.getUsername());
        System.out.println("Email: " + usuario.getEmail());
        
        // Validar que las contraseñas coincidan
        if (!usuario.getPassword().equals(confirmPassword)) {
            System.out.println("Registro fallido: Las contraseñas no coinciden");
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
            return "redirect:/registro";
        }
        
        // Validar que el username no exista
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            System.out.println("Registro fallido: El username ya existe");
            redirectAttributes.addFlashAttribute("error", "El nombre de usuario ya existe");
            return "redirect:/registro";
        }
        
        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            System.out.println("Registro fallido: El email ya existe");
            redirectAttributes.addFlashAttribute("error", "El email ya está registrado");
            return "redirect:/registro";
        }
        
        try {
            // Configurar usuario nuevo
            usuario.setFechaCreacion(LocalDateTime.now());
            usuario.setActivo(true);
            usuario.setRol("USER");
            
            // Guardar usuario
            usuarioRepository.save(usuario);
            System.out.println("Usuario registrado exitosamente: " + usuario.getUsername());
            
            // Enviar correo de bienvenida
            try {
                String asunto = "🎉 ¡Bienvenido al Sistema!";
                String contenido = "Hola " + usuario.getNombreCompleto() + ",\n\n" +
                                  "¡Bienvenido a nuestro sistema!\n\n" +
                                  "Tu cuenta ha sido creada exitosamente con los siguientes datos:\n" +
                                  "👤 Usuario: " + usuario.getUsername() + "\n" +
                                  "📧 Email: " + usuario.getEmail() + "\n" +
                                  "📅 Fecha de registro: " + LocalDateTime.now() + "\n\n" +
                                  "Ya puedes iniciar sesión con tus credenciales.\n\n" +
                                  "Saludos,\nEquipo del Sistema";
                
                emailService.enviarCorreoSimple(usuario.getEmail(), asunto, contenido);
                System.out.println("✅ Correo de bienvenida enviado a: " + usuario.getEmail());
            } catch (Exception e) {
                System.out.println("⚠️ No se pudo enviar correo de bienvenida: " + e.getMessage());
                // No fallamos el registro por error de correo
            }
            
            redirectAttributes.addFlashAttribute("success", "Registro exitoso. Por favor inicia sesión.");
            
        } catch (Exception e) {
            System.out.println("❌ Error en registro: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al registrar: " + e.getMessage());
            return "redirect:/registro";
        }
        
        return "redirect:/login";
    }
    
    // Endpoint de prueba para verificar sesión
    @GetMapping("/verificar-sesion")
    @ResponseBody
    public String verificarSesion(HttpSession session) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== VERIFICACIÓN DE SESIÓN ===\n\n");
        sb.append("Session ID: ").append(session.getId()).append("\n");
        sb.append("¿Sesión nueva? ").append(session.isNew()).append("\n\n");
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            sb.append("✅ USUARIO EN SESIÓN:\n");
            sb.append("  Username: ").append(usuario.getUsername()).append("\n");
            sb.append("  Nombre: ").append(usuario.getNombreCompleto()).append("\n");
            sb.append("  Email: ").append(usuario.getEmail()).append("\n");
            sb.append("  Rol: ").append(usuario.getRol()).append("\n");
            sb.append("  ID: ").append(usuario.getIdUsuario()).append("\n");
        } else {
            sb.append("❌ NO HAY USUARIO EN SESIÓN\n");
        }
        
        sb.append("\n📋 ATRIBUTOS DE SESIÓN:\n");
        java.util.Enumeration<String> atributos = session.getAttributeNames();
        if (!atributos.hasMoreElements()) {
            sb.append("  No hay atributos en la sesión\n");
        }
        while (atributos.hasMoreElements()) {
            String nombre = atributos.nextElement();
            Object valor = session.getAttribute(nombre);
            sb.append("  - ").append(nombre).append(": ").append(valor).append("\n");
        }
        
        return sb.toString().replace("\n", "<br>");
    }
    
    // Endpoint para probar conexión a BD
    @GetMapping("/test-bd")
    @ResponseBody
    public String testBD() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== TEST DE CONEXIÓN A BD ===\n\n");
        
        try {
            long total = usuarioRepository.count();
            sb.append("✅ Conexión exitosa\n");
            sb.append("Total usuarios: ").append(total).append("\n\n");
            
            if (total > 0) {
                sb.append("📋 USUARIOS EN BD:\n");
                usuarioRepository.findAll().forEach(u -> {
                    sb.append("  - ").append(u.getUsername())
                      .append(" | ").append(u.getPassword())
                      .append(" | ").append(u.getEmail())
                      .append(" | Activo: ").append(u.getActivo())
                      .append(" | Rol: ").append(u.getRol())
                      .append("\n");
                });
            } else {
                sb.append("❌ No hay usuarios. Ejecuta:\n");
                sb.append("INSERT INTO usuario (username, password, email, nombre_completo, rol, activo, fecha_creacion) VALUES\n");
                sb.append("('admin', 'admin123', 'admin@sistema.com', 'Administrador', 'ADMIN', 1, NOW()),\n");
                sb.append("('user', 'user123', 'usuario@sistema.com', 'Usuario Prueba', 'USER', 1, NOW());\n");
            }
        } catch (Exception e) {
            sb.append("❌ ERROR: ").append(e.getMessage()).append("\n");
            sb.append("Tipo: ").append(e.getClass().getName());
        }
        
        return sb.toString().replace("\n", "<br>");
    }
    
    // ============= ENDPOINTS PARA PRUEBAS DE CORREO =============
    
    /**
     * Endpoint para probar el envío de correos
     * Uso: http://localhost:8081/test-email
     */
    @GetMapping("/test-email")
    @ResponseBody
    public String testEmail(@RequestParam(defaultValue = "fabian.alegria1188@utc.edu.ec") String destinatario) {
        try {
            String asunto = "📧 PRUEBA DE CORREO - " + LocalDateTime.now();
            String contenido = "Hola!\n\n" +
                              "Este es un correo de prueba desde tu aplicación Spring Boot.\n\n" +
                              "Si recibes esto, la configuración de email funciona correctamente.\n\n" +
                              "Detalles técnicos:\n" +
                              "📅 Fecha y hora: " + LocalDateTime.now() + "\n" +
                              "📧 Destinatario: " + destinatario + "\n" +
                              "🖥️ Aplicación: Proyecto1\n\n" +
                              "Saludos,\nEquipo de Desarrollo";
            
            emailService.enviarCorreoSimple(destinatario, asunto, contenido);
            
            return "<h2 style='color:green'>✅ CORREO ENVIADO EXITOSAMENTE</h2>" +
                   "<p><strong>Destinatario:</strong> " + destinatario + "</p>" +
                   "<p><strong>Asunto:</strong> " + asunto + "</p>" +
                   "<p><strong>Hora:</strong> " + LocalDateTime.now() + "</p>" +
                   "<p>📬 Revisa tu bandeja de entrada y la carpeta de SPAM.</p>";
        } catch (Exception e) {
            return "<h2 style='color:red'>❌ ERROR AL ENVIAR CORREO</h2>" +
                   "<p><strong>Error:</strong> " + e.getMessage() + "</p>" +
                   "<p><strong>Tipo:</strong> " + e.getClass().getName() + "</p>" +
                   "<p><strong>Solución posible:</strong> Verifica tu contraseña de aplicación en Gmail</p>";
        }
    }
    
    /**
     * Endpoint para verificar el estado del servicio de correo
     * Uso: http://localhost:8081/verificar-email-service
     */
    @GetMapping("/verificar-email-service")
    @ResponseBody
    public String verificarEmailService() {
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>🔧 VERIFICACIÓN DEL SERVICIO DE CORREO</h2>");
        
        // Verificar EmailService
        if (emailService != null) {
            sb.append("<p style='color:green'>✅ EmailService: DISPONIBLE</p>");
        } else {
            sb.append("<p style='color:red'>❌ EmailService: NO DISPONIBLE</p>");
        }
        
        // Mostrar configuración
        sb.append("<h3>📋 Configuración actual:</h3>");
        sb.append("<ul>");
        sb.append("<li><strong>Host:</strong> smtp.gmail.com</li>");
        sb.append("<li><strong>Puerto:</strong> 587</li>");
        sb.append("<li><strong>Usuario:</strong> fabian.alegria1188@utc.edu.ec</li>");
        sb.append("<li><strong>Autenticación:</strong> Sí</li>");
        sb.append("<li><strong>TLS:</strong> Habilitado</li>");
        sb.append("</ul>");
        
        sb.append("<h3>🧪 Prueba rápida:</h3>");
        sb.append("<p>Usa este enlace para probar: <a href='/test-email'>/test-email</a></p>");
        sb.append("<p>O con destinatario específico: <a href='/test-email?destinatario=tu@email.com'>/test-email?destinatario=tu@email.com</a></p>");
        
        return sb.toString();
    }
}