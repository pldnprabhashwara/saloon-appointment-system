package com.example.demo.controller;

import com.example.demo.service.RatingService;
import com.example.demo.model.Rating;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 * RatingAdminController - Admin panel for managing ratings
 */
@Controller
@RequestMapping("/admin")
public class RatingAdminController {

    private static final String ADMIN_EMAIL       = "admin@haircare.com";
    private static final String ADMIN_PASSWORD    = "Admin@123";
    private static final String ADMIN_SESSION_KEY = "adminLoggedIn";

    private final RatingService ratingService = new RatingService();

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (Boolean.TRUE.equals(session.getAttribute(ADMIN_SESSION_KEY)))
            return "redirect:/admin/dashboard";
        return "admin-login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password,
                        HttpSession session, Model model) {
        if (ADMIN_EMAIL.equals(email) && ADMIN_PASSWORD.equals(password)) {
            session.setAttribute(ADMIN_SESSION_KEY, true);
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("error", "Invalid admin credentials.");
        return "admin-login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        try {
            List<Rating> ratings = ratingService.getAllRatings();
            model.addAttribute("ratings",    ratings);
            model.addAttribute("totalCount", ratings.size());
        } catch (Exception e) {
            model.addAttribute("error", "Error loading ratings: " + e.getMessage());
        }
        return "admin-dashboard";
    }

    @PostMapping("/ratings/delete")
    public String deleteRating(@RequestParam String id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        try {
            ratingService.deleteRating(id);
            return "redirect:/admin/dashboard?deleted=true";
        } catch (Exception e) {
            return "redirect:/admin/dashboard?error=" + e.getMessage();
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(ADMIN_SESSION_KEY);
        return "redirect:/admin/login";
    }

    private boolean isAdmin(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute(ADMIN_SESSION_KEY));
    }
}
