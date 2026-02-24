package com.utc.proyecto1.controller;

import com.utc.proyecto1.entity.Cargo;
import com.utc.proyecto1.repository.CargoRepository;
import com.utc.proyecto1.repository.SupervisaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;  // 👈 ESTE IMPORT FALTABA

import java.util.List;

@Controller
public class HomeController {
    
    @Autowired
    private CargoRepository cargoRepository;
    
    @Autowired
    private SupervisaRepository supervisaRepository;
    
    @GetMapping("/")
    public String index(Model model) {
        
        // ========== ESTADÍSTICAS DE CARGOS ==========
        List<Cargo> cargos = cargoRepository.findAll();
        
        long totalCargos = cargos.size();
        long cargosActivos = cargos.stream()
                .filter(c -> "Activo".equalsIgnoreCase(c.getEstadoCar()))
                .count();
        long cargosRiesgoAlto = cargos.stream()
                .filter(c -> "Alto".equalsIgnoreCase(c.getNivelRiesgoCar()) || 
                             "Crítico".equalsIgnoreCase(c.getNivelRiesgoCar()))
                .count();
        long totalAreas = cargos.stream()
                .map(Cargo::getAreaCar)
                .filter(area -> area != null && !area.trim().isEmpty())
                .distinct()
                .count();
        
        // ========== ESTADÍSTICAS DE SUPERVISIONES ==========
        long totalSupervisiones = supervisaRepository.count();
        
        // ========== AGREGAR AL MODELO ==========
        model.addAttribute("totalCargos", totalCargos);
        model.addAttribute("cargosActivos", cargosActivos);
        model.addAttribute("cargosRiesgoAlto", cargosRiesgoAlto);
        model.addAttribute("totalAreas", totalAreas);
        model.addAttribute("totalSupervisiones", totalSupervisiones);
        
        return "index";
    }
    
    // ============= ENDPOINT PARA VER ESTADÍSTICAS EN JSON =============
    @GetMapping("/estadisticas")
    @ResponseBody  // 👈 AHORA FUNCIONA CON EL IMPORT
    public String verEstadisticas() {
        List<Cargo> cargos = cargoRepository.findAll();
        
        long totalCargos = cargos.size();
        long cargosActivos = cargos.stream()
                .filter(c -> "Activo".equalsIgnoreCase(c.getEstadoCar()))
                .count();
        long cargosRiesgoAlto = cargos.stream()
                .filter(c -> "Alto".equalsIgnoreCase(c.getNivelRiesgoCar()) || 
                             "Crítico".equalsIgnoreCase(c.getNivelRiesgoCar()))
                .count();
        long totalAreas = cargos.stream()
                .map(Cargo::getAreaCar)
                .filter(area -> area != null && !area.trim().isEmpty())
                .distinct()
                .count();
        
        long totalSupervisiones = supervisaRepository.count();
        
        return String.format(
            "📊 ESTADÍSTICAS DEL SISTEMA:\n" +
            "═══════════════════════════\n" +
            "📋 CARGOS:\n" +
            "  • Total: %d\n" +
            "  • Activos: %d\n" +
            "  • Riesgo Alto: %d\n" +
            "  • Áreas distintas: %d\n\n" +
            "👁️ SUPERVISIONES:\n" +
            "  • Total: %d\n",
            totalCargos, cargosActivos, cargosRiesgoAlto, totalAreas,
            totalSupervisiones
        );
    }
}