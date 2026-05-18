package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // Hardcoded admin credentials exactly as in the friend's project
    private static final String ADMIN_EMAIL = "saloon@admin.com";
    private static final String ADMIN_PASSWORD = "Saloon@123";
    private static final String ADMIN_SESSION_KEY = "adminLoggedIn";

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    /** Show admin login page */
    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (Boolean.TRUE.equals(session.getAttribute(ADMIN_SESSION_KEY))) {
            return "redirect:/admin/dashboard";
        }
        return "admin-login";
    }

    /** Process admin login */
    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {
        if (ADMIN_EMAIL.equals(email) && ADMIN_PASSWORD.equals(password)) {
            session.setAttribute(ADMIN_SESSION_KEY, true);
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("error", "Invalid email or password");
        return "admin-login";
    }

    /** Show admin dashboard */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!Boolean.TRUE.equals(session.getAttribute(ADMIN_SESSION_KEY))) {
            return "redirect:/admin/login";
        }

        try {
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            model.addAttribute("totalUsers", users.size());
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load users: " + e.getMessage());
        }

        return "admin-dashboard";
    }

    /** Delete user */
    @PostMapping("/delete/{id:.+}")
    public String deleteUser(@PathVariable("id") String email, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!Boolean.TRUE.equals(session.getAttribute(ADMIN_SESSION_KEY))) {
            return "redirect:/admin/login";
        }

        try {
            userService.deleteUserAccount(email);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete user: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    /** Admin logout */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(ADMIN_SESSION_KEY);
        return "redirect:/admin/login";
    }
}
