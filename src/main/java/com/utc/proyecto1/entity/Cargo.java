package com.utc.proyecto1.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cargo")
public class Cargo {
    
    @Id
    @Column(name = "codigo_car")
    private Long codigoCar;
    
    @Column(name = "nombre_car", length = 255)
    private String nombreCar;
    
    @Column(name = "estado_car", length = 50)
    private String estadoCar;
    
    @Column(name = "reporta_car", length = 255)
    private String reportaCar;
    
    @Column(name = "reemplazo_car", length = 255)
    private String reemplazoCar;
    
    @Column(name = "macroproceso_car", length = 255)
    private String macroprocesoCar;
    
    @Column(name = "codigo_sectorial_car", length = 100)
    private String codigoSectorialCar;
    
    @Column(name = "area_car", length = 255)
    private String areaCar;
    
    @Column(name = "proceso_car", length = 255)
    private String procesoCar;
    
    @Column(name = "cod_cargo_legajo_car", length = 100)
    private String codCargoLegajoCar;
    
    @Column(name = "seccion_car", length = 255)
    private String seccionCar;
    
    @Column(name = "grupo_riesgo_car", length = 100)
    private String grupoRiesgoCar;
    
    @Column(name = "maquinaria_equipo_car", columnDefinition = "TEXT")
    private String maquinariaEquipoCar;
    
    @Column(name = "nivel_riesgo_car", length = 50)
    private String nivelRiesgoCar;
    
    @Column(name = "mision_car", columnDefinition = "TEXT")
    private String misionCar;
    
    @Column(name = "codigo_est", length = 50)
    private String codigoEst;
    
    @Column(name = "reporta_cargo_car", length = 255)
    private String reportaCargoCar;
    
    @Column(name = "reemplazo_cargo_car", length = 255)
    private String reemplazoCargoCar;
    
    @Column(name = "seccion_1", columnDefinition = "TEXT")
    private String seccion1;
    
    @Column(name = "seccion_2", columnDefinition = "TEXT")
    private String seccion2;
    
    @Column(name = "seccion_3", columnDefinition = "TEXT")
    private String seccion3;
    
    @Column(name = "seccion_4", columnDefinition = "TEXT")
    private String seccion4;
    
    @Column(name = "seccion_5", columnDefinition = "TEXT")
    private String seccion5;
    
    @Column(name = "seccion_6", columnDefinition = "TEXT")
    private String seccion6;
    
    @Column(name = "seccion_7", columnDefinition = "TEXT")
    private String seccion7;
    
    @Column(name = "seccion_8", columnDefinition = "TEXT")
    private String seccion8;
    
    @Column(name = "nombre_cargo", length = 255)
    private String nombreCargo;
    
    // Fechas de auditoría
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // Constructores
    public Cargo() {}
    
    // Getters y Setters para todos los campos
    public Long getCodigoCar() { return codigoCar; }
    public void setCodigoCar(Long codigoCar) { this.codigoCar = codigoCar; }
    
    public String getNombreCar() { return nombreCar; }
    public void setNombreCar(String nombreCar) { this.nombreCar = nombreCar; }
    
    public String getEstadoCar() { return estadoCar; }
    public void setEstadoCar(String estadoCar) { this.estadoCar = estadoCar; }
    
    public String getReportaCar() { return reportaCar; }
    public void setReportaCar(String reportaCar) { this.reportaCar = reportaCar; }
    
    public String getReemplazoCar() { return reemplazoCar; }
    public void setReemplazoCar(String reemplazoCar) { this.reemplazoCar = reemplazoCar; }
    
    public String getMacroprocesoCar() { return macroprocesoCar; }
    public void setMacroprocesoCar(String macroprocesoCar) { this.macroprocesoCar = macroprocesoCar; }
    
    public String getCodigoSectorialCar() { return codigoSectorialCar; }
    public void setCodigoSectorialCar(String codigoSectorialCar) { this.codigoSectorialCar = codigoSectorialCar; }
    
    public String getAreaCar() { return areaCar; }
    public void setAreaCar(String areaCar) { this.areaCar = areaCar; }
    
    public String getProcesoCar() { return procesoCar; }
    public void setProcesoCar(String procesoCar) { this.procesoCar = procesoCar; }
    
    public String getCodCargoLegajoCar() { return codCargoLegajoCar; }
    public void setCodCargoLegajoCar(String codCargoLegajoCar) { this.codCargoLegajoCar = codCargoLegajoCar; }
    
    public String getSeccionCar() { return seccionCar; }
    public void setSeccionCar(String seccionCar) { this.seccionCar = seccionCar; }
    
    public String getGrupoRiesgoCar() { return grupoRiesgoCar; }
    public void setGrupoRiesgoCar(String grupoRiesgoCar) { this.grupoRiesgoCar = grupoRiesgoCar; }
    
    public String getMaquinariaEquipoCar() { return maquinariaEquipoCar; }
    public void setMaquinariaEquipoCar(String maquinariaEquipoCar) { this.maquinariaEquipoCar = maquinariaEquipoCar; }
    
    public String getNivelRiesgoCar() { return nivelRiesgoCar; }
    public void setNivelRiesgoCar(String nivelRiesgoCar) { this.nivelRiesgoCar = nivelRiesgoCar; }
    
    public String getMisionCar() { return misionCar; }
    public void setMisionCar(String misionCar) { this.misionCar = misionCar; }
    
    public String getCodigoEst() { return codigoEst; }
    public void setCodigoEst(String codigoEst) { this.codigoEst = codigoEst; }
    
    public String getReportaCargoCar() { return reportaCargoCar; }
    public void setReportaCargoCar(String reportaCargoCar) { this.reportaCargoCar = reportaCargoCar; }
    
    public String getReemplazoCargoCar() { return reemplazoCargoCar; }
    public void setReemplazoCargoCar(String reemplazoCargoCar) { this.reemplazoCargoCar = reemplazoCargoCar; }
    
    public String getSeccion1() { return seccion1; }
    public void setSeccion1(String seccion1) { this.seccion1 = seccion1; }
    
    public String getSeccion2() { return seccion2; }
    public void setSeccion2(String seccion2) { this.seccion2 = seccion2; }
    
    public String getSeccion3() { return seccion3; }
    public void setSeccion3(String seccion3) { this.seccion3 = seccion3; }
    
    public String getSeccion4() { return seccion4; }
    public void setSeccion4(String seccion4) { this.seccion4 = seccion4; }
    
    public String getSeccion5() { return seccion5; }
    public void setSeccion5(String seccion5) { this.seccion5 = seccion5; }
    
    public String getSeccion6() { return seccion6; }
    public void setSeccion6(String seccion6) { this.seccion6 = seccion6; }
    
    public String getSeccion7() { return seccion7; }
    public void setSeccion7(String seccion7) { this.seccion7 = seccion7; }
    
    public String getSeccion8() { return seccion8; }
    public void setSeccion8(String seccion8) { this.seccion8 = seccion8; }
    
    public String getNombreCargo() { return nombreCargo; }
    public void setNombreCargo(String nombreCargo) { this.nombreCargo = nombreCargo; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}