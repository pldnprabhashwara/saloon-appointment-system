package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 * AdminController - Admin Panel for User Management
 * Provides admin login, dashboard, and user management operations
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    // Hardcoded admin credentials
    private static final String ADMIN_EMAIL    = "admin@haircare.com";
    private static final String ADMIN_PASSWORD = "Admin@123";
    private static final String ADMIN_SESSION_KEY = "adminLoggedIn";

    private final UserService userService = new UserService();

    /** Show admin login page */
    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (Boolean.TRUE.equals(session.getAttribute(ADMIN_SESSION_KEY))) {
            return "redirect:/admin/dashboard";
        }
        return "admin-login";
    }

    /** Handle admin login POST */
    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        if (ADMIN_EMAIL.equals(email) && ADMIN_PASSWORD.equals(password)) {
            session.setAttribute(ADMIN_SESSION_KEY, true);
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("error", "Invalid admin credentials.");
        return "admin-login";
    }

    /** Show admin dashboard with all users */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        try {
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
        } catch (Exception e) {
            model.addAttribute("error", "Error loading users: " + e.getMessage());
        }

        return "admin-dashboard";
    }

    /** Delete a user by email */
    @PostMapping("/users/delete")
    public String deleteUser(
            @RequestParam String email,
            HttpSession session,
            Model model) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        try {
            userService.deleteUserAccount(email);
            return "redirect:/admin/dashboard?deleted=true";
        } catch (Exception e) {
            return "redirect:/admin/dashboard?error=" + e.getMessage();
        }
    }

    /** Admin logout */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(ADMIN_SESSION_KEY);
        return "redirect:/admin/login";
    }

    /** Helper: check if admin session is active */
    private boolean isAdminLoggedIn(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute(ADMIN_SESSION_KEY));
    }
}
