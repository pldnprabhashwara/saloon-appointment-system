package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user-list")
    public String listUsers(Model model) {
        try {
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
        } catch (Exception e) {
            model.addAttribute("error", "Could not fetch users: " + e.getMessage());
        }
        return "user-list";
    }

    @GetMapping("/user-view/{id:.+}")
    public String viewUser(@PathVariable("id") String email, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserByEmail(email);
            model.addAttribute("user", user);
            return "user-view";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found");
            return "redirect:/user-list";
        }
    }

    @GetMapping("/user-edit/{id:.+}")
    public String editUserForm(@PathVariable("id") String email, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserByEmail(email);
            model.addAttribute("user", user);
            return "user-edit-admin";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found");
            return "redirect:/user-list";
        }
    }

    @PostMapping("/user-update")
    public String updateUser(
            @RequestParam("email") String email,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "password", required = false) String password,
            RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserProfile(email, name, phone, password);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update user: " + e.getMessage());
        }
        return "redirect:/user-list";
    }

    @RequestMapping(value = "/user-delete/{id:.+}", method = {RequestMethod.GET, RequestMethod.POST})
    public String deleteUser(@PathVariable("id") String email, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUserAccount(email);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete user: " + e.getMessage());
        }
        return "redirect:/user-list";
    }
}
