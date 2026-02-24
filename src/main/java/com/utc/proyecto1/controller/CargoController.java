package com.utc.proyecto1.controller;

import com.utc.proyecto1.entity.Cargo;
import com.utc.proyecto1.repository.CargoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cargos")
public class CargoController {
    
    @Autowired
    private CargoRepository cargoRepository;
    
    // ============= LISTAR TODOS LOS CARGOS CON ESTADÍSTICAS =============
    @GetMapping
    public String listarCargos(Model model) {
        // Obtener todos los cargos
        List<Cargo> cargos = cargoRepository.findAll();
        model.addAttribute("cargos", cargos);
        
        // ===== CALCULAR ESTADÍSTICAS =====
        // Total de cargos
        long totalCargos = cargos.size();
        model.addAttribute("totalCargos", totalCargos);
        
        // Cargos activos
        long cargosActivos = cargos.stream()
                .filter(c -> "Activo".equalsIgnoreCase(c.getEstadoCar()))
                .count();
        model.addAttribute("cargosActivos", cargosActivos);
        
        // Cargos con riesgo alto
        long cargosRiesgoAlto = cargos.stream()
                .filter(c -> "Alto".equalsIgnoreCase(c.getNivelRiesgoCar()) || 
                             "Crítico".equalsIgnoreCase(c.getNivelRiesgoCar()) ||
                             "Alto".equalsIgnoreCase(c.getGrupoRiesgoCar()))
                .count();
        model.addAttribute("cargosRiesgoAlto", cargosRiesgoAlto);
        
        // Total de áreas distintas
        long totalAreas = cargos.stream()
                .map(Cargo::getAreaCar)
                .filter(area -> area != null && !area.trim().isEmpty())
                .distinct()
                .count();
        model.addAttribute("totalAreas", totalAreas);
        
        return "cargo/list";
    }
    
    // ============= VER DETALLE DE CARGO =============
    @GetMapping("/ver/{codigo}")
    public String verCargo(@PathVariable Long codigo, Model model, RedirectAttributes redirectAttributes) {
        Optional<Cargo> cargo = cargoRepository.findById(codigo);
        if (cargo.isPresent()) {
            model.addAttribute("cargo", cargo.get());
            return "cargo/view";
        } else {
            redirectAttributes.addFlashAttribute("error", "Cargo no encontrado");
            return "redirect:/cargos";
        }
    }
    
    // ============= MOSTRAR FORMULARIO NUEVO CARGO =============
    @GetMapping("/nuevo")
    public String nuevoCargo(Model model) {
        model.addAttribute("cargo", new Cargo());
        return "cargo/form";
    }
    
    // ============= GUARDAR NUEVO CARGO =============
    @PostMapping("/guardar")
    public String guardarCargo(@ModelAttribute Cargo cargo, RedirectAttributes redirectAttributes) {
        try {
            // Verificar si ya existe un cargo con ese código
            if (cargo.getCodigoCar() != null && cargoRepository.existsById(cargo.getCodigoCar())) {
                redirectAttributes.addFlashAttribute("error", "Ya existe un cargo con ese código");
                return "redirect:/cargos/nuevo";
            }
            
            // Establecer fechas de auditoría
            cargo.setFechaCreacion(LocalDateTime.now());
            cargo.setFechaActualizacion(LocalDateTime.now());
            
            cargoRepository.save(cargo);
            redirectAttributes.addFlashAttribute("exito", "Cargo guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el cargo: " + e.getMessage());
        }
        return "redirect:/cargos";
    }
    
    // ============= MOSTRAR FORMULARIO EDITAR CARGO =============
    @GetMapping("/editar/{codigo}")
    public String editarCargo(@PathVariable Long codigo, Model model, RedirectAttributes redirectAttributes) {
        Optional<Cargo> cargo = cargoRepository.findById(codigo);
        if (cargo.isPresent()) {
            model.addAttribute("cargo", cargo.get());
            return "cargo/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "Cargo no encontrado");
            return "redirect:/cargos";
        }
    }
    
    // ============= ACTUALIZAR CARGO EXISTENTE =============
    @PostMapping("/actualizar/{codigo}")
    public String actualizarCargo(@PathVariable Long codigo, @ModelAttribute Cargo cargoDetails, 
                                  RedirectAttributes redirectAttributes) {
        try {
            Optional<Cargo> cargoOptional = cargoRepository.findById(codigo);
            
            if (cargoOptional.isPresent()) {
                Cargo cargo = cargoOptional.get();
                
                // Actualizar TODOS los campos
                cargo.setNombreCar(cargoDetails.getNombreCar());
                cargo.setEstadoCar(cargoDetails.getEstadoCar());
                cargo.setReportaCar(cargoDetails.getReportaCar());
                cargo.setReemplazoCar(cargoDetails.getReemplazoCar());
                cargo.setMacroprocesoCar(cargoDetails.getMacroprocesoCar());
                cargo.setCodigoSectorialCar(cargoDetails.getCodigoSectorialCar());
                cargo.setAreaCar(cargoDetails.getAreaCar());
                cargo.setProcesoCar(cargoDetails.getProcesoCar());
                cargo.setCodCargoLegajoCar(cargoDetails.getCodCargoLegajoCar());
                cargo.setSeccionCar(cargoDetails.getSeccionCar());
                cargo.setGrupoRiesgoCar(cargoDetails.getGrupoRiesgoCar());
                cargo.setMaquinariaEquipoCar(cargoDetails.getMaquinariaEquipoCar());
                cargo.setNivelRiesgoCar(cargoDetails.getNivelRiesgoCar());
                cargo.setMisionCar(cargoDetails.getMisionCar());
                cargo.setCodigoEst(cargoDetails.getCodigoEst());
                cargo.setReportaCargoCar(cargoDetails.getReportaCargoCar());
                cargo.setReemplazoCargoCar(cargoDetails.getReemplazoCargoCar());
                cargo.setSeccion1(cargoDetails.getSeccion1());
                cargo.setSeccion2(cargoDetails.getSeccion2());
                cargo.setSeccion3(cargoDetails.getSeccion3());
                cargo.setSeccion4(cargoDetails.getSeccion4());
                cargo.setSeccion5(cargoDetails.getSeccion5());
                cargo.setSeccion6(cargoDetails.getSeccion6());
                cargo.setSeccion7(cargoDetails.getSeccion7());
                cargo.setSeccion8(cargoDetails.getSeccion8());
                cargo.setNombreCargo(cargoDetails.getNombreCargo());
                
                // Actualizar fecha de modificación
                cargo.setFechaActualizacion(LocalDateTime.now());
                
                cargoRepository.save(cargo);
                redirectAttributes.addFlashAttribute("exito", "Cargo actualizado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Cargo no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el cargo: " + e.getMessage());
        }
        return "redirect:/cargos";
    }
    
    // ============= ELIMINAR CARGO =============
    @GetMapping("/eliminar/{codigo}")
    public String eliminarCargo(@PathVariable Long codigo, RedirectAttributes redirectAttributes) {
        try {
            if (cargoRepository.existsById(codigo)) {
                cargoRepository.deleteById(codigo);
                redirectAttributes.addFlashAttribute("exito", "Cargo eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Cargo no encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el cargo: " + e.getMessage());
        }
        return "redirect:/cargos";
    }
    
    // ============= BUSCAR CARGO POR NOMBRE =============
    @GetMapping("/buscar")
    public String buscarPorNombre(@RequestParam(required = false) String nombre, Model model) {
        if (nombre != null && !nombre.isEmpty()) {
            Optional<Cargo> cargo = cargoRepository.findByNombreCargo(nombre);
            if (cargo.isPresent()) {
                model.addAttribute("cargo", cargo.get());
                return "cargo/view";
            } else {
                model.addAttribute("error", "No se encontró un cargo con ese nombre");
            }
        }
        
        // Si no hay búsqueda o no se encuentra, mostrar lista con estadísticas
        List<Cargo> cargos = cargoRepository.findAll();
        model.addAttribute("cargos", cargos);
        
        // Recalcular estadísticas para la vista
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
        
        model.addAttribute("totalCargos", totalCargos);
        model.addAttribute("cargosActivos", cargosActivos);
        model.addAttribute("cargosRiesgoAlto", cargosRiesgoAlto);
        model.addAttribute("totalAreas", totalAreas);
        
        return "cargo/list";
    }
    
    // ============= VERIFICAR SI EXISTE CARGO (AJAX) =============
    @GetMapping("/verificar/{codigo}")
    @ResponseBody
    public boolean existeCargo(@PathVariable Long codigo) {
        return cargoRepository.existsById(codigo);
    }
    
    // ============= ENDPOINT PARA OBTENER ESTADÍSTICAS EN JSON =============
    @GetMapping("/estadisticas")
    @ResponseBody
    public String obtenerEstadisticas() {
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
        
        return String.format(
            "📊 ESTADÍSTICAS DE CARGOS:\n" +
            "Total de cargos: %d\n" +
            "Cargos activos: %d\n" +
            "Cargos con riesgo alto: %d\n" +
            "Áreas distintas: %d",
            totalCargos, cargosActivos, cargosRiesgoAlto, totalAreas
        );
    }
}