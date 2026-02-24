package com.utc.proyecto1.controller;

import com.utc.proyecto1.entity.Supervisa;
import com.utc.proyecto1.entity.Cargo;
import com.utc.proyecto1.repository.SupervisaRepository;
import com.utc.proyecto1.repository.CargoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/supervisiones")
public class SupervisaController {
    
    @Autowired
    private SupervisaRepository supervisaRepository;
    
    @Autowired
    private CargoRepository cargoRepository;
    
    // ============= LISTAR TODAS LAS SUPERVISIONES =============
    @GetMapping
    public String listarSupervisiones(Model model) {
        model.addAttribute("supervisiones", supervisaRepository.findAllByOrderByFechaCreadoSupDesc());
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
    
    // ============= GUARDAR NUEVA SUPERVISIÓN =============
    @PostMapping("/guardar")
    public String guardarSupervision(@ModelAttribute Supervisa supervision, 
                                     @RequestParam(required = false) Long cargoId,
                                     RedirectAttributes redirectAttributes) {
        try {
            // Validar que el código no exista
            if (supervision.getCodigoSup() != null && supervisaRepository.existsById(supervision.getCodigoSup())) {
                redirectAttributes.addFlashAttribute("error", "Ya existe una supervisión con ese código");
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
            
            // Establecer fechas
            supervision.setFechaCreadoSup(LocalDateTime.now());
            supervision.setFechaEditadoSup(LocalDateTime.now());
            
            supervisaRepository.save(supervision);
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
    
    // ============= ACTUALIZAR SUPERVISIÓN EXISTENTE =============
    @PostMapping("/actualizar/{codigo}")
    public String actualizarSupervision(@PathVariable Long codigo, 
                                        @ModelAttribute Supervisa supervisionDetails,
                                        @RequestParam(required = false) Long cargoId,
                                        RedirectAttributes redirectAttributes) {
        try {
            Optional<Supervisa> supervisionOptional = supervisaRepository.findById(codigo);
            
            if (supervisionOptional.isPresent()) {
                Supervisa supervision = supervisionOptional.get();
                
                // Actualizar código de cargo
                supervision.setCodigoCargoSup(supervisionDetails.getCodigoCargoSup());
                
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
                
                supervisaRepository.save(supervision);
                redirectAttributes.addFlashAttribute("exito", "Supervisión actualizada exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Supervisión no encontrada");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
        }
        return "redirect:/supervisiones";
    }
    
    // ============= ELIMINAR SUPERVISIÓN =============
    @GetMapping("/eliminar/{codigo}")
    public String eliminarSupervision(@PathVariable Long codigo, RedirectAttributes redirectAttributes) {
        try {
            if (supervisaRepository.existsById(codigo)) {
                supervisaRepository.deleteById(codigo);
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
            model.addAttribute("supervisiones", supervisaRepository.findByCargoCodigoCar(cargoId));
            model.addAttribute("filtroCargo", cargo.get().getNombreCar());
            model.addAttribute("cargos", cargoRepository.findAll()); // Para el filtro
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
}