package com.example.demo.controller;

import com.example.demo.service.RatingService;
import com.example.demo.model.Rating;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * RatingController - Handles public rating routes
 */
@Controller
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService = new RatingService();

    /** Show all ratings + submit form */
    @GetMapping
    public String ratingsPage(
            @RequestParam(required = false) String service,
            @RequestParam(required = false) String success,
            @RequestParam(required = false) String error,
            Model model) {
        try {
            List<Rating> ratings = (service != null && !service.isBlank())
                    ? ratingService.getRatingsByService(service)
                    : ratingService.getAllRatings();

            model.addAttribute("ratings",     ratings);
            model.addAttribute("totalCount",  ratingService.getTotalCount());
            model.addAttribute("filterService", service);

            if (success != null) model.addAttribute("success", "Your rating was submitted successfully!");
            if (error   != null) model.addAttribute("error", error);
        } catch (Exception e) {
            model.addAttribute("error", "Error loading ratings: " + e.getMessage());
        }
        return "rating-list";
    }

    /** Show the submit rating form */
    @GetMapping("/submit")
    public String submitForm(Model model) {
        return "rating-form";
    }

    /** Handle rating submission */
    @PostMapping("/submit")
    public String submitRating(
            @RequestParam String userName,
            @RequestParam String userEmail,
            @RequestParam String serviceName,
            @RequestParam int    stars,
            @RequestParam String comment,
            Model model) {
        try {
            ratingService.submitRating(userName, userEmail, serviceName, stars, comment);
            return "redirect:/ratings?success=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("userName",    userName);
            model.addAttribute("userEmail",   userEmail);
            model.addAttribute("serviceName", serviceName);
            model.addAttribute("stars",       stars);
            model.addAttribute("comment",     comment);
            return "rating-form";
        }
    }
}
