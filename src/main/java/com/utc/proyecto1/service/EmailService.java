package com.utc.proyecto1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    private static final String EMAIL_FROM = "fabian.alegria1188@utc.edu.ec";
    
    /**
     * Envía un correo simple (texto plano)
     */
    public void enviarCorreoSimple(String destinatario, String asunto, String contenido) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(destinatario);
            mensaje.setSubject(asunto);
            mensaje.setText(contenido);
            mensaje.setFrom(EMAIL_FROM);
            mailSender.send(mensaje);
            System.out.println("✅ Correo enviado a: " + destinatario);
        } catch (Exception e) {  // ← La variable 'e' está correctamente declarada aquí
            System.out.println("❌ Error al enviar correo simple: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Envía un correo con formato HTML
     */
    public void enviarCorreoHTML(String destinatario, String asunto, String contenidoHTML) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(contenidoHTML, true);
            helper.setFrom(EMAIL_FROM);
            mailSender.send(mensaje);
            System.out.println("✅ Correo HTML enviado a: " + destinatario);
        } catch (MessagingException e) {
            System.out.println("❌ Error enviando correo HTML: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Envía notificación de supervisión
     */
    public void enviarNotificacionSupervision(String destinatario, String codigoSup, String accion, String detalles) {
        String asunto = "🔔 Notificación de Supervisión #" + codigoSup;
        String contenido = "Se ha " + accion + " la supervisión #" + codigoSup + ".\n\n" +
                          "Detalles:\n" + detalles + "\n\n" +
                          "Fecha y hora: " + java.time.LocalDateTime.now() + "\n" +
                          "Este es un mensaje automático.";
        
        enviarCorreoSimple(destinatario, asunto, contenido);
    }
    
    /**
     * Envía notificación de bienvenida
     */
    public void enviarBienvenida(String destinatario, String nombreUsuario) {
        String asunto = "🎉 ¡Bienvenido al Sistema!";
        String contenido = "Hola " + nombreUsuario + ",\n\n" +
                          "Tu cuenta ha sido creada exitosamente.\n\n" +
                          "Saludos,\n" +
                          "El equipo del sistema";
        
        enviarCorreoSimple(destinatario, asunto, contenido);
    }
    
    /**
     * Verifica si el email tiene formato válido
     */
    public boolean esEmailValido(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
}