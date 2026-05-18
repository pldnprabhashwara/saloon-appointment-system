package com.example.demo.controller;

import com.example.demo.model.Review;
import com.example.demo.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/ratings")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public String showRatingsPage(@RequestParam(required = false) String serviceFilter, Model model) {
        List<Review> reviews;
        if (serviceFilter != null && !serviceFilter.isEmpty()) {
            reviews = reviewService.getReviewsByService(serviceFilter);
        } else {
            reviews = reviewService.getAllReviews();
        }
        
        model.addAttribute("reviews", reviews);
        model.addAttribute("totalReviews", reviews.size());
        model.addAttribute("selectedService", serviceFilter != null ? serviceFilter : "All Services");
        return "ratings";
    }

    @GetMapping("/new")
    public String showReviewForm() {
        return "review-form";
    }

    @PostMapping("/save")
    public String saveReview(@RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String serviceReceived,
                             @RequestParam int starRating,
                             @RequestParam String comment,
                             RedirectAttributes redirectAttributes) {
        try {
            Review review = new Review(name, email, serviceReceived, starRating, comment);
            reviewService.saveReview(review);
            redirectAttributes.addFlashAttribute("successMessage", "Your rating was submitted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to submit rating: " + e.getMessage());
        }
        return "redirect:/ratings";
    }
}
