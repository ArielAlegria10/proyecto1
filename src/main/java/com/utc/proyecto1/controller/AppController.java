package com.utc.proyecto1.controller;

import com.utc.proyecto1.entity.Cargo;
import com.utc.proyecto1.entity.Supervisa;
import com.utc.proyecto1.repository.CargoRepository;
import com.utc.proyecto1.repository.SupervisaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller // ¡IMPORTANTE! Usa @Controller, no @RestController
public class AppController {
    
    @Autowired
    private CargoRepository cargoRepository;
    
    @Autowired
    private SupervisaRepository supervisaRepository;
    
    // ============= CARGO =============
    
    @GetMapping("/cargo")
    public String listCargos(Model model) {
        model.addAttribute("cargos", cargoRepository.findAll());
        return "cargo/list"; // Busca en templates/cargo/list.html
    }
    
    @GetMapping("/cargo/nuevo")
    public String nuevoCargo(Model model) {
        model.addAttribute("cargo", new Cargo());
        return "cargo/form";
    }
    
    @PostMapping("/cargo/guardar")
    public String guardarCargo(@ModelAttribute Cargo cargo, RedirectAttributes redirectAttributes) {
        try {
            cargoRepository.save(cargo);
            redirectAttributes.addFlashAttribute("success", "Cargo guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el cargo");
        }
        return "redirect:/cargo";
    }
    
    @GetMapping("/cargo/editar/{codigo}")
    public String editarCargo(@PathVariable Long codigo, Model model) {
        Optional<Cargo> cargo = cargoRepository.findById(codigo);
        if (cargo.isPresent()) {
            model.addAttribute("cargo", cargo.get());
            return "cargo/form";
        }
        return "redirect:/cargo";
    }
    
    @GetMapping("/cargo/eliminar/{codigo}")
    public String eliminarCargo(@PathVariable Long codigo, RedirectAttributes redirectAttributes) {
        try {
            cargoRepository.deleteById(codigo);
            redirectAttributes.addFlashAttribute("success", "Cargo eliminado");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar");
        }
        return "redirect:/cargo";
    }
    
    // ============= SUPERVISA =============
    
    @GetMapping("/supervisa")
    public String listSupervisas(Model model) {
        model.addAttribute("supervisas", supervisaRepository.findAll());
        return "supervisa/list";
    }
    
    @GetMapping("/supervisa/nuevo")
    public String nuevoSupervisa(Model model) {
        model.addAttribute("supervisa", new Supervisa());
        model.addAttribute("cargos", cargoRepository.findAll());
        return "supervisa/form";
    }
    
    @PostMapping("/supervisa/guardar")
    public String guardarSupervisa(@ModelAttribute Supervisa supervisa, RedirectAttributes redirectAttributes) {
        try {
            if (supervisa.getCodigoSup() == null) {
                supervisa.setFechaCreadoSup(LocalDateTime.now());
            }
            supervisa.setFechaEditadoSup(LocalDateTime.now());
            supervisaRepository.save(supervisa);
            redirectAttributes.addFlashAttribute("success", "Supervisión guardada");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar");
        }
        return "redirect:/supervisa";
    }
}