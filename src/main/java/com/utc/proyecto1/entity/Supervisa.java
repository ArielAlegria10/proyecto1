package com.utc.proyecto1.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "supervisa")
public class Supervisa {
    
    @Id
    @Column(name = "codigo_sup")
    private Long codigoSup;
    
    @Column(name = "codigo_cargo_sup")
    private Long codigoCargoSup;
    
    // 👇 NUEVO CAMPO DE EMAIL
    @Column(name = "email_sup", length = 255)
    private String emailSup;
    
    @ManyToOne
    @JoinColumn(name = "fk_codigo_car", referencedColumnName = "codigo_car")
    private Cargo cargo;
    
    @Column(name = "fecha_creado_sup")
    private LocalDateTime fechaCreadoSup;
    
    @Column(name = "fecha_editado_sup")
    private LocalDateTime fechaEditadoSup;
    
    // Constructores
    public Supervisa() {}
    
    // Constructor simplificado (sin fechas, se asignan automáticamente)
    public Supervisa(Long codigoSup, Long codigoCargoSup, Cargo cargo, String emailSup) {
        this.codigoSup = codigoSup;
        this.codigoCargoSup = codigoCargoSup;
        this.cargo = cargo;
        this.emailSup = emailSup;  // 👈 AGREGADO
        this.fechaCreadoSup = LocalDateTime.now();
        this.fechaEditadoSup = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Long getCodigoSup() {
        return codigoSup;
    }
    
    public void setCodigoSup(Long codigoSup) {
        this.codigoSup = codigoSup;
    }
    
    public Long getCodigoCargoSup() {
        return codigoCargoSup;
    }
    
    public void setCodigoCargoSup(Long codigoCargoSup) {
        this.codigoCargoSup = codigoCargoSup;
    }
    
    // 👇 GETTER Y SETTER PARA EMAIL
    public String getEmailSup() {
        return emailSup;
    }
    
    public void setEmailSup(String emailSup) {
        this.emailSup = emailSup;
    }
    
    public Cargo getCargo() {
        return cargo;
    }
    
    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
        // Actualizar automáticamente el código cuando se asigna el cargo
        if (cargo != null) {
            this.codigoCargoSup = cargo.getCodigoCar();
        }
    }
    
    public LocalDateTime getFechaCreadoSup() {
        return fechaCreadoSup;
    }
    
    public void setFechaCreadoSup(LocalDateTime fechaCreadoSup) {
        this.fechaCreadoSup = fechaCreadoSup;
    }
    
    public LocalDateTime getFechaEditadoSup() {
        return fechaEditadoSup;
    }
    
    public void setFechaEditadoSup(LocalDateTime fechaEditadoSup) {
        this.fechaEditadoSup = fechaEditadoSup;
    }
    
    // Método helper para actualizar fecha de edición
    @PreUpdate
    public void actualizarFechaEditado() {
        this.fechaEditadoSup = LocalDateTime.now();
    }
    
    @PrePersist
    public void asignarFechasIniciales() {
        if (this.fechaCreadoSup == null) {
            this.fechaCreadoSup = LocalDateTime.now();
        }
        if (this.fechaEditadoSup == null) {
            this.fechaEditadoSup = LocalDateTime.now();
        }
    }
    
    @Override
    public String toString() {
        return "Supervisa{" +
                "codigoSup=" + codigoSup +
                ", codigoCargoSup=" + codigoCargoSup +
                ", emailSup='" + emailSup + '\'' +  // 👈 AGREGADO AL toString
                ", cargo=" + (cargo != null ? cargo.getNombreCar() : "null") +
                ", fechaCreadoSup=" + fechaCreadoSup +
                ", fechaEditadoSup=" + fechaEditadoSup +
                '}';
    }
}