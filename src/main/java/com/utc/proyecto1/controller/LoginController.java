package com.utc.proyecto1.controller;

import com.utc.proyecto1.entity.Usuario;
import com.utc.proyecto1.repository.UsuarioRepository;
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
        
        // Configurar usuario nuevo
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setActivo(true);
        usuario.setRol("USER");
        
        // Guardar usuario
        usuarioRepository.save(usuario);
        System.out.println("Usuario registrado exitosamente: " + usuario.getUsername());
        
        redirectAttributes.addFlashAttribute("success", "Registro exitoso. Por favor inicia sesión.");
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
                      .append(" | Activo: ").append(u.getActivo())
                      .append(" | Rol: ").append(u.getRol())
                      .append("\n");
                });
            } else {
                sb.append("❌ No hay usuarios. Ejecuta:\n");
                sb.append("INSERT INTO usuario (username, password, email, nombre_completo, rol, activo) VALUES\n");
                sb.append("('admin', 'admin123', 'admin@sistema.com', 'Administrador', 'ADMIN', 1),\n");
                sb.append("('user', 'user123', 'usuario@sistema.com', 'Usuario Prueba', 'USER', 1);\n");
            }
        } catch (Exception e) {
            sb.append("❌ ERROR: ").append(e.getMessage()).append("\n");
            sb.append("Tipo: ").append(e.getClass().getName());
        }
        
        return sb.toString().replace("\n", "<br>");
    }
}