package com.example.demo.controller;

import com.example.demo.exception.AppointmentException;
import com.example.demo.exception.AppointmentNotFoundException;
import com.example.demo.model.Appointment;
import com.example.demo.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * AppointmentController - HTTP Web Layer
 * Handles routing, parameter extraction, and template rendering.
 */
@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    /**
     * Dependency Injection of the business logic service.
     */
    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public String listAppointments(Model model) {
        try {
            List<Appointment> appointments = appointmentService.getAppointments();
            model.addAttribute("appointments", appointments);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load appointments: " + e.getMessage());
        }
        return "appointment-list";
    }

    @GetMapping("/book")
    public String showBookingForm(Model model) {
        return "appointment-book";
    }

    @PostMapping("/save")
    public String saveAppointment(
            @RequestParam String type,
            @RequestParam String customerName,
            @RequestParam String customerPhone,
            @RequestParam String serviceType,
            @RequestParam String stylistName,
            @RequestParam String date,
            @RequestParam String time,
            @RequestParam(required = false) String vipPerk,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            LocalDate apptDate = LocalDate.parse(date);
            LocalTime apptTime = LocalTime.parse(time);
            
            Appointment appointment = appointmentService.createAppointment(type, customerName, customerPhone, serviceType, stylistName, apptDate, apptTime, vipPerk);
            if (appointment != null) {
                return "redirect:/appointments/confirmation/" + appointment.getAppointmentId();
            }
        } catch (AppointmentException e) {
            model.addAttribute("error", e.getMessage());
            return "appointment-book";
        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            return "appointment-book";
        }
        model.addAttribute("error", "Booking failed. Please try again.");
        return "appointment-book";
    }

    @GetMapping("/confirmation/{id}")
    public String showConfirmation(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            model.addAttribute("appointment", appointment);
            return "appointment-success";
        } catch (AppointmentNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Appointment not found.");
            return "redirect:/appointments/book";
        }
    }

    @GetMapping("/search")
    public String searchAppointments(@RequestParam(required = false) String query, Model model) {
        try {
            List<Appointment> appointments = appointmentService.searchAppointment(query);
            model.addAttribute("appointments", appointments);
            model.addAttribute("query", query);
        } catch (Exception e) {
            model.addAttribute("error", "Search failed: " + e.getMessage());
        }
        return "appointment-search";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            model.addAttribute("appointment", appointment);
            return "appointment-edit";
        } catch (AppointmentNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/appointments";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error fetching appointment.");
            return "redirect:/appointments";
        }
    }

    @PostMapping("/update")
    public String updateAppointment(
            @RequestParam String id,
            @RequestParam String serviceType,
            @RequestParam String stylistName,
            @RequestParam String date,
            @RequestParam String time,
            @RequestParam String status,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            appointmentService.updateAppointment(id, serviceType, stylistName, date, time, status);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment updated successfully!");
            return "redirect:/appointments";
        } catch (AppointmentException e) {
            model.addAttribute("error", e.getMessage());
            try {
                model.addAttribute("appointment", appointmentService.getAppointmentById(id));
            } catch (Exception ex) {
                // Ignore
            }
            return "appointment-edit";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update appointment: " + e.getMessage());
            try {
                model.addAttribute("appointment", appointmentService.getAppointmentById(id));
            } catch (Exception ex) {
                // Ignore
            }
            return "appointment-edit";
        }
    }

    @RequestMapping(value = {"/delete/{id}", "/delete"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String deleteAppointment(@PathVariable(value = "id", required = false) String pathId, 
                                   @RequestParam(value = "id", required = false) String paramId, 
                                   RedirectAttributes redirectAttributes) {
        try {
            String id = (pathId != null) ? pathId : paramId;
            if (id == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Invalid appointment ID.");
                return "redirect:/appointments";
            }
            
            boolean deleted = appointmentService.deleteAppointment(id);
            if (deleted) {
                redirectAttributes.addFlashAttribute("successMessage", "Appointment deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Appointment not found or already deleted.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting appointment: " + e.getMessage());
        }
        return "redirect:/appointments";
    }

    @GetMapping("/booked-times")
    @ResponseBody
    public List<LocalTime> getBookedTimes(@RequestParam String date, @RequestParam String stylist) {
        try {
            return appointmentService.getBookedTimes(LocalDate.parse(date), stylist);
        } catch (Exception e) {
            return List.of();
        }
    }
}
