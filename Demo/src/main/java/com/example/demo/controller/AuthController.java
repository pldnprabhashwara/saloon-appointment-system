package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

/**
 * AuthController - Authentication and User Profile Management
 * Handles registration, login, profile view, update, and delete operations
 */
@Controller
@RequestMapping("/auth")
public class AuthController {
    
    private final UserService userService = new UserService();
    
    /**
     * Handle user registration
     * POST request from registration form
     */
    @PostMapping("/register")
    public String register(
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        
        try {
            // Register user via service
            boolean registered = userService.registerUser(name, phone, email, password);
            
            if (registered) {
                // Create user object for session
                User user = userService.getUserByEmail(email);
                session.setAttribute("loggedInUser", user);
                
                return "redirect:/auth/profile";
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "user-register";
        }
        
        model.addAttribute("error", "Registration failed. Please try again.");
        return "user-register";
    }
    
    /**
     * Handle user login
     * POST request from login form
     */
    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        
        try {
            // Authenticate user via service
            User user = userService.loginUser(email, password);
            
            if (user != null) {
                session.setAttribute("loggedInUser", user);
                return "redirect:/auth/profile";
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "user-form";
        }
        
        model.addAttribute("error", "Login failed. Invalid email or password.");
        return "user-form";
    }
    
    /**
     * Display user profile
     * GET request to view profile
     */
    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        
        if (loggedInUser == null) {
            return "redirect:/user-form";
        }
        
        model.addAttribute("user", loggedInUser);
        return "user-profile";
    }
    
    /**
     * Display profile update form
     * GET request to show update form
     */
    @GetMapping("/profile/edit")
    public String editProfile(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        
        if (loggedInUser == null) {
            return "redirect:/user-form";
        }
        
        model.addAttribute("user", loggedInUser);
        return "user-profile-edit";
    }
    
    /**
     * Handle profile update
     * POST request to update user profile
     */
    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String password,
            HttpSession session,
            Model model) {
        
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        
        if (loggedInUser == null) {
            return "redirect:/user-form";
        }
        
        try {
            // Update user via service
            userService.updateUserProfile(loggedInUser.getEmail(), name, phone, password);
            
            // Refresh user data from file
            User updatedUser = userService.getUserByEmail(loggedInUser.getEmail());
            session.setAttribute("loggedInUser", updatedUser);
            
            model.addAttribute("success", "Profile updated successfully!");
            model.addAttribute("user", updatedUser);
            return "user-profile";
        } catch (Exception e) {
            model.addAttribute("error", "Error updating profile: " + e.getMessage());
            model.addAttribute("user", loggedInUser);
            return "user-profile-edit";
        }
    }
    
    /**
     * Handle profile deletion
     * POST request to delete user account
     */
    @PostMapping("/profile/delete")
    public String deleteProfile(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        
        if (loggedInUser == null) {
            return "redirect:/user-form";
        }
        
        try {
            // Delete user via service
            userService.deleteUserAccount(loggedInUser.getEmail());
            
            // Clear session
            session.invalidate();
            
            model.addAttribute("message", "Your account has been deleted successfully.");
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", "Error deleting account: " + e.getMessage());
            model.addAttribute("user", loggedInUser);
            return "user-profile";
        }
    }
    
    /**
     * Handle logout
     * GET request to logout user
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
