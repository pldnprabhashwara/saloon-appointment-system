package com.example.demo.controller;

import com.example.demo.model.Stylist;
import com.example.demo.service.StylistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Stylist interface and CRUD operations.
 */
@Controller
@RequestMapping("/stylist")
public class StylistController {
    private final StylistService stylistService = new StylistService();

    @GetMapping
    public String viewStylists(Model model) {
        try {
            model.addAttribute("stylists", stylistService.getAllStylists());
            model.addAttribute("stylist", new Stylist());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "stylist";
    }

    @PostMapping("/add")
    public String addStylist(@RequestParam String stylistId,
                             @RequestParam String stylistName,
                             @RequestParam String specialization,
                             @RequestParam(required = false) boolean availability,
                             Model model) {
        try {
            stylistService.addStylist(stylistId, stylistName, specialization, availability);
            return "redirect:/stylist?success=added";
        } catch (Exception e) {
            loadStylistPage(model, e.getMessage(), null);
            return "stylist";
        }
    }

    @GetMapping("/edit/{stylistId}")
    public String editStylist(@PathVariable String stylistId, Model model) {
        try {
            model.addAttribute("editStylist", stylistService.getStylistById(stylistId));
            model.addAttribute("stylists", stylistService.getAllStylists());
        } catch (Exception e) {
            loadStylistPage(model, e.getMessage(), null);
        }
        return "stylist";
    }

    @PostMapping("/update")
    public String updateStylist(@RequestParam String stylistId,
                                @RequestParam String stylistName,
                                @RequestParam String specialization,
                                @RequestParam(required = false) boolean availability,
                                Model model) {
        try {
            stylistService.updateStylist(stylistId, stylistName, specialization, availability);
            return "redirect:/stylist?success=updated";
        } catch (Exception e) {
            loadStylistPage(model, e.getMessage(), null);
            return "stylist";
        }
    }

    @PostMapping("/availability")
    public String changeAvailability(@RequestParam String stylistId,
                                     @RequestParam boolean availability) {
        try {
            stylistService.updateAvailability(stylistId, availability);
        } catch (Exception ignored) {
        }
        return "redirect:/stylist";
    }

    @PostMapping("/delete/{stylistId}")
    public String deleteStylist(@PathVariable String stylistId) {
        try {
            stylistService.deleteStylist(stylistId);
        } catch (Exception ignored) {
        }
        return "redirect:/stylist?success=deleted";
    }

    private void loadStylistPage(Model model, String error, String success) {
        try {
            model.addAttribute("stylists", stylistService.getAllStylists());
        } catch (Exception ignored) {
        }
        model.addAttribute("error", error);
        model.addAttribute("success", success);
    }
}
