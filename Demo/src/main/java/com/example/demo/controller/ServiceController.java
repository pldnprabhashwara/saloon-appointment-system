package com.example.demo.controller;

import com.example.demo.model.Service;
import com.example.demo.service.ServiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * ServiceController - Web Layer (Spring MVC)
 * Handles HTTP requests for Salon Service CRUD operations
 * Routes:
 *   GET  /services            -> view all services
 *   GET  /services/add        -> show add form
 *   POST /services/add        -> save new service
 *   GET  /services/edit/{id}  -> show edit form
 *   POST /services/edit/{id}  -> update service
 *   POST /services/delete/{id}-> delete service
 *   GET  /services/search     -> search services
 */
@Controller
@RequestMapping("/services")
public class ServiceController {

    private final ServiceService serviceService = new ServiceService();

    // ==================== READ - List all services ====================

    /**
     * View all services (Admin view)
     */
    @GetMapping
    public String listServices(Model model,
                               @RequestParam(required = false) String category,
                               @RequestParam(required = false) String search) {
        try {
            List<Service> services;

            if (search != null && !search.trim().isEmpty()) {
                services = serviceService.searchServices(search);
                model.addAttribute("search", search);
            } else if (category != null && !category.trim().isEmpty()) {
                services = serviceService.getServicesByCategory(category);
                model.addAttribute("selectedCategory", category);
            } else {
                services = serviceService.getAllServices();
            }

            model.addAttribute("services", services);
            model.addAttribute("totalCount", services.size());
        } catch (Exception e) {
            model.addAttribute("error", "Error loading services: " + e.getMessage());
            model.addAttribute("services", List.of());
        }
        return "service-list";
    }

    // ==================== CREATE - Add new service ====================

    /**
     * Show add service form
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("pageTitle", "Add New Service");
        return "service-add";
    }

    /**
     * Handle add service form submission
     */
    @PostMapping("/add")
    public String addService(@RequestParam String serviceName,
                             @RequestParam String category,
                             @RequestParam double price,
                             @RequestParam int durationMinutes,
                             @RequestParam(required = false, defaultValue = "") String description,
                             RedirectAttributes redirectAttributes) {
        try {
            serviceService.addService(serviceName, category, price, durationMinutes, description);
            redirectAttributes.addFlashAttribute("success",
                "Service '" + serviceName + "' added successfully!");
            return "redirect:/services";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/services/add";
        }
    }

    // ==================== UPDATE - Edit service ====================

    /**
     * Show edit service form
     */
    @GetMapping("/edit/{serviceId}")
    public String showEditForm(@PathVariable String serviceId, Model model) {
        try {
            Service service = serviceService.getServiceById(serviceId);
            model.addAttribute("service", service);
            model.addAttribute("pageTitle", "Edit Service");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "service-edit";
    }

    /**
     * Handle edit service form submission
     */
    @PostMapping("/edit/{serviceId}")
    public String updateService(@PathVariable String serviceId,
                                @RequestParam String serviceName,
                                @RequestParam String category,
                                @RequestParam double price,
                                @RequestParam int durationMinutes,
                                @RequestParam(required = false, defaultValue = "") String description,
                                RedirectAttributes redirectAttributes) {
        try {
            serviceService.updateService(serviceId, serviceName, category,
                                         price, durationMinutes, description);
            redirectAttributes.addFlashAttribute("success", "Service updated successfully!");
            return "redirect:/services";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/services/edit/" + serviceId;
        }
    }

    // ==================== DELETE ====================

    /**
     * Delete a service
     */
    @PostMapping("/delete/{serviceId}")
    public String deleteService(@PathVariable String serviceId,
                                RedirectAttributes redirectAttributes) {
        try {
            serviceService.deleteService(serviceId);
            redirectAttributes.addFlashAttribute("success", "Service deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/services";
    }

    /**
     * Deactivate (soft delete) a service
     */
    @PostMapping("/deactivate/{serviceId}")
    public String deactivateService(@PathVariable String serviceId,
                                    RedirectAttributes redirectAttributes) {
        try {
            serviceService.deactivateService(serviceId);
            redirectAttributes.addFlashAttribute("success", "Service deactivated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/services";
    }
}
